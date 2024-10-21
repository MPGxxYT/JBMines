package me.mortaldev.JBMines.menus.mine;

import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.modules.mine.Mine;
import me.mortaldev.JBMines.modules.mine.MineManager;
import me.mortaldev.JBMines.utils.ItemStackHelper;
import me.mortaldev.JBMines.utils.TextUtil;
import me.mortaldev.menuapi.InventoryButton;
import me.mortaldev.menuapi.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ConfigureMineMenu extends InventoryGUI {

  Mine mine;

  public ConfigureMineMenu(Mine mine) {
    this.mine = mine;
  }

  @Override
  protected Inventory createInventory() {
    return Bukkit.createInventory(null, 4 * 9, TextUtil.format("&6&lConfigure Mine"));
  }

  @Override
  public void decorate(Player player) {
    addButton(27, BackButton());
    addButton(15, SetSpawnButton());
    addButton(11, ConfigureCornersButton());
    ItemStack whiteGlass = ItemStackHelper.builder(Material.WHITE_STAINED_GLASS_PANE).name("").build();
    for(int i = 28; i < 36; i++) {
      getInventory().setItem(i, whiteGlass);
    }
    super.decorate(player);
  }

  private InventoryButton ConfigureCornersButton() {
    return new InventoryButton()
        .creator(
            player -> {
              Location cornerOne = mine.getCornerOne();
              Location cornerTwo = mine.getCornerTwo();
              ItemStackHelper.Builder item =
                  ItemStackHelper.builder(Material.MAP)
                      .name("&e&lConfigure Corners")
                      .addLore("&7Change the cubic region where the")
                      .addLore("&7blocks for the mine will be placed.")
                      .addLore();
              if (cornerOne == null || cornerTwo == null) {
                item.addLore("&eSize:&f N/A");
              } else {
                item.addLore("&eSize:&f " + mine.getTotalSize());
              }
              item.addLore();
              if (cornerOne != null) {
                item.addLore(
                    "&eC1:&f "
                        + cornerOne.getBlockX()
                        + ", "
                        + cornerOne.getBlockY()
                        + ", "
                        + cornerOne.getBlockZ());
              } else {
                item.addLore("&eC1:&c Not Set");
              }
              if (cornerTwo != null) {
                item.addLore(
                    "&eC2:&f "
                        + cornerTwo.getBlockX()
                        + ", "
                        + cornerTwo.getBlockY()
                        + ", "
                        + cornerTwo.getBlockZ());
              } else {
                item.addLore("&eC2:&c Not Set");
              }
              return item.addLore().addLore("&7[Click to Change]").build();
            })
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              mine.configureCorners(player);
            });
  }

  private InventoryButton SetSpawnButton() {
    return new InventoryButton()
        .creator(
            player -> {
              Location mineSpawn = mine.getMineSpawn();
              ItemStackHelper.Builder item =
                  ItemStackHelper.builder(Material.RESPAWN_ANCHOR)
                      .name("&e&lSet Spawn")
                      .addLore("&7The location the players inside the mine")
                      .addLore("&7will be teleported to when reset.")
                      .addLore()
                      .addLore("&eLocation:");
              if (mineSpawn.getWorld() == null) {
                item.addLore("&cNot Set");
              } else {
                item.addLore("&f X: &7" + mineSpawn.getBlockX())
                .addLore("&f Y: &7" + mineSpawn.getBlockY())
                .addLore("&f Z: &7" + mineSpawn.getBlockZ())
                .addLore("&f World: &7" + mineSpawn.getWorld().getName());
              }
              return item.addLore().addLore("&7[Click to Set]").build();
            })
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              Location location = player.getLocation();
              mine.setMineSpawn(location);
              mine.save();
              MineManager.INSTANCE.updateMine(mine);
              player.sendMessage(
                  TextUtil.format(
                      "&e&l"
                          + mine.getID()
                          + " &eSpawn set to: &7"
                          + location.getBlockX()
                          + "&f, &7"
                          + location.getBlockY()
                          + "&f, &7"
                          + location.getBlockZ()
                          + "&f, &7"
                          + location.getWorld().getName()));
            });
  }

  private InventoryButton BackButton() {
    return new InventoryButton()
        .creator(
            player ->
                ItemStackHelper.builder(Material.ARROW)
                    .name("&c&lBack")
                    .addLore("&7Click to return to previous menu.")
                    .build())
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              Main.getGuiManager().openGUI(new ModifyMineMenu(mine), player);
            });
  }
}
