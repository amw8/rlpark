package rltoys.experiments.scheduling.network.internal;

import zephyr.plugin.core.api.signals.Signal;


public interface JobQueue {
  static public class JobDoneEvent {
    final public Runnable todo;
    final public Runnable done;

    public JobDoneEvent(Runnable todo, Runnable done) {
      this.todo = todo;
      this.done = done;
    }
  }

  Runnable request(boolean isLocal);

  void done(Runnable todo, Runnable done);

  Signal<JobDoneEvent> onJobDone();

  int nbJobs();
}
