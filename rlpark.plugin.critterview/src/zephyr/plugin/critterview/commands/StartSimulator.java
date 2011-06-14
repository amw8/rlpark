package zephyr.plugin.critterview.commands;

import rlpark.plugin.rltoysview.commands.RunEnvironmentCommand;
import rltoys.environments.envio.Agent;
import critterbot.actions.CritterbotAction;
import critterbot.environment.CritterbotEnvironment;
import critterbot.environment.CritterbotSimulator;
import critterbot.examples.ConstantAgent;

public class StartSimulator extends RunEnvironmentCommand {
  @Override
  public CritterbotEnvironment createEnvironment() {
    return new CritterbotSimulator();
  }

  @Override
  public Agent createAgent() {
    return new ConstantAgent(CritterbotAction.DoNothing);
  }
}
