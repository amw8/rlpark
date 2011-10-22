package rlpark.plugin.irobotview.runnable;

import rlpark.plugin.irobot.robots.RoombaRobot;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.synchronization.Clock;

public class RoombaRunnable implements Runnable {
  private final RoombaRobot roombaRobot;
  private final Clock clock = new Clock("Roomba");

  public RoombaRunnable() {
    this(new RoombaRobot());
  }

  public RoombaRunnable(RoombaRobot roombaRobot) {
    this.roombaRobot = roombaRobot;
    Zephyr.advertise(clock, roombaRobot);
  }

  @Override
  public void run() {
    while (!roombaRobot.isClosed() && clock.tick())
      roombaRobot.waitNewObs();
  }
}
