package me.mortaldev.JBMines.modules.reset;

import me.mortaldev.JBMines.modules.mine.Mine;

public abstract class ResetType {
  public abstract boolean resetCheck(Mine mine);

  public abstract void start(Mine mine);

  public abstract void kill(Mine mine);
}
