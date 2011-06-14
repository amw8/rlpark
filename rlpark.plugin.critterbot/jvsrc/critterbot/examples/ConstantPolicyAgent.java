package critterbot.examples;

import java.io.IOException;

import rltoys.algorithms.representations.features.Periodic;
import rltoys.environments.envio.observations.TStep;
import zephyr.plugin.core.api.monitoring.fileloggers.FileLogger;
import critterbot.CritterbotAgent;
import critterbot.actions.WheelSpaceAction;
import critterbot.environment.CritterbotEnvironment;
import critterbot.environment.CritterbotSimulator;

public class ConstantPolicyAgent implements CritterbotAgent {
  static private double speed = 20.0;
  private final Periodic[] periodics = new Periodic[] { new Periodic(200), new Periodic(500), new Periodic(1000) };
  private final FileLogger logger;
  private int time = 0;

  public ConstantPolicyAgent(CritterbotEnvironment environment) throws IOException {
    logger = new FileLogger("./data/constantpolicy.crtrlog");
    logger.add(environment);
  }

  @Override
  public WheelSpaceAction getAtp1(TStep step) {
    logger.update(time);
    for (Periodic periodic : periodics)
      periodic.update();
    time++;
    if (time > 10e5) {
      logger.close();
      return null;
    }
    return new WheelSpaceAction(periodics[0].value() * speed,
                                periodics[1].value() * speed,
                                periodics[2].value() * speed);
  }

  public static void main(String[] args) throws IOException {
    CritterbotEnvironment environment = new CritterbotSimulator();
    environment.run(new ConstantPolicyAgent(environment));
  }
}
