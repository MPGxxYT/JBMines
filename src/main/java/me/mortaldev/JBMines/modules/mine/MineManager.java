package me.mortaldev.JBMines.modules.mine;

import me.mortaldev.JBMines.Main;
import me.mortaldev.crudapi.CRUD;
import me.mortaldev.crudapi.CRUDManager;

public class MineManager extends CRUDManager<Mine> {

  private static final class SingletonHolder {
    private static final MineManager INSTANCE = new MineManager();
  }

  public static MineManager getInstance() {
    return SingletonHolder.INSTANCE;
  }

  private MineManager() {}

  @Override
  public CRUD<Mine> getCRUD() {
    return MineCRUD.getInstance();
  }

  @Override
  public void log(String string) {
    Main.log(string);
  }

  public void resetAllMines() {
    for (Mine mine : getSet()) {
      mine.reset();
    }
  }
}
