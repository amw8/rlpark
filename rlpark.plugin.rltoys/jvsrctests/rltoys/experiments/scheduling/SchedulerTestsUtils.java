package rltoys.experiments.scheduling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.interfaces.Scheduler;
import rltoys.experiments.scheduling.internal.network.SocketClient;
import rltoys.experiments.scheduling.network.ServerScheduler;
import rltoys.experiments.scheduling.schedulers.Schedulers;
import zephyr.plugin.core.api.signals.Listener;

public class SchedulerTestsUtils {
  static class ClassResolutionListener implements Listener<String> {
    final List<String> names = new ArrayList<String>();

    @Override
    public void listen(String name) {
      names.add(name);
    }
  }

  static public class Job implements Runnable, Serializable {
    private static final long serialVersionUID = -1405281337225571229L;
    public boolean done = false;

    @Override
    public void run() {
      try {
        Thread.sleep((long) (Math.random() * 5));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      done = true;
    }
  }

  static public class JobDoneListener implements Listener<JobDoneEvent> {
    private final List<Runnable> done = new ArrayList<Runnable>();

    @Override
    public void listen(JobDoneEvent eventInfo) {
      done.add(eventInfo.done);
    }

    public int nbJobDone() {
      return done.size();
    }

    public List<Runnable> jobDone() {
      return done;
    }
  }

  static List<Job> createJobs(int nbJobs) {
    List<Job> jobs = new ArrayList<Job>();
    for (int i = 0; i < nbJobs; i++)
      jobs.add(new Job());
    return jobs;
  }

  static void assertAreDone(List<? extends Runnable> jobs, boolean isDone) {
    for (Runnable job : jobs)
      Assert.assertEquals(isDone, ((Job) job).done);
  }

  static public void testServerScheduler(ServerScheduler scheduler) {
    if (scheduler.isLocalSchedulingEnabled()) {
      testScheduler(scheduler);
      return;
    }
    ClassResolutionListener listener = new ClassResolutionListener();
    SocketClient.onClassRequested.connect(listener);
    testScheduler(scheduler);
    SocketClient.onClassRequested.disconnect(listener);
    Assert.assertTrue(listener.names.contains(Job.class.getName()));
  }

  static public void testScheduler(Scheduler scheduler) {
    final int NbJobs = 100;
    for (int i = 0; i < 2; i++) {
      List<Job> jobs = SchedulerTestsUtils.createJobs(NbJobs);
      SchedulerTestsUtils.assertAreDone(jobs, false);
      JobDoneListener listener = createListener();
      Schedulers.addAll(scheduler, jobs, listener);
      scheduler.runAll();
      Assert.assertEquals(NbJobs, listener.nbJobDone());
      SchedulerTestsUtils.assertAreDone(listener.jobDone(), true);
    }
  }

  static public JobDoneListener createListener() {
    return new JobDoneListener();
  }
}
