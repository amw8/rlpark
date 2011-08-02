package rltoys.experiments.scheduling;

import java.util.List;

import rltoys.experiments.scheduling.network.internal.JobQueue;
import rltoys.experiments.scheduling.network.internal.JobQueue.JobDoneEvent;
import zephyr.plugin.core.api.signals.Listener;

public interface Scheduler {
  void add(Runnable job, Listener<JobDoneEvent> listener);

  List<Runnable> runAll();

  JobQueue queue();
}
