package rltoys.experiments.scheduling.schedulers;

import java.util.List;

import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.interfaces.Scheduler;

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
