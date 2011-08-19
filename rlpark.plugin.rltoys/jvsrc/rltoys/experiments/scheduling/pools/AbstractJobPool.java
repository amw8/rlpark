package rltoys.experiments.scheduling.pools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.interfaces.JobPool;
import rltoys.experiments.scheduling.interfaces.Scheduler;
import rltoys.experiments.scheduling.network.internal.LocalQueue;
import zephyr.plugin.core.api.signals.Listener;


public abstract class AbstractJobPool implements JobPool {
  class RunnableIterator implements Iterator<Runnable> {
    private final Iterator<Runnable> iterator;

    RunnableIterator(Iterator<Runnable> iterator) {
      this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public Runnable next() {
      Runnable next = iterator.next();
      jobSubmitted.add(next);
      return next;
    }

    @Override
    public void remove() {
    }
  }

  protected final JobPoolListener onAllJobDone;
  private final Listener<JobDoneEvent> poolListener = new Listener<JobDoneEvent>() {
    @Override
    public void listen(JobDoneEvent eventInfo) {
      onJobDone(eventInfo);
    }
  };
  protected final Listener<JobDoneEvent> onJobDone;
  final List<Runnable> jobSubmitted = new ArrayList<Runnable>();
  protected RunnableIterator jobIterator = null;

  public AbstractJobPool(JobPoolListener onAllJobDone, Listener<JobDoneEvent> onJobDone) {
    this.onAllJobDone = onAllJobDone;
    this.onJobDone = onJobDone;
  }

  protected boolean hasBeenSubmitted() {
    return jobIterator != null;
  }

  @Override
  public void submitTo(Scheduler scheduler) {
    checkHasBeenSubmitted();
    jobIterator = new RunnableIterator(createIterator());
    ((LocalQueue) scheduler.queue()).add(jobIterator, poolListener);
  }

  protected void checkHasBeenSubmitted() {
    if (hasBeenSubmitted())
      throw new RuntimeException("The pool has already been submitted");
  }

  protected void onJobDone(JobDoneEvent event) {
    assert jobSubmitted.contains(event.todo);
    if (onJobDone != null)
      onJobDone.listen(event);
    jobSubmitted.remove(event.todo);
    if (jobSubmitted.isEmpty() && !jobIterator.hasNext())
      onAllJobDone.listen(this);
  }

  abstract protected Iterator<Runnable> createIterator();
}