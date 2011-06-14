package rlpark.plugin.irobotview.command;

import rlpark.plugin.irobot.robots.CreateRobot;
import rlpark.plugin.irobot.robots.IRobotEnvironment;

public class ConnectCreate extends EnvironmentSerialPortCommand {
  @Override
  protected IRobotEnvironment createIRobotEnvironment(String serialPortPath) {
    return new CreateRobot(serialPortPath);
  }
}
