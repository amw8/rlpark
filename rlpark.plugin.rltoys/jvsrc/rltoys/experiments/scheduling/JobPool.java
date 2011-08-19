package rltoys.experiments.scheduling;

import java.util.ArrayList;
import java.util.List;

import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.interfaces.Scheduler;
import rltoys.experiments.scheduling.schedulers.Schedulers;
import zephyr.plugin.core.api.signals.Listener;


public class JobPool {
  private final Listener<JobPool> onAllJobDone;
  private final Listener<JobDoneEvent> poolListener = new Listener<JobDoneEvent>() {
    @Override
    public void listen(JobDoneEvent eventInfo) {
      onJobDone(eventInfo);
    }
  };
  private final List<Runnable> jobs = new ArrayList<Runnable>();
  private final Listener<JobDoneEvent> onJobDone;
  private boolean submitted = false;

  public JobPool(Listener<JobPool> onAllJobDone, Listener<JobDoneEvent> onJobDone) {
    this.onAllJobDone = onAllJobDone;
    this.onJobDone = onJobDone;
  }

  public void add(Runnable job) {
    if (submitted)
      throw new RuntimeException("No jobs can be added to a pool submitted");
    jobs.add(job);
  }

  public void submitTo(Scheduler scheduler) {
    if (submitted)
      throw new RuntimeException("The pool has already been submitted");
    submitted = true;
    Schedulers.addAll(scheduler, jobs, poolListener);
  }

  protected void onJobDone(JobDoneEvent event) {
    assert jobs.contains(event.todo);
    if (onJobDone != null)
      onJobDone.listen(event);
    jobs.remove(event.todo);
    if (jobs.isEmpty())
      onAllJobDone.listen(this);
  }
}
