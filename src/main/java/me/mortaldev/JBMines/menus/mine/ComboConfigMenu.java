package me.mortaldev.JBMines.menus.mine;

import java.util.Collections;
import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.modules.mine.Mine;
import me.mortaldev.JBMines.modules.mine.MineManager;
import me.mortaldev.JBMines.modules.reset.Combo;
import me.mortaldev.JBMines.modules.reset.Percent;
import me.mortaldev.JBMines.modules.reset.Timer;
import me.mortaldev.JBMines.utils.ItemStackHelper;
import me.mortaldev.JBMines.utils.TextUtil;
import me.mortaldev.menuapi.InventoryButton;
import me.mortaldev.menuapi.InventoryGUI;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ComboConfigMenu extends InventoryGUI {

  private final Mine mine;

  public ComboConfigMenu(Mine mine) {
    this.mine = mine;
  }

  @Override
  protected Inventory createInventory() {
    return Bukkit.createInventory(null, 4 * 9, TextUtil.format("&6&lConfig Combo"));
  }

  @Override
  public void decorate(Player player) {
    addButton(11, TimerButton());
    addButton(15, PercentButton());
    addButton(27, BackButton());
    ItemStack whiteStainedGlassPane = ItemStackHelper.builder(Material.WHITE_STAINED_GLASS_PANE).name("").build();
    for (int i = 28; i < 36; i++) {
      getInventory().setItem(i, whiteStainedGlassPane);
    }
    super.decorate(player);
  }

  private InventoryButton PercentButton() {
    return new InventoryButton()
        .creator(
            player -> {
              Percent percent = ((Combo) mine.getResetType()).getPercent();
              return ItemStackHelper.builder(Material.STRING)
                  .name("&e&lPercent")
                  .addLore("&7Percent: " + percent.getResetPercentage() + "%")
                  .build();
            })
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              new AnvilGUI.Builder()
                  .plugin(Main.getInstance())
                  .title("Set Percent (0-100)")
                  .itemLeft(ItemStackHelper.builder(Material.STRING).name(((Combo) mine.getResetType()).getPercent().getResetPercentage()+"").build())
                  .onClick(
                      (slot, stateSnapshot) -> {
                        if (slot == 2) {
                          String textEntry = stateSnapshot.getText();
                          Combo updatedResetType = (Combo) mine.getResetType();
                          int percentInput;
                          try {
                            percentInput = Integer.parseInt(textEntry);
                          } catch (NumberFormatException e) {
                            percentInput = 50;
                          }
                          mine.getResetType().kill(mine);
                          updatedResetType.setPercent(new Percent(percentInput));
                          mine.setResetType(updatedResetType);
                          MineManager.getInstance().update(mine);
                          if (mine.canBeReset(mine)) {
                            mine.getResetType().start(mine);
                          }
                          Main.getGuiManager().openGUI(new ComboConfigMenu(mine), player);
                        }
                        return Collections.emptyList();
                      })
                  .open(player);
            });
  }

  private InventoryButton TimerButton() {
    return new InventoryButton()
        .creator(
            player -> {
              Timer timer = ((Combo) mine.getResetType()).getTimer();
              return ItemStackHelper.builder(Material.REPEATER)
                  .name("&e&lTimer")
                  .addLore("&7Time: " + timer.getLengthOfTime() + "s")
                  .build();
            })
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              new AnvilGUI.Builder()
                  .plugin(Main.getInstance())
                  .title("Set Timer (Seconds)")
                  .itemLeft(ItemStackHelper.builder(Material.REPEATER).name(((Combo) mine.getResetType()).getTimer().getLengthOfTime()+"").build())
                  .onClick(
                      (slot, stateSnapshot) -> {
                        if (slot == 2) {
                          String textEntry = stateSnapshot.getText();
                          Combo updatedResetType = (Combo) mine.getResetType();
                          int timerInput;
                          try {
                            timerInput = Integer.parseInt(textEntry);
                          } catch (NumberFormatException e) {
                            timerInput = 20;
                          }
                          mine.getResetType().kill(mine);
                          updatedResetType.setTimer(new Timer(timerInput));
                          mine.setResetType(updatedResetType);
                          MineManager.getInstance().update(mine);
                          mine.getResetType().start(mine);
                          Main.getGuiManager().openGUI(new ComboConfigMenu(mine), player);
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
              Main.getGuiManager().openGUI(new ModifyMineMenu(mine), player);
            });
  }
}
