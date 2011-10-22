package rlpark.plugin.irobotview.runnable;

import rlpark.plugin.irobot.robots.CreateRobot;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.synchronization.Clock;

public class CreateRunnable implements Runnable {
  private final CreateRobot createRobot;
  private final Clock clock = new Clock("Create");

  public CreateRunnable() {
    this(new CreateRobot());
  }

  public CreateRunnable(CreateRobot createRobot) {
    this.createRobot = createRobot;
    Zephyr.advertise(clock, createRobot);
  }

  @Override
  public void run() {
    while (!createRobot.isClosed() && clock.tick())
      createRobot.waitNewObs();
  }
}
