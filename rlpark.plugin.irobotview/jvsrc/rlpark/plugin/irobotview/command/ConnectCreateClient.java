package rlpark.plugin.irobotview.command;

import rlpark.plugin.irobot.data.CreateAction;
import rlpark.plugin.irobot.examples.ConstantAgent;
import rlpark.plugin.irobot.robots.CreateRobot;
import rlpark.plugin.rltoysview.commands.RunEnvironmentCommand;
import rlpark.plugin.robot.RobotEnvironment;
import rltoys.environments.envio.Agent;

public class ConnectCreateClient extends RunEnvironmentCommand {
  @Override
  public Agent createAgent() {
    return new ConstantAgent(new CreateAction(null));
  }

  @Override
  public RobotEnvironment createEnvironment() {
    return new CreateRobot();
  }
}
