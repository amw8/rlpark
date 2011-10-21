package rltoys.experiments.scheduling.schedulers;

import java.util.ArrayList;
import java.util.List;

import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.interfaces.Scheduler;
import rltoys.experiments.scheduling.queue.LocalQueue;
import zephyr.plugin.core.api.signals.Listener;

public class Schedulers {
  static public void addAll(Scheduler scheduler, List<Runnable> runnables) {
    addAll(scheduler, runnables, null);
  }

  static public void addAll(Scheduler scheduler, List<? extends Runnable> runnables, Listener<JobDoneEvent> listener) {
    ((LocalQueue) scheduler.queue()).add(new ArrayList<Runnable>(runnables).iterator(), listener);
  }
}
