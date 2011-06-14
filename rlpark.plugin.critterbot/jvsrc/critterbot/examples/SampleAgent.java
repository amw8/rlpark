package critterbot.examples;

import java.util.Random;

import rltoys.environments.envio.observations.TStep;
import rltoys.utils.Utils;
import critterbot.CritterbotAgent;
import critterbot.actions.WheelSpaceAction;
import critterbot.environment.CritterbotEnvironment;
import critterbot.environment.CritterbotRobot;

public class SampleAgent implements CritterbotAgent {
  final private Random random = new Random(0);
  final private int accelXIndex;
  final private int accelYIndex;

  public SampleAgent(CritterbotEnvironment environment) {
    accelXIndex = environment.legend().indexOf("AccelX");
    accelYIndex = environment.legend().indexOf("AccelY");
  }

  @Override
  public WheelSpaceAction getAtp1(TStep step) {
    TStep tobs = step;
    double accelX = tobs.o_t[accelXIndex];
    double accelY = tobs.o_t[accelYIndex];
    double factor = Utils.trunc(accelX * accelY, 400);
    return new WheelSpaceAction(random.nextDouble() * factor, -random.nextDouble() * factor, 10.0);
  }

  public static void main(String[] args) {
    CritterbotEnvironment environment = new CritterbotRobot();
    environment.run(new SampleAgent(environment));
  }
}
