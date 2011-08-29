package rltoys.experiments.parametersweep.tests;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.SweepAll;
import rltoys.experiments.parametersweep.interfaces.AgentFactory;
import rltoys.experiments.parametersweep.interfaces.Context;
import rltoys.experiments.parametersweep.interfaces.ProblemFactory;
import rltoys.experiments.parametersweep.interfaces.SweepDescriptor;
import rltoys.experiments.parametersweep.internal.ParametersLogFile;
import rltoys.experiments.parametersweep.onpolicy.ContextEvaluation;
import rltoys.experiments.parametersweep.onpolicy.AbstractContextOnPolicy;
import rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rltoys.experiments.parametersweep.parameters.RLParameters;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.scheduling.schedulers.LocalScheduler;
import rltoys.experiments.scheduling.tests.SchedulerTest;
import rltoys.utils.Utils;

public class OnPolicySweepTest {
  static final Action someAction = new Action() {
    private static final long serialVersionUID = 3442431335638935580L;
  };
  static final int NbRewardCheckPoint = 10;
  static final int NbEvalaluations = 100;

  class OnPolicyTestSweep implements SweepDescriptor {
    private final int nbTimeSteps;
    private final int nbEpisode;
    private final Action agentAction;

    public OnPolicyTestSweep(int nbTimeSteps, int nbEpisode) {
      this(someAction, nbTimeSteps, nbEpisode);
    }

    public OnPolicyTestSweep(Action agentAction, int nbTimeSteps, int nbEpisode) {
      this.nbTimeSteps = nbTimeSteps;
      this.nbEpisode = nbEpisode;
      this.agentAction = agentAction;
    }

    @Override
    public List<? extends Context> provideContexts() {
      AgentFactory agentFactory = createAgentFactory(agentAction);
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
    ProblemFactory problemFactory = new ProblemFactory() {
      private static final long serialVersionUID = -2472434131734735101L;

      @Override
      public String label() {
        return "Problem";
      }

      @Override
      public void setExperimentParameters(Parameters parameters) {
        parameters.put(RLParameters.MaxEpisodeTimeSteps, nbTimeSteps);
        parameters.put(RLParameters.NbEpisode, nbEpisode);
      }

      @Override
      public RLProblem createEnvironment(Random random) {
        return new RLProblem() {
          TRStep last = null;

          @Override
          public TRStep step(Action action) {
            TRStep result = new TRStep(last, action, new double[] {}, 1.0);
            last = result;
            return result;
          }

          @Override
          public Legend legend() {
            return new Legend();
          }

          @Override
          public TRStep initialize() {
            last = new TRStep(new double[] {}, 1);
            return last;
          }
        };
      }
    };
    return problemFactory;
  }

  static AgentFactory createAgentFactory(final Action agentAction) {
    return new AgentFactory() {
      private static final long serialVersionUID = 1L;

      @Override
      public String label() {
        return "Agent";
      }

      @Override
      public RLAgent createAgent(RLProblem problem, Parameters parameters, Random random) {
        return new RLAgent() {
          @Override
          public Action getAtp1(TRStep step) {
            return agentAction;
          }
        };
      }
    };
  }

  @Test
  public void testSweepOneEpisode() throws IOException {
    testSweep(new OnPolicyTestSweep(NbEvalaluations, 1), 1, false);
  }

  @Test
  public void testSweepMultipleEpisode() throws IOException {
    testSweep(new OnPolicyTestSweep(100, NbEvalaluations), 100, false);
  }

  @Test
  public void testSweepWithBadAgent() throws IOException {
    testSweep(new OnPolicyTestSweep(null, NbEvalaluations, 1), 1, true);
  }

  private void testSweep(OnPolicyTestSweep provider, int multiplier, boolean diverged) throws IOException {
    LocalScheduler scheduler = new LocalScheduler();
    FileUtils.deleteDirectory(new File(JUnitFolder));
    ExperimentCounter counter = new ExperimentCounter(NbRun, JUnitFolder);
    SweepAll sweep = new SweepAll(scheduler, provider, counter);
    sweep.runSweep();
    checkFile(diverged, multiplier);
    scheduler.dispose();
    FileUtils.deleteDirectory(new File(JUnitFolder));
  }

  private void checkFile(boolean diverged, int multiplier) {
    for (int i = 0; i < NbRun; i++) {
      File dataFile = new File(String.format("%s/Problem/Agent/data%02d.logtxt", JUnitFolder, i));
      if (!dataFile.canRead())
        Assert.fail();
      ParametersLogFile logFile = new ParametersLogFile(dataFile.getAbsolutePath());
      Set<FrozenParameters> parametersList = logFile.extractParameters(ProviderTest.ParameterName);
      for (FrozenParameters parameters : parametersList)
        checkParameters(diverged, parameters, multiplier);
    }
  }

  private void checkParameters(boolean diverged, FrozenParameters parameters, int multiplier) {
    for (String label : parameters.labels()) {
      int checkPoint = 0;
      if (label.contains("Reward"))
        checkPoint = Integer.parseInt(label.substring(label.length() - 2, label.length()));
      int sliceSize = NbEvalaluations / NbRewardCheckPoint;
      if (label.contains("Start"))
        Assert.assertEquals(checkPoint * sliceSize, (int) parameters.get(label));
      if (label.contains("Slice"))
        assertValue(diverged, sliceSize * multiplier, parameters.get(label));
      if (label.contains("Cumulated"))
        assertValue(diverged, (NbRewardCheckPoint - checkPoint) * sliceSize * multiplier, parameters.get(label));
    }
  }

  private void assertValue(boolean diverged, double expected, double value) {
    if (diverged)
      Assert.assertEquals(-Float.MAX_VALUE, value, 0.0);
    else
      Assert.assertEquals((int) expected, (int) value);
  }
}
