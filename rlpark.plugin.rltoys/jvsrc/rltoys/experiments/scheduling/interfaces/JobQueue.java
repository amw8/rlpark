package rltoys.experiments.scheduling.interfaces;

import zephyr.plugin.core.api.signals.Signal;

public interface JobQueue {
  Runnable request(boolean isLocal);

  void done(Runnable todo, Runnable done);

  Signal<JobDoneEvent> onJobDone();
}
