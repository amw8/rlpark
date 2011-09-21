package rltoys.experiments.scheduling;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import rltoys.experiments.scheduling.JobPoolTest.JobPoolListenerTest;
import rltoys.experiments.scheduling.SchedulerTestsUtils.Job;
import rltoys.experiments.scheduling.SchedulerTestsUtils.JobDoneListener;
import rltoys.experiments.scheduling.internal.messages.ClassLoading;
import rltoys.experiments.scheduling.internal.messages.Messages;
import rltoys.experiments.scheduling.network.ServerScheduler;

public class UnreliableNetworkClientWithPoolTest {
  static int nbUnreliableQueue = 0;

  @BeforeClass
  static public void junitMode() {
    ClassLoading.enableForceNetworkClassResolution();
    Messages.disableVerbose();
    // Messages.enableDebug();
  }

  @Test(timeout = SchedulerTestsUtils.Timeout)
  public void testServerSchedulerWithMultipleClients() throws IOException {
    ServerScheduler scheduler = new ServerScheduler(SchedulerTestsUtils.Port, 0);
    scheduler.start();
    UnreliableNetworkClientTest.startUnreliableClients(5);
    testServerSchedulerWithPool(scheduler, 10000, 100);
    scheduler.dispose();
  }

  private void testServerSchedulerWithPool(ServerScheduler scheduler, int nbJobs, int nbPools) {
    for (int i = 0; i < 2; i++) {
      List<Job> jobs = SchedulerTestsUtils.createJobs(nbJobs);
      SchedulerTestsUtils.assertAreDone(jobs, false);
      JobDoneListener jobListener = SchedulerTestsUtils.createListener();
      JobPoolListenerTest poolListener = new JobPoolTest.JobPoolListenerTest();
      SchedulerTestsUtils.submitJobsInPool(scheduler, jobs, jobListener, poolListener, nbPools);
      scheduler.runAll();
      SchedulerTestsUtils.assertAreDone(jobListener.jobDone(), true);
      Assert.assertEquals(nbJobs, jobListener.nbJobDone());
      Assert.assertEquals(nbPools, poolListener.nbPoolDone());
    }
  }
}
