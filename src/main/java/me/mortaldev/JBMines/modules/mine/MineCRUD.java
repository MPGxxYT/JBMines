package me.mortaldev.JBMines.modules.mine;

import java.util.HashMap;
import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.modules.reset.ResetType;
import me.mortaldev.JBMines.modules.reset.ResetTypeDeserializer;
import me.mortaldev.crudapi.CRUD;

public class MineCRUD extends CRUD<Mine> {

  private static final class SingletonHolder {
    private static final MineCRUD INSTANCE = new MineCRUD();
  }

  public static MineCRUD getInstance() {
    return SingletonHolder.INSTANCE;
  }

  private MineCRUD() {}

  @Override
  public Class<Mine> getClazz() {
    return Mine.class;
  }

  @Override
  public HashMap<Class<?>, Object> getTypeAdapterHashMap() {
    return new HashMap<>() {
      {
        put(ResetType.class, new ResetTypeDeserializer());
      }
    };
  }

  @Override
  public String getPath() {
    return Main.getInstance().getDataFolder().getAbsolutePath() + "/mines/";
  }
}
