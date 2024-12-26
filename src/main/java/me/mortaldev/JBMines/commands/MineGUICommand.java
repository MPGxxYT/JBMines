package me.mortaldev.JBMines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.listeners.OnRightClickBlockEvent;
import me.mortaldev.JBMines.menus.MainMenu;
import me.mortaldev.JBMines.menus.mine.ConfigureMineMenu;
import me.mortaldev.JBMines.modules.chamber.ChamberManager;
import me.mortaldev.JBMines.modules.mine.Mine;
import me.mortaldev.JBMines.modules.mine.MineManager;
import me.mortaldev.JBMines.utils.TextUtil;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
@CommandAlias("minegui|mg")
public class MineGUICommand extends BaseCommand {

  @Default
  @CommandPermission("jbmines.admin")
  public void openMenu(final Player player) {
    Main.getGuiManager().openGUI(new MainMenu(), player);
  }

  @Subcommand("reload")
  @CommandPermission("jbmines.admin")
  public void reload(final Player player) {
    MineManager.getInstance().load();
    ChamberManager.getInstance().load();
    player.sendMessage("Reloaded.");
  }

  @Subcommand("cancelConfig")
  @CommandPermission("jbmines.admin")
  public void cancelConfig(final Player player) {
    Mine mine = OnRightClickBlockEvent.removeUser(player.getUniqueId());
    if (mine == null) {
      player.sendMessage(TextUtil.format("&cNo configuration to cancel."));
      return;
    }
    player.sendMessage(TextUtil.format("&cCancelled configuration."));
    Main.getGuiManager().openGUI(new ConfigureMineMenu(mine), player);
  }
}
