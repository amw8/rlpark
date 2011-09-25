package rltoys.experiments.parametersweep.onpolicy.internal;

import rltoys.experiments.parametersweep.parameters.Parameters;

public class RewardMonitors {
  public static OnPolicyRewardMonitor create(String prefix, int nbBins, int nbTimeSteps, int nbEpisode) {
    if (nbEpisode == 1)
      return new RewardMonitorAverage(prefix, nbBins, nbTimeSteps);
    return new RewardMonitorEpisode(prefix, nbBins, nbEpisode);
  }

  public static OnPolicyRewardMonitor create(int nbBins, Parameters parameters) {
    return create("", nbBins, parameters.maxEpisodeTimeSteps(), parameters.nbEpisode());
  }

  public static OnPolicyRewardMonitor create(String prefix, int nbBins, Parameters parameters) {
    return create(prefix, nbBins, parameters.maxEpisodeTimeSteps(), parameters.nbEpisode());
  }
}
