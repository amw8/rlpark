package rltoys.experiments.scheduling.queue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Semaphore;

import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.interfaces.JobQueue;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;


public class LocalQueue implements JobQueue {
  static class JobInfo {
    final Runnable job;
    final Listener<JobDoneEvent> listener;

    JobInfo(Runnable job, Listener<JobDoneEvent> listener) {
      this.job = job;
      this.listener = listener;
    }
  }

  private final Map<Iterator<? extends Runnable>, Listener<JobDoneEvent>> listeners = new HashMap<Iterator<? extends Runnable>, Listener<JobDoneEvent>>();
  private final LinkedList<Iterator<? extends Runnable>> waiting = new LinkedList<Iterator<? extends Runnable>>();
  private final Map<Runnable, Listener<JobDoneEvent>> pending = new LinkedHashMap<Runnable, Listener<JobDoneEvent>>();
  private final Signal<JobDoneEvent> onJobDone = new Signal<JobDoneEvent>();
  private final LinkedList<JobInfo> canceled = new LinkedList<JobInfo>();
  private Iterator<? extends Runnable> currentJobIterator = null;
  private int nbJobsDone = 0;

  synchronized public void requestCancel(Runnable pendingJob) {
    assert pending.containsKey(pendingJob);
    Listener<JobDoneEvent> listener = pending.remove(pendingJob);
    canceled.addFirst(new JobInfo(pendingJob, listener));
  }

  private JobInfo findJob() {
    JobInfo jobInfo = canceled.poll();
    while (jobInfo == null) {
      if (currentJobIterator == null)
        currentJobIterator = waiting.poll();
      if (currentJobIterator == null)
        break;
      if (currentJobIterator.hasNext()) {
        Runnable job = currentJobIterator.next();
        jobInfo = new JobInfo(job, listeners.get(currentJobIterator));
      }
      if (!currentJobIterator.hasNext()) {
        listeners.remove(currentJobIterator);
        currentJobIterator = null;
      }
    }
    return jobInfo;
  }

  @Override
  synchronized public Runnable request() {
    JobInfo jobInfo = findJob();
    if (jobInfo == null)
      return null;
    pending.put(jobInfo.job, jobInfo.listener);
    return jobInfo.job;
  }

  @Override
  synchronized public void done(Runnable todo, Runnable done) {
    boolean removed = pending.containsKey(todo);
    if (!removed)
      return;
    Listener<JobDoneEvent> listener = pending.remove(todo);
    onJobDone(todo, done, listener);
    nbJobsDone++;
  }

  private void onJobDone(Runnable todo, Runnable done, Listener<JobDoneEvent> listener) {
    JobDoneEvent event = new JobDoneEvent(todo, done);
    if (listener != null)
      listener.listen(event);
    onJobDone.fire(event);
  }

  synchronized public boolean areAllDone() {
    return currentJobIterator == null && waiting.isEmpty() && pending.isEmpty() && canceled.isEmpty();
  }

  synchronized public void add(Iterator<? extends Runnable> jobIterator, Listener<JobDoneEvent> listener) {
    listeners.put(jobIterator, listener);
    waiting.add(jobIterator);
  }

  static public void waitAllDone(LocalQueue queue) {
    final Semaphore semaphore = new Semaphore(0);
    final Listener<JobDoneEvent> listener = new Listener<JobDoneEvent>() {
      @Override
      public void listen(JobDoneEvent eventInfo) {
        semaphore.release();
      }
    };
    queue.onJobDone.connect(listener);
    while (!queue.areAllDone()) {
      try {
        semaphore.acquire();
      } catch (InterruptedException e) {
      }
    }
    queue.onJobDone.disconnect(listener);
  }

  @Override
  public Signal<JobDoneEvent> onJobDone() {
    return onJobDone;
  }

  public int nbJobsDone() {
    return nbJobsDone;
  }
}
