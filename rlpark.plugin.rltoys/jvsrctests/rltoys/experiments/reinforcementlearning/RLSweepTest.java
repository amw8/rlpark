package rltoys.experiments.reinforcementlearning;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.ProviderTest;
import rltoys.experiments.parametersweep.SweepAll;
import rltoys.experiments.parametersweep.interfaces.SweepDescriptor;
import rltoys.experiments.parametersweep.internal.ParametersLogFile;
import rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rltoys.experiments.scheduling.SchedulerTest;
import rltoys.experiments.scheduling.schedulers.LocalScheduler;

public abstract class RLSweepTest {
  protected static final String JUnitFolder = ".junittests_rlparametersweep";
  protected static final int NbRun = 4;
  protected static final int NbRewardCheckPoint = 10;
  protected static final int NbEvaluations = 100;
  protected SweepAll sweep = null;

  @Before
  public void before() throws IOException {
    FileUtils.deleteDirectory(new File(JUnitFolder));
    sweep = new SweepAll(new LocalScheduler(1));
    SchedulerTest.junitMode();
    // Sweep.disableVerbose();
  }

  @After
  public void after() throws IOException {
    FileUtils.deleteDirectory(new File(JUnitFolder));
    sweep.scheduler().dispose();
  }

  protected void testSweep(SweepDescriptor provider) {
    ExperimentCounter counter = new ExperimentCounter(NbRun, JUnitFolder);
    sweep.runSweep(provider, counter);
  }

  protected void assertValue(boolean diverged, double expected, double value) {
    if (diverged)
      Assert.assertEquals(-Float.MAX_VALUE, value, 0.0);
    else
      Assert.assertEquals((int) expected, (int) value);
  }

  protected void checkFile(String testFolder, int multiplier, int divergedOnSlice) {
    for (int i = 0; i < NbRun; i++) {
      String filename = String.format("data%02d.logtxt", i);
      File dataFile = new File(String.format("%s/%s/%s", JUnitFolder, testFolder, filename));
      if (!dataFile.canRead())
        Assert.fail("Cannot read " + dataFile.getAbsolutePath());
      ParametersLogFile logFile = new ParametersLogFile(dataFile.getAbsolutePath());
      List<FrozenParameters> parametersList = logFile.extractParameters(ProviderTest.ParameterName);
      for (FrozenParameters parameters : parametersList)
        checkParameters(testFolder, filename, divergedOnSlice, parameters, multiplier);
    }
  }

  abstract protected void checkParameters(String testFolder, String filename, int divergedOnSlice,
      FrozenParameters parameters, int multiplier);
}
