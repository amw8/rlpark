package rltoys.experiments.scheduling;

import java.util.List;

public class Schedulers {
  static public void addAll(Scheduler scheduler, List<Runnable> runnables) {
    for (Runnable runnable : runnables)
      scheduler.add(runnable);
  }
}
