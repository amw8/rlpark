package rlpark.plugin.irobotview.command;

import rlpark.plugin.irobot.robots.RoombaRobot;
import rlpark.plugin.irobotview.runnable.RoombaRunnable;
import zephyr.ZephyrCore;
import zephyr.plugin.core.RunnableFactory;

public class ConnectRoomba extends EnvironmentSerialPortCommand {
  @Override
  protected void startRunnable(final String serialPortPath) {
    ZephyrCore.start(new RunnableFactory() {
      @Override
      public Runnable createRunnable() {
        return new RoombaRunnable(new RoombaRobot(serialPortPath));
      }
    });
  }
}
