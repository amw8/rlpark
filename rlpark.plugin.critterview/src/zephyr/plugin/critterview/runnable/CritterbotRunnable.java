package zephyr.plugin.critterview.runnable;

import rlpark.plugin.rltoysview.commands.AgentRobotRunnable;
import rlpark.plugin.rltoysview.commands.RunEnvironmentCommand;
import rlpark.plugin.robot.RobotEnvironment;
import rltoys.environments.envio.Agent;
import critterbot.actions.CritterbotAction;
import critterbot.environment.CritterbotRobot;
import critterbot.examples.ConstantAgent;

public class CritterbotRunnable extends AgentRobotRunnable {
  public CritterbotRunnable() {
    super(new RunEnvironmentCommand() {
      @Override
      public Agent createAgent() {
        return new ConstantAgent(CritterbotAction.DoNothing);
      }

      @Override
      public RobotEnvironment createEnvironment() {
        return new CritterbotRobot(true);
      }
    });
  }
}