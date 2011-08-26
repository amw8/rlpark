package rltoys.experiments.parametersweep.onpolicy;

import rltoys.environments.envio.Runner;
import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.interfaces.JobWithParameters;
import rltoys.experiments.parametersweep.parameters.Parameters;
import zephyr.plugin.core.api.synchronization.Chrono;

public class SweepJob implements JobWithParameters {
  private static final long serialVersionUID = -1636763888764939471L;
  private final Parameters parameters;
  private final ContextOnPolicyEvaluation context;
  private long computationTime;
  private final int counter;

  public SweepJob(ContextOnPolicyEvaluation context, Parameters parameters, ExperimentCounter counter) {
    this.context = context;
    this.parameters = parameters;
    this.counter = counter.currentIndex();
  }

  @Override
  public void run() {
    Runner runner = context.createRunner(counter, parameters);
    RewardMonitor rewardMonitor = context.createRewardMonitor(parameters);
    runner.onTimeStep.connect(rewardMonitor);
    Chrono chrono = new Chrono();
    boolean diverged = false;
    try {
      runner.run();
    } catch (Throwable e) {
      rewardMonitor.worstResult();
      diverged = true;
    }
    computationTime = chrono.getCurrentMillis();
    rewardMonitor.putResult(parameters);
    parameters.putResult("computationTime", diverged ? -1 : computationTime);
    parameters.putResult("totalTimeStep", runner.runnerEvent().nbTotalTimeSteps);
  }

  @Override
  public Parameters parameters() {
    return parameters;
  }
}
