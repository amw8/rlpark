package rlpark.plugin.irobotview.command;

import rlpark.plugin.irobot.robots.IRobotEnvironment;
import rlpark.plugin.irobot.robots.RoombaRobot;

public class ConnectRoomba extends EnvironmentSerialPortCommand {
  @Override
  protected IRobotEnvironment createIRobotEnvironment(String serialPortPath) {
    return new RoombaRobot(serialPortPath);
  }
}
