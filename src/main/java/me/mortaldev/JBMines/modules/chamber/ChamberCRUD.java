package me.mortaldev.JBMines.modules.chamber;

import java.util.HashMap;
import me.mortaldev.JBMines.Main;
import me.mortaldev.crudapi.CRUD;

public class ChamberCRUD extends CRUD<Chamber> {

  private static class Singleton {
    private static final ChamberCRUD CHAMBER_CRUD_INSTANCE = new ChamberCRUD();
  }

  public static ChamberCRUD getInstance() {
    return Singleton.CHAMBER_CRUD_INSTANCE;
  }

  @Override
  public Class<Chamber> getClazz() {
    return Chamber.class;
  }

  @Override
  public HashMap<Class<?>, Object> getTypeAdapterHashMap() {
    return new HashMap<>();
  }

  @Override
  public String getPath() {
    return Main.getInstance().getDataFolder().getAbsolutePath()+"/chambers/";
  }


}
