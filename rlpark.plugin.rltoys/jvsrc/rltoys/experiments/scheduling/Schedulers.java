package rltoys.experiments.scheduling;

import java.util.List;

import rltoys.experiments.scheduling.network.internal.JobQueue.JobDoneEvent;
import zephyr.plugin.core.api.signals.Listener;

public class Schedulers {
  static public void addAll(Scheduler scheduler, List<Runnable> runnables) {
    addAll(scheduler, runnables, null);
  }

  static public void addAll(Scheduler scheduler, List<Runnable> runnables, Listener<JobDoneEvent> listener) {
    for (Runnable runnable : runnables)
      scheduler.add(runnable, listener);
  }
}
