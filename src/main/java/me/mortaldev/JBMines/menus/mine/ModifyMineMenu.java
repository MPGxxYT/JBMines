package me.mortaldev.JBMines.menus.mine;

import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.menus.MinesMenu;
import me.mortaldev.JBMines.modules.mine.Mine;
import me.mortaldev.JBMines.modules.reset.Combo;
import me.mortaldev.JBMines.modules.reset.Percent;
import me.mortaldev.JBMines.modules.reset.ResetType;
import me.mortaldev.JBMines.modules.reset.Timer;
import me.mortaldev.JBMines.utils.ItemStackHelper;
import me.mortaldev.JBMines.utils.TextUtil;
import me.mortaldev.menuapi.InventoryButton;
import me.mortaldev.menuapi.InventoryGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ModifyMineMenu extends InventoryGUI {

  Mine mine;

  public ModifyMineMenu(Mine mine) {
    this.mine = mine;
  }

  @Override
  protected Inventory createInventory() {
    return Bukkit.createInventory(null, 4 * 9, TextUtil.format("&6&lModify Mine"));
  }

  @Override
  public void decorate(Player player) {
    addButton(10, ConfigureButton());
    addButton(13, ResetTypeButton());
    addButton(16, PaletteButton());
    addButton(27, BackButton());
    ItemStack whiteGlass = ItemStackHelper.builder(Material.WHITE_STAINED_GLASS_PANE).name("").build();
    for(int i = 28; i < 36; i++) {
      getInventory().setItem(i, whiteGlass);
    }
    super.decorate(player);
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
              Main.getGuiManager().openGUI(new MinesMenu(), player);
            });
  }

  private InventoryButton ConfigureButton() {
    return new InventoryButton()
        .creator(
            player ->
                ItemStackHelper.builder(Material.LODESTONE)
                    .name("&e&lConfigure")
                    .addLore("&7Click to configure the mine.")
                    .build())
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              Main.getGuiManager().openGUI(new ConfigureMineMenu(mine), player);
            });
  }

  private InventoryButton ResetTypeButton() {
    return new InventoryButton()
        .creator(
            player -> {
              ItemStackHelper.Builder itemStack =
                  ItemStackHelper.builder(Material.GRINDSTONE).name("&e&lReset Type");
              List<String> data = new ArrayList<>();
              ResetType resetType = mine.getResetType();

              if (resetType instanceof Timer) {
                itemStack.addLore("&e&l> Timer");
                data.add("&fTime:&7 " + ((Timer) resetType).getLengthOfTime());
              } else {
                itemStack.addLore("&7> Timer");
              }
              if (resetType instanceof Percent) {
                itemStack.addLore("&e&l> Percent");
                data.add("&fPercent:&7 " + ((Percent) resetType).getResetPercentage());
              } else {
                itemStack.addLore("&7> Percent");
              }
              if (resetType instanceof Combo) {
                itemStack.addLore("&e&l> Combo");
                data.add("&fPercent:&7 " + ((Combo) resetType).getTimer().getLengthOfTime());
                data.add("&fTime:&7 " + ((Combo) resetType).getPercent().getResetPercentage());
              } else {
                itemStack.addLore("&7> Combo");
              }
              itemStack.addLore("").addLore(data);
              return itemStack.build();
            })
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
            });
  }

  private InventoryButton PaletteButton() {
    return new InventoryButton()
        .creator(
            player ->
                ItemStackHelper.builder(Material.CHEST)
                    .name("&e&lPalette")
                    .addLore("&7Click to change block palette.")
                    .build())
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              Main.getGuiManager().openGUI(new MinePaletteMenu(mine), player);
            });
  }
}
