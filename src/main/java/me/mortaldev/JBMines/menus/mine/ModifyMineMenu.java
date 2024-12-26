package me.mortaldev.JBMines.menus.mine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.menus.MinesMenu;
import me.mortaldev.JBMines.modules.mine.Mine;
import me.mortaldev.JBMines.modules.mine.MineManager;
import me.mortaldev.JBMines.modules.reset.Combo;
import me.mortaldev.JBMines.modules.reset.Percent;
import me.mortaldev.JBMines.modules.reset.ResetType;
import me.mortaldev.JBMines.modules.reset.Timer;
import me.mortaldev.JBMines.utils.ItemStackHelper;
import me.mortaldev.JBMines.utils.TextUtil;
import me.mortaldev.menuapi.InventoryButton;
import me.mortaldev.menuapi.InventoryGUI;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
    ItemStack whiteGlass =
        ItemStackHelper.builder(Material.WHITE_STAINED_GLASS_PANE).name("").build();
    for (int i = 28; i < 36; i++) {
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
              itemStack.addLore("").addLore(data).addLore("").addLore("&7[Left-Click for Next Type]").addLore("&7[Right-Click to Update]");
              return itemStack.build();
            })
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              if (mine.canBeReset(mine) && mine.isResetting()) {
                player.sendMessage(TextUtil.format("&cMine is currently resetting, please wait."));
                return;
              }
              ResetType resetType = mine.getResetType();
              if (event.getClick() == ClickType.LEFT) {
                mine.getResetType().kill(mine);
                mine.setResetType(nextType(resetType));
                MineManager.getInstance().update(mine);
                if (mine.canBeReset(mine)) {
                  mine.getResetType().start(mine);
                }
                Main.getGuiManager().openGUI(new ModifyMineMenu(mine), player);
              } else if (event.getClick() == ClickType.RIGHT) {
                if (resetType instanceof Timer) {
                  anvilUpdateTimer(player);
                } else if (resetType instanceof Percent) {
                  anvilUpdatePercent(player);
                } else if (resetType instanceof Combo) {
                  anvilUpdateCombo(player);
                }
              }
            });
  }

  public void anvilUpdateCombo(Player player) {
    Main.getGuiManager().openGUI(new ComboConfigMenu(mine), player);
  }

  public void anvilUpdateTimer(Player player) {
    new AnvilGUI.Builder()
        .plugin(Main.getInstance())
        .title("New Time (Seconds)")
        .itemLeft(
            ItemStackHelper.builder(Material.NAME_TAG)
                .name(((Timer) mine.getResetType()).getLengthOfTime() + "")
                .build())
        .onClick(
            (slot, stateSnapshot) -> {
              if (slot == 2) {
                String textEntry = stateSnapshot.getText();
                mine.getResetType().kill(mine);
                mine.setResetType(new Timer(Integer.parseInt(textEntry)));
                MineManager.getInstance().update(mine);
                mine.getResetType().start(mine);
                Main.getGuiManager().openGUI(new ModifyMineMenu(mine), player);
              }
              return Collections.emptyList();
            })
        .open(player);
  }

  public void anvilUpdatePercent(Player player) {
    new AnvilGUI.Builder()
        .plugin(Main.getInstance())
        .title("New Percent (0-100)")
        .itemLeft(
            ItemStackHelper.builder(Material.NAME_TAG)
                .name(((Percent) mine.getResetType()).getResetPercentage().toPlainString())
                .build())
        .onClick(
            (slot, stateSnapshot) -> {
              if (slot == 2) {
                String textEntry = stateSnapshot.getText();
                mine.getResetType().kill(mine);
                mine.setResetType(new Percent(Double.parseDouble(textEntry)));
                MineManager.getInstance().update(mine);
                mine.getResetType().start(mine);
                Main.getGuiManager().openGUI(new ModifyMineMenu(mine), player);
              }
              return Collections.emptyList();
            })
        .open(player);
  }

  public ResetType nextType(ResetType resetType) {
    if (resetType instanceof Timer) {
      return new Percent(50);
    } else if (resetType instanceof Percent) {
      return new Combo(new Timer(20), new Percent(50));
    } else if (resetType instanceof Combo) {
      return new Timer(20);
    }
    return null;
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
