package rltoys.experiments.parametersweep.onpolicy;

import java.io.IOException;
import java.io.Serializable;

import rltoys.environments.envio.Runner;
import rltoys.environments.envio.Runner.RunnerEvent;
import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.parameters.Parameters;
import zephyr.plugin.core.api.monitoring.fileloggers.LoggerRow;
import zephyr.plugin.core.api.signals.Listener;

public class LearningCurveJob implements Runnable, Serializable {
  private static final long serialVersionUID = -5212166519929349880L;
  private final Parameters parameters;
  private final ContextOnPolicyEvaluation context;
  private final ExperimentCounter counter;

  public LearningCurveJob(ContextOnPolicyEvaluation context, Parameters parameters, ExperimentCounter counter) {
    this.context = context;
    this.parameters = parameters;
    this.counter = counter;
  }

  protected Listener<RunnerEvent> createRewardListener(final LoggerRow loggerRow) {
    return new Listener<Runner.RunnerEvent>() {
      @Override
      public void listen(RunnerEvent eventInfo) {
        loggerRow.writeRow(eventInfo.step.time, eventInfo.step.r_tp1);
      }
    };
  }

  protected Listener<RunnerEvent> createEpisodeListener(final LoggerRow loggerRow) {
    return new Listener<Runner.RunnerEvent>() {
      @Override
      public void listen(RunnerEvent eventInfo) {
        loggerRow.writeRow(eventInfo.episode, eventInfo.step.time);
      }
    };
  }

  protected void setupEpisodeListener(Runner runner, LoggerRow loggerRow) {
    loggerRow.writeLegend("Episode", "Steps");
    runner.onEpisodeEnd.connect(createEpisodeListener(loggerRow));
  }

  protected void setupRewardListener(Runner runner, LoggerRow loggerRow) {
    loggerRow.writeLegend("Time", "Reward");
    runner.onTimeStep.connect(createRewardListener(loggerRow));
  }

  @Override
  public void run() {
    Runner runner = context.createRunner(counter.currentIndex(), parameters);
    String fileName = counter.folderFilename(context.folderPath(), context.fileName());
    System.out.println(fileName);
    LoggerRow loggerRow = null;
    try {
      loggerRow = new LoggerRow(fileName, false);
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    if (parameters.nbEpisode() == 1)
      setupRewardListener(runner, loggerRow);
    else
      setupEpisodeListener(runner, loggerRow);
    runner.run();
    loggerRow.close();
  }
}
