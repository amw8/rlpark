package rltoys.experiments.parametersweep.reinforcementlearning;

import rltoys.environments.envio.Runner.RunnerEvent;
import rltoys.experiments.parametersweep.parameters.Parameters;
import zephyr.plugin.core.api.signals.Listener;

public class RewardMonitor implements Listener<RunnerEvent> {
  private final int[] starts;
  private final double[] rewards;
  private final double[] slices;
  private final int nbEpisode;
  private final int sliceSize;
  private int currentSlice;
  private final String prefix;

  public RewardMonitor(int nbRewardCheckpoint, int nbTimeSteps, int nbEpisode) {
    this("", nbRewardCheckpoint, nbTimeSteps, nbEpisode);
  }

  public RewardMonitor(String prefix, int nbRewardCheckpoint, int nbTimeSteps, int nbEpisode) {
    this.prefix = prefix;
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
      String startLabel = String.format("%sRewardStart%02d", prefix, i);
      parameters.put(startLabel, starts[i]);
      String rewardLabel = String.format("%sRewardCumulated%02d", prefix, i);
      parameters.put(rewardLabel, rewards[i]);
      String sliceLabel = String.format("%sRewardSlice%02d", prefix, i);
      parameters.put(sliceLabel, slices[i]);
    }
  }

  @Override
  public void listen(RunnerEvent eventInfo) {
    double reward = eventInfo.step.r_tp1;
    long current = nbEpisode > 1 ? eventInfo.episode : eventInfo.step.time;
    currentSlice = (int) (current / sliceSize);
    slices[currentSlice] += reward;
    for (int i = 0; i < starts.length; i++) {
      if (starts[i] > current)
        break;
      rewards[i] += reward;
    }
  }

  public void worstResultUntilEnd() {
    for (int i = 0; i < rewards.length; i++)
      rewards[i] = -Float.MAX_VALUE;
    for (int i = currentSlice; i < rewards.length; i++)
      slices[i] = -Float.MAX_VALUE;
  }
}
