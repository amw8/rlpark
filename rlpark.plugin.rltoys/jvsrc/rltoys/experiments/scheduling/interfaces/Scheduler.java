package rltoys.experiments.scheduling.interfaces;

import java.util.List;


import zephyr.plugin.core.api.signals.Listener;

public interface Scheduler {
  void add(Runnable job, Listener<JobDoneEvent> listener);

  List<Runnable> runAll();

  JobQueue queue();
}
