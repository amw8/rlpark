package rltoys.experiments.scheduling.interfaces;

import zephyr.plugin.core.api.signals.Listener;

public interface Scheduler {
  void add(Runnable job, Listener<JobDoneEvent> listener);

  void runAll();

  JobQueue queue();
}
