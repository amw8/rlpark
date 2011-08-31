package rltoys.experiments.scheduling.interfaces;

public interface Scheduler {
  void runAll();

  JobQueue queue();

  void dispose();
}
