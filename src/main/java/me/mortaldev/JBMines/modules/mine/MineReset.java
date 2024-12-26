package me.mortaldev.JBMines.modules.mine;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.utils.TextUtil;
import me.mortaldev.JBMines.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

public abstract class MineReset {
  private static final int BATCH_SIZE = 10; // This will be a config value in the future
  private static final int COUNTDOWN_START = 10; // This will be a config value in the future
  private transient AtomicInteger countdown;
  private transient HashMap<String, Integer> scheduler = new HashMap<>();

  abstract Mine getMine();

  public boolean isResetting() {
    if (scheduler != null) {
      return scheduler.containsKey("countdownToReset");
    }
    return false;
  }

  public Integer getCountdown() {
    return countdown.get();
  }

  public void killCountdown() {
    if (scheduler != null) {
      scheduler.values().forEach(Bukkit.getScheduler()::cancelTask);
      scheduler.clear();
    }
  }

  public boolean canBeReset(Mine mine) {
    return mine.getCornerOne().getWorld() != null
        && mine.getCornerTwo().getWorld() != null
        && !mine.getBlockPalette().isEmpty()
        && mine.getMineSpawn().getWorld() != null;
  }

  public void countdownToReset() {
    if (scheduler == null) {
      scheduler = new HashMap<>();
    }
    countdown = new AtomicInteger(COUNTDOWN_START);
    Mine mine = getMine();
    if (mine == null) {
      throw new IllegalStateException("mine is null");
    }
    scheduler.computeIfAbsent(
        "countdownToReset",
        k ->
            Bukkit.getScheduler()
                .scheduleSyncRepeatingTask(
                    Main.getInstance(),
                    () -> {
                      int countdownValue = countdown.getAndDecrement();
                      if (countdownValue <= 0) {
                        mine.getPlayersInMine(
                            0,
                            player ->
                                player.sendMessage(
                                    TextUtil.format(
                                        "&e&l"
                                            + mine.getDisplayName()
                                            + " Mine&e has been reset.")));
                        Bukkit.getScheduler().cancelTask(scheduler.get("countdownToReset"));
                        mine.reset();
                      }
                      if (countdownValue == 10 || countdownValue <= 5) {
                        mine.getPlayersInMine(
                            0,
                            player ->
                                player.sendMessage(
                                    TextUtil.format(
                                        "&eResetting in &l" + countdownValue + "&e seconds!")));
                      }
                    },
                    0,
                    20L));
  }

  public Material getWinningMaterial(Mine mine) {
    double generatedNumber = ThreadLocalRandom.current().nextDouble(0.00, 100.00);
    mine.getBlockPaletteRaw().sort();
    LinkedHashMap<Material, BigDecimal> reversedBlockPalette =
        Utils.reverseMap(mine.getBlockPaletteRaw().getTable());
    BigDecimal total = BigDecimal.ZERO;
    for (Map.Entry<Material, BigDecimal> entry : reversedBlockPalette.entrySet()) {
      total = total.add(entry.getValue());
      if (BigDecimal.valueOf(generatedNumber).compareTo(total) < 0) {
        return entry.getKey();
      }
    }
    return Material.AIR;
  }

  public void reset() {
    Mine mine = getMine();
    if (!canBeReset(mine)) {
      return;
    }
    if (scheduler != null && !scheduler.isEmpty()) {
      scheduler.values().forEach(Bukkit.getScheduler()::cancelTask);
      scheduler.clear();
    }
    Instant before = Instant.now();
    World world = mine.getCornerOne().getWorld();
    mine.getPlayersInMine(
        0,
        (player) -> {
          Location mineSpawn = mine.getMineSpawn();
          if (mineSpawn.getWorld() != null) {
            player.teleport(mineSpawn);
          }
        });
    // Process the blocks in batches to reduce lag
    // Batch size is controlled by an external config
    List<Vector> blockList = new ArrayList<>();
    mine.getMineBlocks(blockList::add);

    int batches = (int) Math.ceil((double) blockList.size() / BATCH_SIZE);
    for (int i = 0; i < batches; i++) {
      int start = i * BATCH_SIZE;
      int end = Math.min(start + BATCH_SIZE, blockList.size());
      List<Vector> batch = blockList.subList(start, end);

      Bukkit.getScheduler()
          .scheduleSyncDelayedTask(
              Main.getInstance(),
              () -> {
                for (Vector vector : batch) {
                  Block block =
                      world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
                  Material material = getWinningMaterial(mine);
                  BlockData newBlockData = Bukkit.getServer().createBlockData(material);
                  if (block.getType() != material) {
                    block.setBlockData(newBlockData, false);
                  }
                }
              });
    }
    mine.resetBlocksLeft();
    Duration duration = Duration.between(before, Instant.now());
    String durationString = String.format("%.2f", duration.toMillis() / 1000.0);
    Main.log(
        "Reset "
            + mine.getDisplayName()
            + " ["
            + mine.getID()
            + "] - Took "
            + durationString
            + "s");
    mine.getResetType().start(mine);
  }
}
