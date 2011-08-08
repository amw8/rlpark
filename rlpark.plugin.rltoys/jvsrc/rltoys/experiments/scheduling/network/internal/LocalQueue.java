package rltoys.experiments.scheduling.network.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.interfaces.JobQueue;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;


public class LocalQueue implements JobQueue {
  private final Map<Runnable, Listener<JobDoneEvent>> listeners = new HashMap<Runnable, Listener<JobDoneEvent>>();
  private final LinkedList<Runnable> waiting = new LinkedList<Runnable>();
  private final Set<Runnable> pending = new LinkedHashSet<Runnable>();
  private final List<Runnable> done = new ArrayList<Runnable>();
  private final Set<Runnable> pendingLocally = new HashSet<Runnable>();
  private final Signal<JobDoneEvent> onJobDone = new Signal<JobDoneEvent>();
  private int nbJobs = 0;
  private int nbJobsDone = 0;

  synchronized public void requestCancel(Runnable pendingJob) {
    if (!pending.contains(pendingJob))
      return;
    pending.remove(pendingJob);
    pendingLocally.remove(pendingJob);
    waiting.addFirst(pendingJob);
  }

  @Override
  synchronized public Runnable request(boolean isLocal) {
    assert nbJobs == nbJobs();
    Runnable job = waiting.poll();
    if (job != null) {
      pending.add(job);
      if (isLocal)
        pendingLocally.add(job);
      return job;
    }
    if (!isLocal)
      return null;
    for (Runnable candidate : pending) {
      if (pendingLocally.contains(candidate))
        continue;
      pendingLocally.add(candidate);
      return candidate;
    }
    return null;
  }

  @Override
  synchronized public void done(Runnable todo, Runnable done) {
    assert nbJobs == nbJobs();
    boolean removed = pending.remove(todo);
    if (!removed)
      return;
    this.done.add(done);
    pendingLocally.remove(todo);
    onJobDone(todo, done);
    nbJobsDone++;
    notifyAll();
  }

  private void onJobDone(Runnable todo, Runnable done) {
    JobDoneEvent event = new JobDoneEvent(todo, done);
    Listener<JobDoneEvent> listener = listeners.get(todo);
    if (listener != null)
      listener.listen(event);
    listeners.remove(todo);
    onJobDone.fire(event);
  }

  synchronized public boolean areAllDone() {
    return waiting.isEmpty() && pending.isEmpty();
  }

  synchronized public void add(Runnable job, Listener<JobDoneEvent> listener) {
    listeners.put(job, listener);
    waiting.add(job);
    nbJobs += 1;
  }

  @Override
  synchronized public int nbJobs() {
    return waiting.size() + pending.size() + done.size();
  }

  static public List<Runnable> waitAllDone(LocalQueue queue) {
    Messages.debug("To do " + queue.nbJobs());
    while (!queue.areAllDone()) {
      synchronized (queue) {
        try {
          queue.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
    Messages.debug("Done: " + queue.nbJobs());
    assert queue.nbJobs == queue.nbJobs();
    return queue.queryJobDone();
  }

  synchronized public List<Runnable> queryJobDone() {
    nbJobs -= done.size();
    ArrayList<Runnable> result = new ArrayList<Runnable>(done);
    done.clear();
    return result;
  }

  @Override
  public Signal<JobDoneEvent> onJobDone() {
    return onJobDone;
  }

  public int nbRemainingJobs() {
    return waiting.size() + pending.size();
  }

  public int nbJobsDone() {
    return nbJobsDone;
  }
}
