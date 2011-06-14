package zephyr.plugin.critterview.commands;

import rlpark.plugin.rltoysview.commands.RunEnvironmentCommand;
import rltoys.environments.envio.Agent;
import critterbot.actions.CritterbotAction;
import critterbot.environment.CritterbotEnvironment;
import critterbot.environment.CritterbotRobot;
import critterbot.examples.ConstantAgent;

public class ConnectCritterbot extends RunEnvironmentCommand {
  @Override
  public CritterbotEnvironment createEnvironment() {
    return new CritterbotRobot(true);
  }

  @Override
  public Agent createAgent() {
    return new ConstantAgent(CritterbotAction.DoNothing);
  }
}
