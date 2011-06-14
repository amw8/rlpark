package rltoys.experiments.parametersweep.onpolicy;

import rltoys.environments.envio.Runner;
import rltoys.environments.envio.Runner.RunnerEvent;
import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.interfaces.JobWithParameters;
import rltoys.experiments.parametersweep.parameters.Parameters;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Chrono;

public class SweepJob implements JobWithParameters {
  private static final long serialVersionUID = -1636763888764939471L;
  private final Parameters parameters;
  private final ContextOnPolicyEvaluation context;
  private long computationTime;
  double reward;
  private final ExperimentCounter counter;

  public SweepJob(ContextOnPolicyEvaluation context, Parameters parameters, ExperimentCounter counter) {
    this.context = context;
    this.parameters = parameters;
    this.counter = counter;
  }

  private Listener<RunnerEvent> createRewardListener() {
    return new Listener<Runner.RunnerEvent>() {
      @Override
      public void listen(RunnerEvent eventInfo) {
        reward += eventInfo.step.r_tp1;
      }
    };
  }

  @Override
  public void run() {
    Runner runner = context.createRunner(counter, parameters);
    reward = 0.0;
    runner.onTimeStep.connect(createRewardListener());
    Chrono chrono = new Chrono();
    runner.run();
    computationTime = chrono.getCurrentMillis();
    parameters.putResult("reward", reward);
    parameters.putResult("computationTime", computationTime);
    parameters.putResult("totalTimeStep", runner.runnerEvent().nbTotalTimeSteps);
  }

  @Override
  public Parameters parameters() {
    return parameters;
  }
}
