package me.mortaldev.JBMines.modules.mine;

import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.utils.TextUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public enum MineManager {
  INSTANCE;
  private Set<Mine> mineSet;

  MineManager() {
    mineSet = new HashSet<>();
  }

  public void loadMinesFromFile() {
    mineSet = new HashSet<>();
    File mineDir = new File(MineCRUD.getInstance().getPath());
    if (!mineDir.exists()) {
      if (!mineDir.mkdirs()) {
        Main.log("Failed to /mine/ create directory.");
        return;
      }
    }
    File[] files = mineDir.listFiles();
    if (files == null) {
      Main.log("No mines loaded.");
      return;
    }
    for (File file : files) {
      String fileNameWithoutExtension = file.getName().replace(".json", "");
      Optional<Mine> data = MineCRUD.getInstance().getData(fileNameWithoutExtension);
      if (data.isEmpty()) {
        Main.log("Failed to load Mine: " + file.getName());
        continue;
      }
      mineSet.add(data.get());
    }
  }

  public synchronized Set<Mine> getMineSet() {
    if (mineSet == null) {
      loadMinesFromFile();
    }
    return mineSet;
  }

  public synchronized Mine getMine(String id) {
    for (Mine mine : getMineSet()) {
      if (mine.getID().equals(TextUtil.fileFormat(id))) {
        return mine;
      }
    }
    return null;
  }

  public Boolean mineByIdExists(String id) {
    for (Mine mine : getMineSet()) {
      if (mine.getID().equals(TextUtil.fileFormat(id))) {
        return true;
      }
    }
    return false;
  }

  public synchronized boolean addMine(Mine mine) {
    if (mineByIdExists(mine.getID())) {
      return false;
    }
    mineSet.add(mine);
    mine.save();
    return true;
  }

  public synchronized void updateMine(Mine mine) {
    removeMine(mine);
    addMine(mine);
  }

  private synchronized void removeMine(Mine mine){
    if (mineByIdExists(mine.getID())) {
      mineSet.remove(mine);
    }
  }

  public synchronized void deleteMine(Mine mine) {
    if (mineByIdExists(mine.getID())) {
      mineSet.remove(mine);
      mine.delete();
    }
  }
}
