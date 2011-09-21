package rltoys.experiments.reinforcementlearning;

import org.junit.Assert;
import org.junit.Test;

import rltoys.experiments.parametersweep.offpolicy.evaluation.EpisodeBasedOffPolicyEvaluation;
import rltoys.experiments.parametersweep.offpolicy.evaluation.OffPolicyEvaluation;
import rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;
import rltoys.experiments.reinforcementlearning.OffPolicyComponentTest.OffPolicySweepDescriptor;

public class OffPolicyPerEpisodeBasedEvaluationSweepTest extends RLSweepTest {
  final static private int NbEpisode = 100;
  final static private int NbTimeSteps = 100;
  final static private int NbBehaviourRewardCheckpoint = 10;
  final static private int NbEvaluation = 5;
  final static private int NbTimeStepsPerEvaluation = 20;

  @Test
  public void testSweepEvaluationPerEpisode() {
    OffPolicyEvaluation evaluation = new EpisodeBasedOffPolicyEvaluation(NbBehaviourRewardCheckpoint, NbEvaluation,
                                                                         NbTimeStepsPerEvaluation);
    ProblemFactory problemFactory = new RLProblemFactoryTest(NbEpisode, NbTimeSteps);
    testSweep(new OffPolicySweepDescriptor(problemFactory, evaluation));
    checkFile("Problem/OffPolicyAgent", 1, Integer.MAX_VALUE);
  }

  @Override
  protected void checkParameters(int divergedOnSlice, FrozenParameters parameters, int multiplier) {
    for (String label : parameters.labels()) {
      if (!label.contains("Reward"))
        continue;
      int checkPoint = Integer.parseInt(label.substring(label.length() - 2, label.length()));
      if (label.contains("Behaviour"))
        checkBehaviourParameter(checkPoint, label, (int) parameters.get(label));
      if (label.contains("Target"))
        checkTargetParameter(checkPoint, label, (int) parameters.get(label));
    }
  }

  private void checkBehaviourParameter(int checkPoint, String label, int value) {
    int sliceSize = NbEpisode / NbRewardCheckPoint;
    if (label.contains("Start"))
      Assert.assertEquals(checkPoint * sliceSize, value);
    if (label.contains("Slice"))
      Assert.assertEquals(sliceSize * NbTimeSteps, value);
    if (label.contains("Cumulated"))
      Assert.assertEquals((NbRewardCheckPoint - checkPoint) * sliceSize * NbTimeSteps, value);
  }

  private void checkTargetParameter(int checkPoint, String label, int value) {
    Assert.assertTrue(checkPoint < NbEvaluation);
    if (label.contains("Start"))
      Assert.assertEquals(checkPoint, value);
    if (label.contains("Slice"))
      Assert.assertEquals(NbTimeStepsPerEvaluation * 2, value);
    if (label.contains("Cumulated"))
      Assert.assertEquals((NbEvaluation - checkPoint) * NbTimeStepsPerEvaluation * 2, value);
  }
}
