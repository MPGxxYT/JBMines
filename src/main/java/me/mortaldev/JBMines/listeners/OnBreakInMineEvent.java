package me.mortaldev.JBMines.listeners;

import me.mortaldev.JBMines.modules.mine.Mine;
import me.mortaldev.JBMines.modules.mine.MineManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class OnBreakInMineEvent implements Listener {

  @EventHandler
  public void onBreak(BlockBreakEvent event) {
    Location location = event.getBlock().getLocation();
    for (Mine mine : MineManager.getInstance().getSet()) {
      if (mine.locationIsInMine(location)) {
        mine.adjustBlocksLeft(-1);
      }
    }
  }
}
