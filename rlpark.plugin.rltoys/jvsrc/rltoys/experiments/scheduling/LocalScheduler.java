package rltoys.experiments.scheduling;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import rltoys.experiments.scheduling.network.internal.JobQueue;
import rltoys.experiments.scheduling.network.internal.LocalQueue;
import zephyr.plugin.core.api.synchronization.Chrono;

public class LocalScheduler implements Scheduler {
  protected class RunnableProcessor implements Runnable {
    @Override
    public void run() {
      try {
        Runnable runnable = runnables.request(true);
        while (runnable != null) {
          runnable.run();
          runnables.done(runnable, runnable);
          runnable = runnables.request(true);
        }
      } catch (Throwable exception) {
        if (exceptionThrown == null)
          exceptionThrown = exception;
        exception.printStackTrace();
      }
    }
  }

  Throwable exceptionThrown;
  private final ExecutorService executor;
  private final List<RunnableProcessor> updaters = new ArrayList<RunnableProcessor>();
  private final Future<?>[] futurs;
  protected final JobQueue runnables;
  private final Chrono chrono = new Chrono();
  protected final int nbThread;

  public LocalScheduler() {
    this(getDefaultNbThreads() + 1);
  }

  public LocalScheduler(int nbThread) {
    this(nbThread, new LocalQueue());
  }

  public LocalScheduler(JobQueue runnables) {
    this(getDefaultNbThreads() + 1, runnables);
  }

  public static int getDefaultNbThreads() {
    return Runtime.getRuntime().availableProcessors();
  }

  public LocalScheduler(int nbThread, List<? extends Runnable> jobs) {
    this(nbThread, new LocalQueue(jobs));
  }

  public LocalScheduler(int nbThread, JobQueue runnables) {
    this.nbThread = nbThread;
    this.runnables = runnables;
    for (int i = 0; i < nbThread; i++)
      updaters.add(new RunnableProcessor());
    futurs = new Future<?>[nbThread];
    executor = Executors.newFixedThreadPool(nbThread);
  }

  synchronized public void start() {
    exceptionThrown = null;
    chrono.start();
    for (int i = 0; i < updaters.size(); i++)
      if (futurs[i] == null || futurs[i].isDone())
        futurs[i] = executor.submit(updaters.get(i));
  }

  @Override
  public List<Runnable> runAll() {
    start();
    for (Future<?> future : futurs)
      try {
        future.get();
      } catch (Exception e) {
        exceptionThrown = e;
        break;
      }
    if (exceptionThrown != null)
      throw new RuntimeException(exceptionThrown);
    if (runnables instanceof LocalQueue)
      return ((LocalQueue) runnables).queryJobDone();
    return null;
  }

  public long updateTimeAverage() {
    return chrono.getCurrentNano();
  }

  synchronized public void dispose() {
    executor.shutdown();
  }

  public Chrono chrono() {
    return chrono;
  }

  @Override
  public JobQueue queue() {
    return runnables;
  }

  public Throwable exceptionOccured() {
    return exceptionThrown;
  }

  @Override
  public void add(Runnable job) {
    ((LocalQueue) runnables).add(job);
  }
}
