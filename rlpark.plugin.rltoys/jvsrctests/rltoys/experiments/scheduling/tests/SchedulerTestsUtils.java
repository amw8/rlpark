package rltoys.experiments.scheduling.tests;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import rltoys.experiments.scheduling.Scheduler;
import rltoys.experiments.scheduling.network.ServerScheduler;
import rltoys.experiments.scheduling.network.internal.NetworkClassLoader;

public class SchedulerTestsUtils {
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

  static private List<Job> createJobs(int nbJobs) {
    List<Job> jobs = new ArrayList<Job>();
    for (int i = 0; i < nbJobs; i++)
      jobs.add(new Job());
    return jobs;
  }

  static private void assertAreDone(List<? extends Runnable> jobs, boolean isDone) {
    for (Runnable job : jobs)
      Assert.assertEquals(isDone, ((Job) job).done);
  }

  static public void testScheduler(Scheduler scheduler) {
    for (int i = 0; i < 2; i++) {
      List<Job> jobs = SchedulerTestsUtils.createJobs(100);
      SchedulerTestsUtils.assertAreDone(jobs, false);
      for (Job job : jobs)
        scheduler.add(job);
      List<Runnable> done = scheduler.runAll();
      SchedulerTestsUtils.assertAreDone(done, true);
      // Checking if, when we have a ServerScheduler, some code has been
      // transfered between the client and the server
      if (scheduler instanceof ServerScheduler)
        Assert.assertTrue(((ServerScheduler) scheduler).isLocalSchedulingEnabled() ||
                          NetworkClassLoader.downloaded() > 0);
    }
  }
}
