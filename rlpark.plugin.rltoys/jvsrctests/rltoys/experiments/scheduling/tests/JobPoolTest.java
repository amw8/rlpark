package rltoys.experiments.scheduling.tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import rltoys.experiments.scheduling.JobPool;
import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.schedulers.LocalScheduler;
import rltoys.experiments.scheduling.tests.SchedulerTestsUtils.Job;
import zephyr.plugin.core.api.signals.Listener;

public class JobPoolTest {
  final static private int NbJobs = 100;
  final static private int NbPool = 5;

  @Test
  public void testJobPool() {
    LocalScheduler scheduler = new LocalScheduler(10);
    int[] nbPoolDone = new int[] { 0 };
    List<Runnable> jobDone = new ArrayList<Runnable>();
    Listener<JobDoneEvent> jobListener = createListener(jobDone);
    Listener<JobPool> poolListener = SchedulerTestsUtils.createListener(nbPoolDone);
    for (int i = 0; i < NbPool; i++) {
      JobPool jobPool = createPool(poolListener, jobListener);
      jobPool.submitTo(scheduler);
    }
    scheduler.runAll();
    Assert.assertEquals(NbPool, nbPoolDone[0]);
    Assert.assertEquals(NbJobs * NbPool, jobDone.size());
    SchedulerTestsUtils.assertAreDone(jobDone, true);
  }

  private Listener<JobDoneEvent> createListener(final List<Runnable> done) {
    return new Listener<JobDoneEvent>() {
      @Override
      public void listen(JobDoneEvent event) {
        done.add(event.done);
      }
    };
  }

  private JobPool createPool(Listener<JobPool> poolListener, Listener<JobDoneEvent> jobListener) {
    List<Job> jobs = SchedulerTestsUtils.createJobs(NbJobs);
    SchedulerTestsUtils.assertAreDone(jobs, false);
    JobPool pool = new JobPool(poolListener);
    for (Job job : jobs)
      pool.add(job, jobListener);
    return pool;
  }
}
