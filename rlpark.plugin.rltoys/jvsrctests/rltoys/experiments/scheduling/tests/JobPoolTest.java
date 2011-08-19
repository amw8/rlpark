package rltoys.experiments.scheduling.tests;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import rltoys.experiments.scheduling.JobPool;
import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.schedulers.LocalScheduler;
import rltoys.experiments.scheduling.tests.SchedulerTestsUtils.Job;
import rltoys.experiments.scheduling.tests.SchedulerTestsUtils.JobDoneListener;
import zephyr.plugin.core.api.signals.Listener;

public class JobPoolTest {
  static public class JobPoolListener implements Listener<JobPool> {
    int poolDone = 0;

    @Override
    public void listen(JobPool pool) {
      poolDone++;
    }
  }

  final static private int NbJobs = 100;
  final static private int NbPool = 5;

  @Test
  public void testJobPool() {
    LocalScheduler scheduler = new LocalScheduler(10);
    JobDoneListener jobListener = SchedulerTestsUtils.createListener();
    JobPoolListener poolListener = new JobPoolListener();
    for (int i = 0; i < NbPool; i++) {
      JobPool jobPool = createPool(poolListener, jobListener);
      jobPool.submitTo(scheduler);
    }
    scheduler.runAll();
    Assert.assertEquals(NbPool, poolListener.poolDone);
    Assert.assertEquals(NbJobs * NbPool, jobListener.nbJobDone());
    SchedulerTestsUtils.assertAreDone(jobListener.jobDone(), true);
  }

  private JobPool createPool(Listener<JobPool> poolListener, Listener<JobDoneEvent> jobListener) {
    List<Job> jobs = SchedulerTestsUtils.createJobs(NbJobs);
    SchedulerTestsUtils.assertAreDone(jobs, false);
    JobPool pool = new JobPool(poolListener, jobListener);
    for (Job job : jobs)
      pool.add(job);
    return pool;
  }
}
