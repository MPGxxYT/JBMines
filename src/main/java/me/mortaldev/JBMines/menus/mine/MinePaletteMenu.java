package me.mortaldev.JBMines.menus.mine;

import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.modules.mine.Mine;
import me.mortaldev.JBMines.modules.mine.MineManager;
import me.mortaldev.JBMines.records.Pair;
import me.mortaldev.JBMines.utils.ItemStackHelper;
import me.mortaldev.JBMines.utils.TextUtil;
import me.mortaldev.menuapi.InventoryButton;
import me.mortaldev.menuapi.InventoryGUI;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

public class MinePaletteMenu extends InventoryGUI {

  Mine mine;

  public MinePaletteMenu(Mine mine) {
    this.mine = mine;
    allowBottomInventoryClick(true);
  }

  @Override
  protected Inventory createInventory() {
    return Bukkit.createInventory(null, 6 * 9, TextUtil.format("&6&lMine Palette"));
  }

  @Override
  public void decorate(Player player) {
    int i = 0;
    for (Map.Entry<Material, BigDecimal> entry : mine.getBlockPalette().entrySet()) {
      addButton(i, BlockButton(new Pair<>(entry.getKey(), entry.getValue())));
      i++;
    }
    ItemStack whiteGlass =
        ItemStackHelper.builder(Material.WHITE_STAINED_GLASS_PANE).name("").build();
    for (int j = 45; j < 54; j++) {
      getInventory().setItem(j, whiteGlass);
    }
    addButton(45, BackButton());
    addButton(47, RebalanceButton());
    addButton(49, AddBlockButton());
    super.decorate(player);
  }

  private InventoryButton AddBlockButton() {
    return new InventoryButton()
        .creator(
            player ->
                ItemStackHelper.builder(Material.BUCKET)
                    .name("&e&lAdd Block")
                    .addLore("&7Click with a block to")
                    .addLore("&7add it to the palette.")
                    .addLore("")
                    .addLore("&7[Click to Add]")
                    .build())
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
                return;
              }
              if (!mine.addBlockToPalette(event.getCursor())) {
                player.playNote(
                    player.getLocation(), Instrument.BASS_GUITAR, Note.natural(1, Note.Tone.C));
                return;
              }
              mine.save();
              MineManager.getInstance().update(mine);
              Main.getGuiManager().openGUI(new MinePaletteMenu(mine), player);
            });
  }

  private InventoryButton RebalanceButton() {
    return new InventoryButton()
        .creator(
            player ->
                ItemStackHelper.builder(Material.REDSTONE)
                    .name("&e&lBalance")
                    .addLore("&7Will balance the percents")
                    .addLore("&7to have a sum of 100%")
                    .addLore("")
                    .addLore("&eTotal: " + mine.getBlockPaletteRaw().getTableSum() + "%")
                    .addLore("")
                    .addLore("&7[Click to Balance]")
                    .build())
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              if (mine.getBlockPaletteRaw().balanceTable()) {
                mine.save();
                MineManager.getInstance().update(mine);
                Main.getGuiManager().openGUI(new MinePaletteMenu(mine), player);
              }
            });
  }

  /**
   * Returns a button for a block in the palette.
   *
   * <p>The button will display the block's material and the percent of the mine that block makes
   * up. When clicked, the button will open an anvil gui with the percent of the mine displayed as a
   * string, and the player can enter a new percent.
   *
   * <p>When the anvil gui is submitted, the block's percent in the mine will be updated to the
   * entered value, and the gui will be re-opened.
   *
   * <p>When right-clicked, the block will be removed from the palette.
   *
   * @param pair the pair of material and percent of the block
   * @return the button
   */
  private InventoryButton BlockButton(Pair<Material, BigDecimal> pair) {
    return new InventoryButton()
        .creator(
            player ->
                ItemStackHelper.builder(pair.first())
                    .addLore("&e" + pair.second().doubleValue() + "% of Mine")
                    .addLore("")
                    .addLore("&7[Left-Click to Change]")
                    .addLore("&7[Right-Click to Remove]")
                    .build())
        .consumer(
            event -> {
              Player player = (Player) event.getWhoClicked();
              if (event.getClick() == ClickType.LEFT) {
                new AnvilGUI.Builder()
                    .plugin(Main.getInstance())
                    .title("Percent")
                    .itemLeft(
                        ItemStackHelper.builder(Material.PAPER)
                            .name(pair.second().doubleValue() + "")
                            .build())
                    .onClick(
                        (slot, stateSnapshot) -> {
                          if (slot == 2) {
                            String textEntry = stateSnapshot.getText();
                            if (textEntry.trim().matches("^(\\d+(\\.\\d+)?)$")) {
                              if (!mine.getBlockPaletteRaw().updateKey(pair.first(), textEntry)) {
                                Main.log("Failed to update block: " + pair.first().toString());
                              }
                              mine.save();
                              MineManager.getInstance().update(mine);
                            }
                          }
                          Main.getGuiManager().openGUI(new MinePaletteMenu(mine), player);
                          return Collections.emptyList();
                        })
                    .open(player);
              } else if (event.getClick() == ClickType.RIGHT) {
                mine.removeBlockFromPalette(pair.first());
                mine.save();
                MineManager.getInstance().update(mine);
                Main.getGuiManager().openGUI(new MinePaletteMenu(mine), player);
              }
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
