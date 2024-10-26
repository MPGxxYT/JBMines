package me.mortaldev.JBMines.modules.chamber;

import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.utils.TextUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public enum ChamberManager {
  INSTANCE;
  private Set<Chamber> chamberSet;

  ChamberManager() {
    chamberSet = new HashSet<>();
  }

  public void loadChambersFromFile() {
    chamberSet.clear();
    File fileDir = new File(ChamberCRUD.getInstance().getPath());
    if (!fileDir.exists()) {
      if (!fileDir.mkdirs()) {
        Main.log("Failed to create /chambers/ directory.");
        return;
      }
    }
    File[] files = fileDir.listFiles();
    if (files == null) {
      Main.log("No chambers loaded.");
      return;
    }
    for (File file : files) {
      String fileNameWithoutExtension = file.getName().replace(".json", "");
      Optional<Chamber> data = ChamberCRUD.getInstance().getData(fileNameWithoutExtension);
      if (data.isEmpty()) {
        Main.log("Failed to load Chamber: " + file.getName());
        continue;
      }
      chamberSet.add(data.get());
    }
  }

  public boolean chamberByIDExists(String id) {
    for (Chamber chamber : chamberSet) {
      if (chamber.getID().equals(TextUtil.fileFormat(id))) {
        return true;
      }
    }
    return false;
  }

  public Set<Chamber> getChamberSet() {
    if (chamberSet.isEmpty()) {
      loadChambersFromFile();
    }
    return chamberSet;
  }

  public Optional<Chamber> getChamber(String id) {
    Set<Chamber> chambers = getChamberSet();
    for (Chamber chamber : chambers) {
      if (chamber.getID().equals(id)) {
        return Optional.of(chamber);
      }
    }
    Main.log("No chamber found with ID: " + id);
    return Optional.empty();
  }

  public boolean addChamber(Chamber chamber) {
    if (chamberByIDExists(chamber.getID())) {
      return false;
    }
    chamberSet.add(chamber);
    return true;
  }

  public void deleteChamber(Chamber chamber) {
    if (chamberByIDExists(chamber.getID())) {
      chamberSet.remove(chamber);
      chamber.delete();
    }
  }
}
