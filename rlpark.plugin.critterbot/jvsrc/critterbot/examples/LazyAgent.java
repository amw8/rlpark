package critterbot.examples;

import rltoys.environments.envio.observations.TStep;
import critterbot.CritterbotAgent;
import critterbot.actions.WheelSpaceAction;
import critterbot.environment.CritterbotEnvironment;
import critterbot.environment.CritterbotRobot;

public class LazyAgent implements CritterbotAgent {
  final private WheelSpaceAction action = new WheelSpaceAction(0.0, 0.0, 0.0);

  public LazyAgent() {
  }

  @Override
  public WheelSpaceAction getAtp1(TStep step) {
    return action;
  }

  public static void main(String[] args) {
    CritterbotEnvironment environment = new CritterbotRobot();
    environment.run(new LazyAgent());
  }
}
