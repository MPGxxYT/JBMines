package me.mortaldev.JBMines.modules.reset;

import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.modules.mine.Mine;
import me.mortaldev.JBMines.utils.TextUtil;
import org.bukkit.Bukkit;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Timer extends ResetType {
  public static final int COUNTDOWN_START = 5;
  private final int lengthOfTime;
  private transient Instant endTime;
  private transient HashMap<String, Integer> scheduler;

  public Timer(int lengthOfTime) {
    this.lengthOfTime = lengthOfTime;
    this.scheduler = new HashMap<>();
  }

  @Override
  public boolean resetCheck(Mine mine) {
    return Instant.now().compareTo(endTime) > 0;
  }

  public Duration getTimeLeft() {
    if (endTime == null) {
      return Duration.ZERO;
    }
    Duration between = Duration.between(Instant.now(), endTime);
    if (between == null) {
      return Duration.ZERO;
    }
    return between;
  }

  public int getLengthOfTime() {
    return lengthOfTime;
  }

  private void countdownToReset(Mine mine) {
    if (scheduler.containsKey("countdownToReset")) {
      return;
    }
    AtomicInteger count = new AtomicInteger(COUNTDOWN_START);
    scheduler.put(
        "countdownToReset",
        Bukkit.getScheduler()
            .scheduleSyncRepeatingTask(
                Main.getInstance(),
                () -> {
                  int countdownValue = count.getAndDecrement();
                  if (countdownValue <= 0) {
                    return;
                  }
                  mine.getPlayersInMine(
                      0,
                      (player) -> {
                        player.sendMessage(
                            TextUtil.format("&eResetting in &l" + countdownValue + "&e seconds!"));
                      });
                },
                0,
                20L));
  }

  @Override
  public void start(Mine mine) {
    endTime = Instant.now().plusSeconds(lengthOfTime);
    if (scheduler == null) {
      scheduler = new HashMap<>();
    } else if (scheduler.containsKey("resetCheck")) {
      Bukkit.getScheduler().cancelTask(scheduler.get("resetCheck"));
    }
    if (scheduler.containsKey("countdownToReset")) {
      Bukkit.getScheduler().cancelTask(scheduler.remove("countdownToReset"));
    }
    Bukkit.getScheduler()
        .scheduleSyncDelayedTask(
            Main.getInstance(),
            () -> {
              scheduler.put(
                  "resetCheck",
                  Bukkit.getScheduler()
                      .scheduleSyncRepeatingTask(
                          Main.getInstance(),
                          () -> {
                            if (getTimeLeft().getSeconds() <= 5 && getTimeLeft().getSeconds() > 0) {
                              countdownToReset(mine);
                            }
                            if (resetCheck(mine)) {
                              mine.getPlayersInMine(
                                  0,
                                  (player) -> {
                                    player.sendMessage(
                                        TextUtil.format(
                                            "&e&l" + mine.getID() + " Mine&e has been reset."));
                                  });
                              mine.reset();
                              Bukkit.getScheduler().cancelTask(scheduler.get("resetCheck"));
                            }
                          },
                          0,
                          20L));
            },
            20L);
  }
}
