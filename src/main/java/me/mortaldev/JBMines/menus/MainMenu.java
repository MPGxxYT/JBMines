package me.mortaldev.JBMines.menus;

import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.utils.ItemStackHelper;
import me.mortaldev.JBMines.utils.TextUtil;
import me.mortaldev.menuapi.InventoryButton;
import me.mortaldev.menuapi.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MainMenu extends InventoryGUI {

  @Override
  protected Inventory createInventory() {
    return Bukkit.createInventory(null, 9, TextUtil.format("Mines Main Menu"));
  }

  @Override
  public void decorate(Player player) {
    addButton(4, MinesButton());
    super.decorate(player);
  }

  private InventoryButton MinesButton() {
    return new InventoryButton()
        .creator(
            player ->
                ItemStackHelper.builder(Material.COBBLESTONE)
                    .name("&6&lMines")
                    .addLore("&7Click to view your mines.")
                    .build())
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              Main.getGuiManager().openGUI(new MinesMenu(), player);
            });
  }
}
