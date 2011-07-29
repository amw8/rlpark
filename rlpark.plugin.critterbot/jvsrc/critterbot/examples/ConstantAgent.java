package critterbot.examples;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.Agent;
import zephyr.plugin.core.api.synchronization.Clock;
import critterbot.actions.CritterbotAction;
import critterbot.actions.VoltageSpaceAction;
import critterbot.environment.CritterbotEnvironment;
import critterbot.environment.CritterbotRobot;
import critterbot.environment.CritterbotRobot.SoundMode;

public class ConstantAgent implements Runnable, Agent {
  private final CritterbotEnvironment environment = new CritterbotRobot(SoundMode.None);
  private final Clock clock = new Clock("ConstantAgent");
  private final CritterbotAction action;

  public ConstantAgent() {
    this(CritterbotAction.DoNothing);
  }

  public ConstantAgent(CritterbotAction action) {
    this.action = action;
  }

  @Override
  public Action getAtp1(double[] obs) {
    return action;
  }

  @Override
  public void run() {
    while (clock.tick() && !environment.isClosed())
      environment.sendAction(action);
  }

  public static void main(String[] args) {
    new ConstantAgent(new VoltageSpaceAction(10, 10, 10)).run();
  }
}
