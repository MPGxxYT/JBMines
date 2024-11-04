package me.mortaldev.JBMines.modules.chamber;

import me.mortaldev.JBMines.Main;
import me.mortaldev.crudapi.CRUD;
import me.mortaldev.crudapi.CRUDManager;

public class ChamberManager extends CRUDManager<Chamber> {

  private static final class SingletonHolder {
    private static final ChamberManager INSTANCE = new ChamberManager();
  }

  public static ChamberManager getInstance() {
    return SingletonHolder.INSTANCE;
  }

  private ChamberManager() {}

  @Override
  public CRUD<Chamber> getCRUD() {
    return ChamberCRUD.getInstance();
  }

  @Override
  public void log(String string) {
    Main.log(string);
  }
}
