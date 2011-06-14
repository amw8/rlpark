package critterbot.examples;

import rltoys.environments.envio.observations.TStep;
import critterbot.CritterbotAgent;
import critterbot.actions.CritterbotAction;
import critterbot.actions.VoltageSpaceAction;
import critterbot.environment.CritterbotEnvironment;
import critterbot.environment.CritterbotRobot;
import critterbot.environment.CritterbotRobot.SoundMode;

public class ConstantAgent implements CritterbotAgent {
  private final CritterbotAction action;

  public ConstantAgent() {
    this(null);
  }

  public ConstantAgent(CritterbotAction action) {
    this.action = action;
  }

  @Override
  public CritterbotAction getAtp1(TStep step) {
    if (action != null)
      return action;
    return new VoltageSpaceAction(10, 10, 10);
  }

  @Override
  public String toString() {
    return action.toString();
  }

  public static void main(String[] args) {
    CritterbotEnvironment environment = new CritterbotRobot(SoundMode.None);
    environment.run(new ConstantAgent());
  }
}
