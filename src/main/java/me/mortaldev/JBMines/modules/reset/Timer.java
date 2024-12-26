package me.mortaldev.JBMines.modules.reset;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import me.mortaldev.JBMines.Main;
import me.mortaldev.JBMines.modules.mine.Mine;
import org.bukkit.Bukkit;

public class Timer extends ResetType {
  private final int lengthOfTime;
  private transient Instant endTime;
  private transient HashMap<String, Integer> scheduler = new HashMap<>();

  public Timer(int lengthOfTime) {
    this.lengthOfTime = lengthOfTime;
  }

  /**
   * Checks if the timer has expired.
   *
   * @param mine The mine being reset.
   * @return true if the timer has expired, false otherwise.
   */
  @Override
  public boolean resetCheck(Mine mine) {
    return endTime != null && Instant.now().isAfter(endTime);
  }

  /**
   * Calculates the duration between the current time and the end time.
   *
   * @return a duration representing the time left until the timer expires, or Duration.ZERO if the
   *     timer has already expired.
   */
  public Duration getTimeLeft() {
    return endTime != null ? Duration.between(Instant.now(), endTime) : Duration.ZERO;
  }

  /**
   * Gets the length of time in seconds that the timer will last.
   *
   * @return the length of time in seconds that the timer will last
   */
  public int getLengthOfTime() {
    return lengthOfTime;
  }

  /**
   * Starts a timer to reset the mine. The timer lasts for the length of time given in the
   * constructor.
   *
   * <p>During the last 5 seconds of the timer, the plugin will send a message to all players in the
   * mine with the time left until reset.
   *
   * <p>When the timer expires, the plugin will send a message to all players in the mine that the
   * mine has been reset, and then reset the mine.
   *
   * @param mine The mine being reset.
   */
  @Override
  public void start(Mine mine) {
    if (scheduler == null) {
      scheduler = new HashMap<>();
    }
    endTime = Instant.now().plusSeconds(lengthOfTime);
    scheduler.computeIfAbsent(
        "resetCheck",
        k ->
            Bukkit.getScheduler()
                .scheduleSyncRepeatingTask(
                    Main.getInstance(),
                    () -> {
                      if (getTimeLeft().getSeconds() <= 10) {
                        mine.countdownToReset();
                        Bukkit.getScheduler().cancelTask(scheduler.remove("resetCheck"));
                      }
                    },
                    0,
                    20L));
  }

  @Override
  public void kill(Mine mine) {
    mine.killCountdown();
    if (scheduler == null) {
      return;
    }
    Integer resetCheck = scheduler.remove("resetCheck");
    if (resetCheck != null) {
      Bukkit.getScheduler().cancelTask(resetCheck);
    }
  }
}
