package rltoys.experiments.reinforcementlearning;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import rltoys.experiments.parametersweep.interfaces.Context;
import rltoys.experiments.parametersweep.interfaces.SweepDescriptor;
import rltoys.experiments.parametersweep.offpolicy.AbstractContextOffPolicy;
import rltoys.experiments.parametersweep.offpolicy.ContextEvaluation;
import rltoys.experiments.parametersweep.offpolicy.evaluation.ContinuousOffPolicyEvaluation;
import rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyAgentFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.ProjectorFactory;
import rltoys.experiments.reinforcementlearning.OffPolicyComponentTest.OffPolicyAgentFactoryTest;
import rltoys.experiments.reinforcementlearning.OffPolicyComponentTest.ProjectorFactoryTest;
import rltoys.utils.Utils;

public class OffPolicySweepTest extends RLSweepTest {
  class OffPolicySweepDescriptor implements SweepDescriptor {
    private final int nbTimeSteps;

    public OffPolicySweepDescriptor(int nbTimeSteps) {
      this.nbTimeSteps = nbTimeSteps;
    }

    @Override
    public List<? extends Context> provideContexts() {
      ProblemFactory problemFactory = new RLProblemFactoryTest(1, nbTimeSteps);
      OffPolicyAgentFactory agentFactory = new OffPolicyAgentFactoryTest();
      ProjectorFactory projectorFactory = new ProjectorFactoryTest();
      ContinuousOffPolicyEvaluation evaluation = new ContinuousOffPolicyEvaluation(10);
      return Utils.asList(new ContextEvaluation(problemFactory, projectorFactory, agentFactory, evaluation));
    }

    @Override
    public List<Parameters> provideParameters(Context context) {
      return Utils.asList(((AbstractContextOffPolicy) context).contextParameters());
    }
  }

  @Test
  public void testSweepOneEpisode() {
    testSweep(new OffPolicySweepDescriptor(NbEvalaluations));
    checkFile("Problem/OffPolicyAgent", 1, Integer.MAX_VALUE);
  }

  @Override
  protected void checkParameters(int divergedOnSlice, FrozenParameters parameters, int multiplier) {
    for (String label : parameters.labels()) {
      int checkPoint = 0;
      if (label.contains("Reward"))
        checkPoint = Integer.parseInt(label.substring(label.length() - 2, label.length()));
      int sliceSize = NbEvalaluations / NbRewardCheckPoint;
      if (label.contains("Start"))
        Assert.assertEquals(checkPoint * sliceSize, (int) parameters.get(label));
      int multiplierAdjusted = adjustMultiplier(label, multiplier);
      if (label.contains("Slice"))
        assertValue(checkPoint >= divergedOnSlice, sliceSize * multiplierAdjusted, parameters.get(label));
      if (label.contains("Cumulated"))
        assertValue(divergedOnSlice <= NbRewardCheckPoint, (NbRewardCheckPoint - checkPoint) * sliceSize
            * multiplierAdjusted, parameters.get(label));
    }
  }

  private int adjustMultiplier(String label, int multiplier) {
    if (label.contains("Target"))
      return multiplier * 2;
    if (label.contains("Behaviour"))
      return multiplier;
    return 0;
  }
}
