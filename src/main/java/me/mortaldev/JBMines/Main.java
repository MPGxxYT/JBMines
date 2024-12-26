package me.mortaldev.JBMines;

import co.aikar.commands.PaperCommandManager;
import me.mortaldev.JBMines.commands.MineGUICommand;
import me.mortaldev.JBMines.listeners.OnBreakInMineEvent;
import me.mortaldev.JBMines.listeners.OnRightClickBlockEvent;
import me.mortaldev.JBMines.modules.chamber.ChamberManager;
import me.mortaldev.JBMines.modules.mine.MineManager;
import me.mortaldev.menuapi.GUIListener;
import me.mortaldev.menuapi.GUIManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

  private static final String LABEL = "JBMines";
  static Main instance;
  //  static HashSet<String> dependencies = new HashSet<>() {{
  //    add("GangsPlus");
  //    add("Skript");
  //  }};
  static PaperCommandManager commandManager;
  static GUIManager guiManager;

  public static Main getInstance() {
    return instance;
  }

  public static String getLabel() {
    return LABEL;
  }

  public static GUIManager getGuiManager() {
    return guiManager;
  }

  public static void log(String message) {
    Bukkit.getLogger().info("[" + Main.getLabel() + "] " + message);
  }

  @Override
  public void onEnable() {
    instance = this;
    commandManager = new PaperCommandManager(this);

    // DATA FOLDER

    if (!getDataFolder().exists()) {
      if (!getDataFolder().mkdir()) {
        log("FAILED TO CREATE DATA FOLDER!");
      }
    }

    MineManager.getInstance().load();
    ChamberManager.getInstance().load();

    // DEPENDENCIES

    //    for (String plugin : dependencies) {
    //      if (Bukkit.getPluginManager().getPlugin(plugin) == null) {
    //        getLogger().warning("Could not find " + plugin + "! This plugin is required.");
    //        Bukkit.getPluginManager().disablePlugin(this);
    //        return;
    //      }
    //    }

    // CONFIGS
    //    mainConfig = new MainConfig();

    // Managers (Loading data)
    //    GangManager.loadGangDataList();
    // GUI Manager
    guiManager = new GUIManager();
    GUIListener guiListener = new GUIListener(guiManager);
    Bukkit.getPluginManager().registerEvents(guiListener, this);

    // Events

    getServer().getPluginManager().registerEvents(new OnRightClickBlockEvent(), this);
    getServer().getPluginManager().registerEvents(new OnBreakInMineEvent(), this);

    // COMMANDS

    commandManager.registerCommand(new MineGUICommand());

    getLogger().info(LABEL + " Enabled");

    MineManager.getInstance().resetAllMines();
  }

  @Override
  public void onDisable() {
    getLogger().info(LABEL + " Disabled");
  }
}
