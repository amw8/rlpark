package rlpark.plugin.rltoysview.commands;

import rlpark.plugin.robot.RobotEnvironment;
import rltoys.environments.envio.Agent;

public interface CommandRunnableFactory {

  Agent createAgent();

  RobotEnvironment createEnvironment();
}
