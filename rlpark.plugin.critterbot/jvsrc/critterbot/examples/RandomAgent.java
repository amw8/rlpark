package critterbot.examples;

import java.util.Random;

import rltoys.environments.envio.observations.TStep;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import critterbot.CritterbotAgent;
import critterbot.actions.CritterbotAction;
import critterbot.actions.XYThetaAction;
import critterbot.environment.CritterbotDrops.LedMode;
import critterbot.environment.CritterbotEnvironment;
import critterbot.environment.CritterbotRobot;

public class RandomAgent implements CritterbotAgent {
  private final CritterbotAction[] actions;
  private final Random random;
  private final long latencyMillis;
  @Monitor
  private long lastActionChange;
  @Monitor
  private int actionIndex = -1;
  @Monitor
  protected boolean actionPickedUp;
  private CritterbotEnvironment environment;

  public RandomAgent(CritterbotEnvironment environment) {
    this(new Random(0), 1.0, XYThetaAction.sevenActions());
    this.environment = environment;
  }

  public RandomAgent(Random random, double latencySeconds, CritterbotAction... actions) {
    this.random = random;
    this.actions = actions;
    latencyMillis = (long) (latencySeconds * 1e3);
  }

  @Override
  public CritterbotAction getAtp1(TStep step) {
    if (actionIndex < 0 || step.time - lastActionChange >= latencyMillis) {
      actionIndex = random.nextInt(actions.length);
      lastActionChange = step.time;
      actionPickedUp = true;
    } else
      actionPickedUp = false;
    environment.setLedMode(LedMode.BALL);
    return actions[actionIndex];
  }

  public static void main(String[] args) {
    CritterbotEnvironment environment = new CritterbotRobot();
    environment.run(new RandomAgent(environment));
  }
}
