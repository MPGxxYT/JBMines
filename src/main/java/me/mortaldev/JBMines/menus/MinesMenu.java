package me.mortaldev.JBMines.menus;

import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.menus.mine.ModifyMineMenu;
import me.mortaldev.JBMines.modules.mine.Mine;
import me.mortaldev.JBMines.modules.mine.MineManager;
import me.mortaldev.JBMines.utils.ItemStackHelper;
import me.mortaldev.JBMines.utils.TextUtil;
import me.mortaldev.JBMines.utils.Utils;
import me.mortaldev.menuapi.InventoryButton;
import me.mortaldev.menuapi.InventoryGUI;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Set;

public class MinesMenu extends InventoryGUI {
  Set<Mine> mines;

  public MinesMenu() {
    this.mines = MineManager.INSTANCE.getMineSet();
  }

  private Integer getInventorySize() {
    double inventorySize = Math.ceil(((double) mines.size() / 9)) + 2;
    return Utils.clamp((int) inventorySize, 3, 6);
  }

  @Override
  protected Inventory createInventory() {
    return Bukkit.createInventory(null, getInventorySize() * 9, TextUtil.format("&6&lMines"));
  }

  @Override
  public void decorate(Player player) {
    int i = 0;
    for (Mine mine : mines) {
      addButton(i, MineButton(mine));
      i++;
    }
    int offset = 9 * (getInventorySize() - 3);
    ItemStack glassFiller =
        ItemStackHelper.builder(Material.WHITE_STAINED_GLASS_PANE)
            .name("&7")
            .itemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
            .build();
    i = 18 + offset;
    for (int j = 0; j < 9; j++, i++) {
      getInventory().setItem(i, glassFiller);
    }
    addButton(18 + offset, BackButton());
    addButton(25 + offset, CreateButton());
    super.decorate(player);
  }

  private InventoryButton CreateButton() {
    return new InventoryButton()
        .creator(
            player ->
                ItemStackHelper.builder(Material.LIME_DYE)
                    .name("&2&lCreate")
                    .addLore("&7Click to create a new mine.")
                    .build())
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              new AnvilGUI.Builder()
                  .plugin(Main.getInstance())
                  .title("Mine ID")
                  .itemLeft(ItemStackHelper.builder(Material.PAPER).name().build())
                  .onClick(
                      (slot, stateSnapshot) -> {
                        if (slot == 2) {
                          String textEntry = stateSnapshot.getText();
                          Mine mine = new Mine(textEntry);
                          MineManager.INSTANCE.addMine(mine);
                          Main.getGuiManager().openGUI(new MinesMenu(), player);
                        }
                        return Collections.emptyList();
                      })
                  .open(player);
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
              Main.getGuiManager().openGUI(new MainMenu(), player);
            });
  }

  private InventoryButton MineButton(Mine mine) {
    return new InventoryButton()
        .creator(player -> mine.getDisplayItemStack())
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              if (event.getClick() == ClickType.RIGHT) {
                MineManager.INSTANCE.deleteMine(mine);
                Main.getGuiManager().openGUI(new MinesMenu(), player);
              } else if (event.getClick() == ClickType.MIDDLE) {
                mine.reset();
              } else if (event.getClick() == ClickType.LEFT) {
                Main.getGuiManager().openGUI(new ModifyMineMenu(mine), player);
              }
            });
  }
}
