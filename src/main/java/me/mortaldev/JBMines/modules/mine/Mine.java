package me.mortaldev.JBMines.modules.mine;

import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.listeners.OnRightClickBlockEvent;
import me.mortaldev.JBMines.modules.ChanceMap;
import me.mortaldev.JBMines.modules.chamber.Chamber;
import me.mortaldev.JBMines.modules.chamber.ChamberManager;
import me.mortaldev.JBMines.modules.reset.Combo;
import me.mortaldev.JBMines.modules.reset.Percent;
import me.mortaldev.JBMines.modules.reset.ResetType;
import me.mortaldev.JBMines.modules.reset.Timer;
import me.mortaldev.JBMines.records.Pair;
import me.mortaldev.JBMines.utils.ItemStackHelper;
import me.mortaldev.JBMines.utils.TextUtil;
import me.mortaldev.JBMines.utils.Utils;
import me.mortaldev.crudapi.CRUD;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Mine implements CRUD.Identifiable {
  private final String id;
  private Map<String, Object> mineSpawn = new HashMap<>();
  private Map<String, Object> cornerOne = new HashMap<>();
  private Map<String, Object> cornerTwo = new HashMap<>();
  private ChanceMap<Material> blockPalette = new ChanceMap<>();
  private ResetType resetType = new Timer(20);
  private String chamberID;
  private int totalSize;
  private transient int blocksLeft;

  public Mine(String id) {
    this.id = TextUtil.fileFormat(id);
    addBlockToPalette(new ItemStack(Material.COBBLESTONE));
  }

  public ResetType getResetType() {
    return resetType;
  }

  public Optional<Chamber> getChamber() {
    return ChamberManager.INSTANCE.getChamber(chamberID);
  }

  public void setChamber(String chamberID) {
    this.chamberID = chamberID;
  }

  public Location getMineSpawn() {
    return Location.deserialize(mineSpawn);
  }

  public void setMineSpawn(Location mineSpawn) {
    this.mineSpawn = mineSpawn.serialize();
  }

  public Location getCornerOne() {
    return Location.deserialize(cornerOne);
  }

  public void setCornerOne(Location location) {
    this.cornerOne = location.serialize();
  }

  public Location getCornerTwo() {
    return Location.deserialize(cornerTwo);
  }

  public void setCornerTwo(Location location) {
    this.cornerTwo = location.serialize();
  }

  public ChanceMap<Material> getBlockPaletteRaw() {
    return blockPalette;
  }

  public void setBlockPaletteRaw(ChanceMap<Material> blockPalette) {
    this.blockPalette = blockPalette;
  }

  public LinkedHashMap<Material, BigDecimal> getBlockPalette() {
    this.blockPalette.sort();
    return blockPalette.getTable();
  }

  public void setBlockPalette(LinkedHashMap<Material, BigDecimal> blockPalette) {
    this.blockPalette.setTable(blockPalette);
    this.blockPalette.sort();
  }

  public boolean addBlockToPalette(ItemStack itemStack) {
    if (!itemStack.getType().isBlock()) {
      return false;
    }
    BigDecimal percent;
    if (blockPalette.getTable().isEmpty()) {
      percent = new BigDecimal("100");
    } else {
      BigDecimal size = new BigDecimal(blockPalette.size());
      BigDecimal hundred = new BigDecimal(100);
      percent = hundred.divide(size, 2, RoundingMode.HALF_UP);
    }
    blockPalette.put(itemStack.getType(), percent, true);
    return true;
  }

  public void removeBlockFromPalette(ItemStack itemStack) {
    removeBlockFromPalette(itemStack.getType());
  }

  public void removeBlockFromPalette(Material material) {
    blockPalette.remove(material, true);
  }

  private Material getWinningMaterial() {
    double generatedNumber = ThreadLocalRandom.current().nextDouble(0.00, 100.00);
    getBlockPaletteRaw().sort();
    LinkedHashMap<Material, BigDecimal> reversedBlockPalette = Utils.reverseMap(getBlockPalette());
    BigDecimal total = BigDecimal.ZERO;
    for (Map.Entry<Material, BigDecimal> entry : reversedBlockPalette.entrySet()) {
      total = total.add(entry.getValue());
      if (BigDecimal.valueOf(generatedNumber).compareTo(total) < 0) {
        return entry.getKey();
      }
    }
    return Material.AIR;
  }

  private Location getMineCenter(){
    return getCornerOne().add(getCornerTwo()).multiply(0.5);
  }

  public void getPlayersInMine(int extraRadius, Consumer<Player> consumer) {
    double distance = getCornerOne().distance(getMineCenter()) + extraRadius;
    for (Player player : getCornerOne().getWorld().getPlayers()) {
      if (player.getLocation().distance(getMineCenter()) > distance) {
        continue;
      }
      if (Utils.locationIsWithin(player.getLocation(), getCornerOne(), getCornerTwo())) {
        consumer.accept(player);
      }
    }
  }

  public void reset() {
    World world = getCornerOne().getWorld();
    getPlayersInMine(0, (player) -> {
      Location mineSpawn = getMineSpawn();
      if (mineSpawn.getWorld() != null) {
        player.teleport(mineSpawn);
      }
    });
    getMineBlocks(
        (vector ->
            Bukkit.getScheduler()
                .scheduleSyncDelayedTask(
                    Main.getInstance(),
                    () -> {
                      Block block =
                          world.getBlockAt(
                              vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
                      Material material = getWinningMaterial();
                      BlockData newBlockData = Bukkit.getServer().createBlockData(material);
                      if (block.getType() != material) {
                        block.setBlockData(newBlockData, false);
                      }
                    })));
    blocksLeft = totalSize;
    Main.log("Reset " + this.id);
    resetType.start(this);
  }

  public void configureCorners(Player player) {
    player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
    player.sendMessage("");
    player.sendMessage(TextUtil.format("&6&lConfiguring Corners..."));
    player.sendMessage(TextUtil.format("&eRight-Click a block to set Corner 1."));
    player.sendMessage(TextUtil.format("&c[Click to Cancel]##cmd:/minegui cancelconfig", true));
    OnRightClickBlockEvent.addUser(player.getUniqueId(), new Pair<>(1, this));
  }

  // Operation Methods

  public void getMineBlocks(Consumer<Vector> action) {
    Vector difference =
        getCornerOne().clone().subtract(getCornerTwo()).toBlockLocation().toVector();
    int xDirection = (int) Math.signum(-difference.getX());
    int yDirection = (int) Math.signum(-difference.getY());
    int zDirection = (int) Math.signum(-difference.getZ());
    for (int x = getCornerOne().getBlockX();
        x != getCornerTwo().getBlockX() + xDirection;
        x += xDirection) {
      for (int y = getCornerOne().getBlockY();
          y != getCornerTwo().getBlockY() + yDirection;
          y += yDirection) {
        for (int z = getCornerOne().getBlockZ();
            z != getCornerTwo().getBlockZ() + zDirection;
            z += zDirection) {
          action.accept(new Vector(x, y, z));
        }
      }
    }
  }

  public int getTotalSize() {
    return totalSize;
  }

  public void updateTotalSize() {
    totalSize = getCalculatedSize();
  }

  private int getCalculatedSize() {
    Vector difference =
        getCornerOne().clone().subtract(getCornerTwo()).toBlockLocation().toVector();
    return (Math.abs(difference.getBlockX()) + 1)
        * (Math.abs(difference.getBlockY()) + 1)
        * (Math.abs(difference.getBlockZ()) + 1);
  }

  public int getTypeAmount(Material material) {
    World world = getCornerOne().getWorld();
    AtomicInteger count = new AtomicInteger();
    getMineBlocks(
        (vector) -> {
          if (world
              .getType(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ())
              .equals(material)) {
            count.getAndIncrement();
          }
        });
    return count.get();
  }

  // Display Methods

  public ItemStack getDisplayItemStack() {
    ItemStack topItem;
    if (blockPalette.getTable().isEmpty()) {
      topItem = new ItemStack(Material.COBBLESTONE);
    } else {
      topItem = new ItemStack(blockPalette.getTable().keySet().iterator().next());
    }
    return ItemStackHelper.builder(topItem.getType())
        .name("&e&l" + id + " Mine")
        .addLore(getDisplayLore())
        .addLore("")
        .addLore("&7[Left-Click to Modify]")
        .addLore("&7[Middle-Click to Reset]")
        .addLore("&7[Right-Click to Delete]")
        .build();
  }

  public List<String> getDisplayLore() {
    int percentBlocksLeft = 100;
    if (getTotalSize() > 0) {
      percentBlocksLeft = (1 - (blocksLeft / getTotalSize())) * 100;
    }
    int finalPercentBlocksLeft = percentBlocksLeft;
    return new ArrayList<>() {
      {
        add("&fID:&7 " + id);
        if (resetType instanceof Timer timer) {
          add("&fResets in:&7 " + timer.getTimeLeft().getSeconds() + "s");
        } else if (resetType instanceof Percent percent) {
          add("&fResets at:&7 " + percent.getResetPercentage() + "%");
        } else if(resetType instanceof Combo combo){
          add("&fResets in:&7 " + combo.getTimer().getTimeLeft().getSeconds() + "s");
          add("&fResets at:&7 " + combo.getPercent().getResetPercentage() + "%");
        }
        add("&fBlocks Left:&7 " + finalPercentBlocksLeft + "%");
        if (!blockPalette.getTable().isEmpty()) {
          add("");
          getBlockPalette()
              .forEach((key, value) -> add("&f - &e" + value + "% &f" + Utils.itemName(key)));
        }
      }
    };
  }

  // CRUD Methods

  public void save() {
    MineCRUD.getInstance().saveData(this);
  }

  public void delete() {
    MineCRUD.getInstance().deleteData(this);
  }

  @Override
  public String getID() {
    return id;
  }
}
