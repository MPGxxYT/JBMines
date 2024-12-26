package me.mortaldev.JBMines.modules.reset;

import me.mortaldev.JBMines.modules.mine.Mine;

public class Combo extends ResetType {
  private Timer timer;
  private Percent percent;

  public Combo(Timer timer, Percent percent) {
    this.timer = timer;
    this.percent = percent;
  }

  public Percent getPercent() {
    return percent;
  }

  public void setPercent(Percent percent) {
    this.percent = percent;
  }

  public Timer getTimer() {
    return timer;
  }

  public void setTimer(Timer timer) {
    this.timer = timer;
  }

  @Override
  public boolean resetCheck(Mine mine) {
    return false;
  }

  @Override
  public void start(Mine mine) {
    timer.start(mine);
    percent.start(mine);
  }

  @Override
  public void kill(Mine mine) {
    timer.kill(mine);
    percent.kill(mine);
  }
}
