package critterbot.examples;

import java.io.IOException;

import rltoys.environments.envio.observations.TStep;
import zephyr.plugin.core.api.monitoring.fileloggers.FileLogger;
import critterbot.CritterbotAgent;
import critterbot.actions.CritterbotAction;
import critterbot.actions.XYThetaAction;
import critterbot.environment.CritterbotEnvironment;
import critterbot.environment.CritterbotSimulator;

public class LoggedAgent implements CritterbotAgent {
  protected TStep lastStep;
  private final FileLogger logger;
  private final CritterbotEnvironment environment;

  public LoggedAgent(CritterbotEnvironment environment, String logfilepath) throws IOException {
    this.environment = environment;
    logger = new FileLogger(logfilepath);
  }

  @Override
  public CritterbotAction getAtp1(TStep step) {
    lastStep = step;
    logger.update(step.time);
    if (step.time > 1000) {
      environment.close();
      logger.close();
    }
    return new XYThetaAction(20, 0, Math.sin(step.time / 100.0) * Math.PI * 2);
  }

  public static void main(String[] args) throws IOException {
    CritterbotEnvironment environment = new CritterbotSimulator();
    environment.run(new LoggedAgent(environment, "/tmp/log.txt"));
  }
}
