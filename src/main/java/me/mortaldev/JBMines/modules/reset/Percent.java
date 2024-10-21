package me.mortaldev.JBMines.modules.reset;

import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.modules.mine.Mine;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

public class Percent extends ResetType {
  private final BigDecimal resetPercentage;

  Percent(double resetPercentage) {
    this.resetPercentage = BigDecimal.valueOf(resetPercentage);
  }

  public BigDecimal getResetPercentage() {
    return resetPercentage;
  }

  @Override
  public boolean resetCheck(Mine mine) {
    BigDecimal amountOfAir = BigDecimal.valueOf(mine.getTypeAmount(Material.AIR));
    BigDecimal percent = amountOfAir.divide(BigDecimal.valueOf(mine.getTotalSize()), RoundingMode.DOWN);
    return percent.compareTo(resetPercentage) <= 0;
  }

  @Override
  public void start(Mine mine) {
    HashMap<String, Integer> scheduler = new HashMap<>();
    scheduler.put("resetCheck", Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
      if (resetCheck(mine)) {
        mine.reset();
        Bukkit.getScheduler().cancelTask(scheduler.get("resetCheck"));
      }
    },0, 1000));
  }
}