package me.mortaldev.JBMines.modules.chamber;

import me.mortaldev.JBMines.Main;
import me.mortaldev.crudapi.CRUD;

import java.util.Optional;

public class ChamberCRUD extends CRUD<Chamber> {

  private static class Singleton {
    private static final ChamberCRUD CHAMBER_CRUD_INSTANCE = new ChamberCRUD();
  }

  public static ChamberCRUD getInstance() {
    return Singleton.CHAMBER_CRUD_INSTANCE;
  }

  @Override
  public String getPath() {
    return Main.getInstance().getDataFolder().getAbsolutePath()+"/chambers/";
  }

  protected Optional<Chamber> getData(String id) {
    return super.getData(id, Chamber.class);
  }
}
