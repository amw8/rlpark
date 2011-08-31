package rltoys.experiments.reinforcementlearning;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import rltoys.experiments.parametersweep.interfaces.Context;
import rltoys.experiments.parametersweep.interfaces.SweepDescriptor;
import rltoys.experiments.parametersweep.onpolicy.AbstractContextOnPolicy;
import rltoys.experiments.parametersweep.onpolicy.ContextEvaluation;
import rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.AgentFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;
import rltoys.utils.Utils;

public class OnPolicySweepTest extends RLSweepTest {
  private static final String ResultFolderTest = "Problem/Agent";

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
      AgentFactory agentFactory = new RLAgentFactoryTest(divergeAfter, RLProblemFactoryTest.Action01);
      ProblemFactory problemFactory = new RLProblemFactoryTest(nbEpisode, nbTimeSteps);
      return Utils.asList(new ContextEvaluation(problemFactory, agentFactory, NbRewardCheckPoint));
    }

    @Override
    public List<Parameters> provideParameters(Context context) {
      return Utils.asList(((AbstractContextOnPolicy) context).contextParameters());
    }
  }

  @Test
  public void testSweepOneEpisode() {
    testSweep(new OnPolicyTestSweep(Integer.MAX_VALUE, NbEvaluations, 1));
    checkFile(ResultFolderTest, 1, Integer.MAX_VALUE);
  }

  @Test
  public void testSweepMultipleEpisode() {
    testSweep(new OnPolicyTestSweep(Integer.MAX_VALUE, 100, NbEvaluations));
    checkFile(ResultFolderTest, 100, Integer.MAX_VALUE);
  }

  @Test
  public void testSweepWithBadAgent() {
    testSweep(new OnPolicyTestSweep(50, NbEvaluations, 1));
    checkFile(ResultFolderTest, 1, 5);
  }

  @Override
  protected void checkParameters(int divergedOnSlice, FrozenParameters parameters, int multiplier) {
    for (String label : parameters.labels()) {
      int checkPoint = 0;
      if (label.contains("Reward"))
        checkPoint = Integer.parseInt(label.substring(label.length() - 2, label.length()));
      int sliceSize = NbEvaluations / NbRewardCheckPoint;
      if (label.contains("Start"))
        Assert.assertEquals(checkPoint * sliceSize, (int) parameters.get(label));
      if (label.contains("Slice"))
        assertValue(checkPoint >= divergedOnSlice, sliceSize * multiplier, parameters.get(label));
      if (label.contains("Cumulated"))
        assertValue(divergedOnSlice <= NbRewardCheckPoint, (NbRewardCheckPoint - checkPoint) * sliceSize * multiplier,
                    parameters.get(label));
    }
  }
}
