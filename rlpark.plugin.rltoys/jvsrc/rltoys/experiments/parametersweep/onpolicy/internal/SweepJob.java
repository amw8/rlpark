package rltoys.experiments.parametersweep.onpolicy.internal;

import rltoys.environments.envio.Runner;
import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.interfaces.JobWithParameters;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.internal.RewardMonitor;
import zephyr.plugin.core.api.synchronization.Chrono;

public class SweepJob implements JobWithParameters {
  private static final long serialVersionUID = -1636763888764939471L;
  private final Parameters parameters;
  private final OnPolicyEvaluationContext context;
  private long computationTime;
  private final int counter;

  public SweepJob(OnPolicyEvaluationContext context, Parameters parameters, ExperimentCounter counter) {
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
      rewardMonitor.worstResultUntilEnd();
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
