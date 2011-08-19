package rltoys.experiments.scheduling.tests;

import static rltoys.experiments.scheduling.tests.SchedulerTestsUtils.testScheduler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import rltoys.experiments.scheduling.network.NetworkClientScheduler;
import rltoys.experiments.scheduling.network.ServerScheduler;
import rltoys.experiments.scheduling.network.internal.LocalQueue;
import rltoys.experiments.scheduling.network.internal.Messages;
import rltoys.experiments.scheduling.network.internal.NetworkClassLoader;
import rltoys.experiments.scheduling.schedulers.LocalScheduler;
import rltoys.experiments.scheduling.tests.SchedulerTestsUtils.Job;
import rltoys.experiments.scheduling.tests.SchedulerTestsUtils.JobDoneListener;

public class SchedulerTest {
  private static final String Localhost = "localhost";
  private static final int Port = 5000;
  public static final int Timeout = 1000000;

  @BeforeClass
  static public void junitMode() {
    NetworkClassLoader.enableForceNetworkClassResolution();
    Messages.disableVerbose();
    // Messages.enableDebug();
  }

  @Test(timeout = Timeout)
  public void testJobScheduler() {
    LocalScheduler scheduler = new LocalScheduler(10);
    SchedulerTestsUtils.testScheduler(scheduler);
    scheduler.dispose();
  }

  @Test(timeout = Timeout)
  public void testJobSchedulerWithManyIterators() {
    LocalScheduler scheduler = new LocalScheduler(10);
    final int NbJobs = 100;
    for (int i = 0; i < 2; i++) {
      List<Job> jobs = SchedulerTestsUtils.createJobs(NbJobs);
      SchedulerTestsUtils.assertAreDone(jobs, false);
      JobDoneListener listener = SchedulerTestsUtils.createListener();
      for (Job job : jobs) {
        List<Runnable> oneElement = new ArrayList<Runnable>();
        oneElement.add(job);
        ((LocalQueue) scheduler.queue()).add(oneElement.iterator(), listener);
      }
      scheduler.runAll();
      Assert.assertEquals(NbJobs, listener.nbJobDone());
      SchedulerTestsUtils.assertAreDone(listener.jobDone(), true);
    }
    scheduler.dispose();
  }

  @Test(timeout = Timeout)
  public void testServerScheduler() throws IOException {
    ServerScheduler scheduler = new ServerScheduler(Port, 10);
    testScheduler(scheduler);
    scheduler.dispose();
  }

  @Test(timeout = Timeout)
  public void testServerSchedulerWithUniqueClient() throws IOException {
    ServerScheduler scheduler = new ServerScheduler(Port, 0);
    NetworkClientScheduler client01 = new NetworkClientScheduler(1, Localhost, Port);
    client01.start();
    testScheduler(scheduler);
    client01.dispose();
    scheduler.dispose();
  }

  @Test(timeout = Timeout)
  public void testServerSchedulerWithUniqueClientMultipleThreads() throws IOException {
    ServerScheduler scheduler = new ServerScheduler(Port, 0);
    NetworkClientScheduler client01 = new NetworkClientScheduler(2, Localhost, Port);
    client01.start();
    testScheduler(scheduler);
    scheduler.dispose();
    client01.dispose();
  }

  @Test(timeout = Timeout)
  public void testServerSchedulerWithMultipleClients() throws IOException {
    ServerScheduler scheduler = new ServerScheduler(Port, 0);
    NetworkClientScheduler client01 = new NetworkClientScheduler(10, Localhost, Port);
    NetworkClientScheduler client02 = new NetworkClientScheduler(10, Localhost, Port);
    client01.start();
    client02.start();
    testScheduler(scheduler);
    scheduler.dispose();
    client01.dispose();
    client02.dispose();
  }
}
