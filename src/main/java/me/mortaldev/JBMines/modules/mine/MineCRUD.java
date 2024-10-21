package me.mortaldev.JBMines.modules.mine;

import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.modules.reset.ResetType;
import me.mortaldev.JBMines.modules.reset.ResetTypeDeserializer;
import me.mortaldev.crudapi.CRUD;

import java.util.HashMap;
import java.util.Optional;

public class MineCRUD extends CRUD<Mine> {

  private static class Singleton {
    private static final MineCRUD MINE_CRUD_INSTANCE = new MineCRUD();
  }

  public static MineCRUD getInstance(){
    return Singleton.MINE_CRUD_INSTANCE;
  }

  @Override
  public String getPath() {
    return Main.getInstance().getDataFolder().getAbsolutePath()+"/mines/";
  }

  protected Optional<Mine> getData(String id) {
    HashMap<Class<?>, Object> typeAdapterHashMap = new HashMap<>(){{
      put(ResetType.class, new ResetTypeDeserializer());
    }};
    return super.getData(id, Mine.class, typeAdapterHashMap);
    }
}
