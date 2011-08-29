package rltoys.experiments.parametersweep.onpolicy.internal;

import java.util.Arrays;

import rltoys.environments.envio.Runner.RunnerEvent;
import rltoys.experiments.parametersweep.parameters.Parameters;
import zephyr.plugin.core.api.signals.Listener;

public class RewardMonitor implements Listener<RunnerEvent> {
  private final int[] starts;
  private final double[] rewards;
  private final double[] slices;
  private final int nbEpisode;
  private final int sliceSize;

  public RewardMonitor(int nbRewardCheckpoint, int nbTimeSteps, int nbEpisode) {
    this.nbEpisode = nbEpisode;
    int nbMeasurements = nbEpisode > 1 ? nbEpisode : nbTimeSteps;
    sliceSize = nbMeasurements / nbRewardCheckpoint;
    starts = createStartingPoints(nbRewardCheckpoint, nbMeasurements);
    rewards = new double[nbRewardCheckpoint];
    slices = new double[nbRewardCheckpoint];
  }

  private int[] createStartingPoints(int nbRewardCheckpoint, int nbMeasurements) {
    int[] starts = new int[nbRewardCheckpoint];
    for (int i = 0; i < starts.length; i++)
      starts[i] = i * sliceSize;
    return starts;
  }

  public void putResult(Parameters parameters) {
    for (int i = 0; i < starts.length; i++) {
      String startLabel = String.format("RewardStart%02d", i);
      parameters.put(startLabel, starts[i]);
      String rewardLabel = String.format("RewardCumulated%02d", i);
      parameters.put(rewardLabel, rewards[i]);
      String sliceLabel = String.format("RewardSlice%02d", i);
      parameters.put(sliceLabel, slices[i]);
    }
  }

  @Override
  public void listen(RunnerEvent eventInfo) {
    double reward = eventInfo.step.r_tp1;
    long current = nbEpisode > 1 ? eventInfo.episode : eventInfo.step.time;
    slices[(int) (current / sliceSize)] += reward;
    for (int i = 0; i < starts.length; i++) {
      if (starts[i] > current)
        break;
      rewards[i] += reward;
    }
  }

  public void worstResult() {
    Arrays.fill(rewards, -Float.MAX_VALUE);
    Arrays.fill(slices, -Float.MAX_VALUE);
  }
}
