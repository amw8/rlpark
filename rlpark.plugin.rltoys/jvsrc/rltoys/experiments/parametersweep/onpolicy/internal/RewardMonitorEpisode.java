package rltoys.experiments.parametersweep.onpolicy.internal;

import rltoys.environments.envio.Runner;
import rltoys.environments.envio.Runner.RunnerEvent;
import rltoys.experiments.parametersweep.reinforcementlearning.internal.AbstractRewardMonitor;
import zephyr.plugin.core.api.signals.Listener;

public class RewardMonitorEpisode extends AbstractRewardMonitor implements OnPolicyRewardMonitor {
  public RewardMonitorEpisode(int nbBins, int nbEpisode) {
    this("", nbBins, nbEpisode);
  }

  public RewardMonitorEpisode(String prefix, int nbBins, int nbEpisode) {
    super(prefix, createStartingPoints(nbBins, nbEpisode));
  }

  @Override
  public void connect(Runner runner) {
    runner.onEpisodeEnd.connect(new Listener<Runner.RunnerEvent>() {
      @Override
      public void listen(RunnerEvent eventInfo) {
        registerMeasurement(eventInfo.episode, eventInfo.episodeReward);
      }
    });
  }
}
