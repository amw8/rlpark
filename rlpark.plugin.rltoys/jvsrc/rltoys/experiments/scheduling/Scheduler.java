package rltoys.experiments.scheduling;

import java.util.List;

import rltoys.experiments.scheduling.network.internal.JobQueue;

public interface Scheduler {
  void add(Runnable job);

  List<Runnable> runAll();

  JobQueue queue();
}
