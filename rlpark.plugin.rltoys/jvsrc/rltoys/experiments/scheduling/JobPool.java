package rltoys.experiments.scheduling;

import java.util.LinkedHashMap;
import java.util.Map;

import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.interfaces.Scheduler;

import zephyr.plugin.core.api.signals.Listener;


public class JobPool {
  private final Listener<JobPool> onAllJobDone;
  private final Listener<JobDoneEvent> poolListener = new Listener<JobDoneEvent>() {
    @Override
    public void listen(JobDoneEvent eventInfo) {
      onJobDone(eventInfo);
    }
  };
  private final Map<Runnable, Listener<JobDoneEvent>> jobs = new LinkedHashMap<Runnable, Listener<JobDoneEvent>>();
  private boolean submitted = false;

  public JobPool(Listener<JobPool> onAllJobDone) {
    this.onAllJobDone = onAllJobDone;
  }

  public void add(Runnable job, Listener<JobDoneEvent> listener) {
    if (submitted)
      throw new RuntimeException("No jobs can be added to a pool submitted");
    jobs.put(job, listener);
  }

  public void submitTo(Scheduler scheduler) {
    if (submitted)
      throw new RuntimeException("The pool has already been submitted");
    submitted = true;
    for (Runnable job : jobs.keySet())
      scheduler.add(job, poolListener);
  }

  protected void onJobDone(JobDoneEvent event) {
    assert jobs.containsKey(event.todo);
    Listener<JobDoneEvent> listener = jobs.get(event.todo);
    if (listener != null)
      listener.listen(event);
    jobs.remove(event.todo);
    if (jobs.isEmpty())
      onAllJobDone.listen(this);
  }
}
