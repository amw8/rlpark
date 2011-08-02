package rltoys.experiments.parametersweep;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.internal.ParametersLogFile;
import rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rltoys.experiments.scheduling.interfaces.Scheduler;
import rltoys.experiments.scheduling.network.NetworkClientScheduler;
import rltoys.experiments.scheduling.network.ServerScheduler;
import rltoys.experiments.scheduling.schedulers.LocalScheduler;
import rltoys.experiments.scheduling.tests.SchedulerTest;

public class SweepTest {
  private static final String JUnitFolder = ".junittests_parametersweep";
  private static final int NbRun = 4;
  private static final int Port = 5000;

  @BeforeClass
  static public void setup() {
    SchedulerTest.junitMode();
    Sweep.disableVerbose();
  }

  @Ignore
  @Test
  public void testSweepLocalScheduler() throws IOException {
    LocalScheduler scheduler = new LocalScheduler();
    testSweep(scheduler);
    scheduler.dispose();
  }

  @Test(timeout = SchedulerTest.Timeout)
  public void testSweepNetworkScheduler() throws IOException {
    ServerScheduler scheduler = new ServerScheduler(Port);
    scheduler.stopLocalScheduler();
    new NetworkClientScheduler(2, "localhost", Port).start();
    testSweep(scheduler);
    scheduler.dispose();
  }

  private void testSweep(Scheduler scheduler) throws IOException {
    FileUtils.deleteDirectory(new File(JUnitFolder));
    Assert.assertFalse(checkFile(-1));
    int nbJobs = runFullSweep(scheduler, 5);
    Assert.assertEquals(5 * NbRun, nbJobs);
    nbJobs = runFullSweep(scheduler, 7);
    Assert.assertEquals((7 - 5) * NbRun, nbJobs);
    nbJobs = runFullSweep(scheduler, 7);
    Assert.assertEquals(0, nbJobs);
    Assert.assertTrue(checkFile(7));
  }

  private boolean checkFile(int nbTotalParameters) {
    File dataFile = new File(JUnitFolder + "/" + ProviderTest.ContextPath + "/data00.logtxt");
    if (!dataFile.canRead())
      return false;
    ParametersLogFile logFile = new ParametersLogFile(dataFile.getAbsolutePath());
    Set<FrozenParameters> parametersList = logFile.extractParameters(ProviderTest.ParameterName);
    if (nbTotalParameters > 0 && parametersList.size() != nbTotalParameters)
      return false;
    for (FrozenParameters parameters : parametersList)
      if (!ProviderTest.parametersHasBeenDone(parameters))
        return false;
    return true;
  }

  private int runFullSweep(Scheduler scheduler, int nbParameters) {
    ProviderTest provider = new ProviderTest(nbParameters);
    ExperimentCounter counter = new ExperimentCounter(NbRun, JUnitFolder);
    Sweep sweep = new Sweep(scheduler, provider, counter);
    sweep.runSweep();
    return sweep.nbJobs();
  }
}
