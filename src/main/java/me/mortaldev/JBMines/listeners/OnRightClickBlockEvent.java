package me.mortaldev.JBMines.listeners;

import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.menus.MinesMenu;
import me.mortaldev.JBMines.menus.mine.ConfigureMineMenu;
import me.mortaldev.JBMines.modules.mine.Mine;
import me.mortaldev.JBMines.modules.mine.MineManager;
import me.mortaldev.JBMines.records.Pair;
import me.mortaldev.JBMines.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class OnRightClickBlockEvent implements Listener {

  static HashMap<UUID, Pair<Integer, Mine>> interactUsers = new HashMap<>();
  static Set<UUID> pauseUsers = new HashSet<>();
  static HashMap<UUID, Location> corners = new HashMap<>();

  public static void addUser(UUID uuid, Pair<Integer, Mine> pair) {
    interactUsers.put(uuid, pair);
  }
  public static Mine removeUser(UUID uuid) {
    if (interactUsers.containsKey(uuid)) {
      Pair<Integer, Mine> remove = interactUsers.remove(uuid);
      pauseUsers.remove(uuid);
      corners.remove(uuid);
      return remove.second();
    }
    return null;
  }

  @EventHandler
  private void onPlayerInteractEvent(PlayerInteractEvent event) {
    if (!interactUsers.containsKey(event.getPlayer().getUniqueId())) {
      return;
    }
    if (pauseUsers.contains(event.getPlayer().getUniqueId())) {
      return;
    }
    Player player = event.getPlayer();
    UUID uniqueId = player.getUniqueId();
    Pair<Integer, Mine> pair = interactUsers.get(uniqueId);
    Mine mine = pair.second();
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK
        && event.getClickedBlock() != null
        && event.getClickedBlock().getType() != Material.AIR) {
      Location location = event.getClickedBlock().getLocation();
      if (pair.first() == 1) {
        pauseUsers.add(uniqueId);
        corners.put(uniqueId, location);
        interactUsers.put(uniqueId, new Pair<>(2, mine));
        player.sendMessage(TextUtil.format("&7Corner 1 set to " + location.getX() + ", " + location.getY() + ", "+ location.getZ() + ", "+ location.getWorld().getName()));
        player.sendMessage(TextUtil.format("&eRight-Click a block to set Corner 2."));
        player.sendMessage(TextUtil.format("&c[Click to Cancel]##cmd:/minegui cancelconfig", true));
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
          pauseUsers.remove(uniqueId);
        }, 10);
      } else if (pair.first() == 2) {
        mine.setCornerOne(corners.get(uniqueId));
        mine.setCornerTwo(location);
        corners.remove(uniqueId);
        MineManager.INSTANCE.updateMine(mine);
        mine.updateTotalSize();
        mine.save();
        interactUsers.remove(uniqueId);
        player.sendMessage(TextUtil.format("&7Corner 2 set to " + location.getX() + ", " + location.getY() + ", "+ location.getZ() + ", "+ location.getWorld().getName()));
        player.sendMessage(TextUtil.format("&6&lCorners are Configured!"));
        Main.getGuiManager().openGUI(new ConfigureMineMenu(mine), player);
      }
    }
  }
}
