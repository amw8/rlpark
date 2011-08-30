package rltoys.experiments.reinforcementlearning;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import rltoys.algorithms.representations.actions.Action;
import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.ProviderTest;
import rltoys.experiments.parametersweep.SweepAll;
import rltoys.experiments.parametersweep.interfaces.AgentFactory;
import rltoys.experiments.parametersweep.interfaces.Context;
import rltoys.experiments.parametersweep.interfaces.ProblemFactory;
import rltoys.experiments.parametersweep.interfaces.SweepDescriptor;
import rltoys.experiments.parametersweep.internal.ParametersLogFile;
import rltoys.experiments.parametersweep.onpolicy.AbstractContextOnPolicy;
import rltoys.experiments.parametersweep.onpolicy.ContextEvaluation;
import rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.scheduling.SchedulerTest;
import rltoys.experiments.scheduling.schedulers.LocalScheduler;
import rltoys.utils.Utils;

public class OnPolicySweepTest {
  static final int NbRewardCheckPoint = 10;
  static final int NbEvalaluations = 100;

  class OnPolicyTestSweep implements SweepDescriptor {
    private final int nbTimeSteps;
    private final int nbEpisode;
    private final int divergeAfter;

    public OnPolicyTestSweep(int divergeAfter, int nbTimeSteps, int nbEpisode) {
      this.nbTimeSteps = nbTimeSteps;
      this.nbEpisode = nbEpisode;
      this.divergeAfter = divergeAfter;
    }

    @Override
    public List<? extends Context> provideContexts() {
      AgentFactory agentFactory = createAgentFactory(divergeAfter, RLProblemFactoryTest.Action01);
      ProblemFactory problemFactory = createProblemFactory(nbTimeSteps, nbEpisode);
      return Utils.asList(new ContextEvaluation(problemFactory, agentFactory, NbRewardCheckPoint));
    }

    @Override
    public List<Parameters> provideParameters(Context context) {
      return Utils.asList(((AbstractContextOnPolicy) context).contextParameters());
    }
  }

  private static final String JUnitFolder = ".junittests_onpolicyparametersweep";
  private static final int NbRun = 4;

  @BeforeClass
  static public void setup() {
    SchedulerTest.junitMode();
    // Sweep.disableVerbose();
  }

  static ProblemFactory createProblemFactory(final int nbTimeSteps, final int nbEpisode) {
    ProblemFactory problemFactory = new RLProblemFactoryTest(nbEpisode, nbTimeSteps);
    return problemFactory;
  }

  static AgentFactory createAgentFactory(int divergeAfter, final Action agentAction) {
    return new RLAgentFactoryTest(divergeAfter, agentAction);
  }

  @Test
  public void testSweepOneEpisode() throws IOException {
    testSweep(new OnPolicyTestSweep(Integer.MAX_VALUE, NbEvalaluations, 1), 1, Integer.MAX_VALUE);
  }

  @Test
  public void testSweepMultipleEpisode() throws IOException {
    testSweep(new OnPolicyTestSweep(Integer.MAX_VALUE, 100, NbEvalaluations), 100, Integer.MAX_VALUE);
  }

  @Test
  public void testSweepWithBadAgent() throws IOException {
    testSweep(new OnPolicyTestSweep(50, NbEvalaluations, 1), 1, 5);
  }

  private void testSweep(OnPolicyTestSweep provider, int multiplier, int divergedOnSlice) throws IOException {
    LocalScheduler scheduler = new LocalScheduler();
    FileUtils.deleteDirectory(new File(JUnitFolder));
    ExperimentCounter counter = new ExperimentCounter(NbRun, JUnitFolder);
    SweepAll sweep = new SweepAll(scheduler, provider, counter);
    sweep.runSweep();
    checkFile(divergedOnSlice, multiplier);
    scheduler.dispose();
    FileUtils.deleteDirectory(new File(JUnitFolder));
  }

  private void checkFile(int divergedOnSlice, int multiplier) {
    for (int i = 0; i < NbRun; i++) {
      File dataFile = new File(String.format("%s/Problem/Agent/data%02d.logtxt", JUnitFolder, i));
      if (!dataFile.canRead())
        Assert.fail();
      ParametersLogFile logFile = new ParametersLogFile(dataFile.getAbsolutePath());
      Set<FrozenParameters> parametersList = logFile.extractParameters(ProviderTest.ParameterName);
      for (FrozenParameters parameters : parametersList)
        checkParameters(divergedOnSlice, parameters, multiplier);
    }
  }

  private void checkParameters(int divergedOnSlice, FrozenParameters parameters, int multiplier) {
    for (String label : parameters.labels()) {
      int checkPoint = 0;
      if (label.contains("Reward"))
        checkPoint = Integer.parseInt(label.substring(label.length() - 2, label.length()));
      int sliceSize = NbEvalaluations / NbRewardCheckPoint;
      if (label.contains("Start"))
        Assert.assertEquals(checkPoint * sliceSize, (int) parameters.get(label));
      if (label.contains("Slice"))
        assertValue(checkPoint >= divergedOnSlice, sliceSize * multiplier, parameters.get(label));
      if (label.contains("Cumulated"))
        assertValue(divergedOnSlice <= NbRewardCheckPoint, (NbRewardCheckPoint - checkPoint) * sliceSize * multiplier,
                    parameters.get(label));
    }
  }

  private void assertValue(boolean diverged, double expected, double value) {
    if (diverged)
      Assert.assertEquals(-Float.MAX_VALUE, value, 0.0);
    else
      Assert.assertEquals((int) expected, (int) value);
  }
}
