package rltoys.experiments.reinforcementlearning;

import org.junit.Assert;
import org.junit.Test;

import rltoys.experiments.parametersweep.offpolicy.evaluation.ContinuousOffPolicyEvaluation;
import rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;
import rltoys.experiments.reinforcementlearning.OffPolicyComponentTest.OffPolicySweepDescriptor;

public class OffPolicyContinuousEvaluationSweepTest extends RLSweepTest {
  @Test
  public void testSweepOneEpisode() {
    ProblemFactory problemFactory = new RLProblemFactoryTest(1, NbEvaluations);
    ContinuousOffPolicyEvaluation evaluation = new ContinuousOffPolicyEvaluation(10);
    testSweep(new OffPolicySweepDescriptor(problemFactory, evaluation));
    checkFile("Problem/OffPolicyAgent", 1, Integer.MAX_VALUE);
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
