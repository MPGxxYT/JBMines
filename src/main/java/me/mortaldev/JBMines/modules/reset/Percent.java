package me.mortaldev.JBMines.modules.reset;

import java.math.BigDecimal;
import java.util.HashMap;
import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.modules.mine.Mine;
import org.bukkit.Bukkit;

public class Percent extends ResetType {
  private final BigDecimal resetPercentage;
  private transient HashMap<String, Integer> scheduler = new HashMap<>();

  public Percent(double resetPercentage) {
    this.resetPercentage = BigDecimal.valueOf(resetPercentage);
  }

  public BigDecimal getResetPercentage() {
    return resetPercentage;
  }

  @Override
  public boolean resetCheck(Mine mine) {
    return mine.getPercentLeft().compareTo(resetPercentage) <= 0;
  }

  @Override
  public void start(Mine mine) {
    scheduler = new HashMap<>();
    scheduler.computeIfAbsent(
        "resetCheck",
        k ->
            Bukkit.getScheduler()
                .scheduleSyncRepeatingTask(
                    Main.getInstance(),
                    () -> {
                      if (resetCheck(mine)) {
                        mine.countdownToReset();
                        Bukkit.getScheduler().cancelTask(scheduler.get("resetCheck"));
                      }
                    },
                    0,
                    20));
  }

  @Override
  public void kill(Mine mine) {
    mine.killCountdown();
    if (scheduler == null) {
      return;
    }
    Integer resetCheck = scheduler.remove("resetCheck");
    if (resetCheck != null) {
      Bukkit.getScheduler().cancelTask(resetCheck);
    }
  }
}
