package rltoys.experiments.reinforcementlearning;

import org.junit.Assert;
import org.junit.Test;

import rltoys.experiments.parametersweep.offpolicy.evaluation.EpisodeBasedOffPolicyEvaluation;
import rltoys.experiments.parametersweep.offpolicy.evaluation.OffPolicyEvaluation;
import rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rltoys.experiments.reinforcementlearning.OffPolicyComponentTest.OffPolicyRLProblemFactoryTest;
import rltoys.experiments.reinforcementlearning.OffPolicyComponentTest.OffPolicySweepDescriptor;

public class OffPolicyPerEpisodeBasedEvaluationSweepTest extends AbstractOffPolicyRLSweepTest {
  final static private int NbEpisode = 100;
  final static private int NbTimeSteps = 100;
  final static private int NbBehaviourRewardCheckpoint = 10;
  final static private int NbEvaluation = 5;
  final static private int NbTimeStepsPerEvaluation = 20;

  @Test
  public void testSweepEvaluationPerEpisode() {
    OffPolicyEvaluation evaluation = new EpisodeBasedOffPolicyEvaluation(NbBehaviourRewardCheckpoint, NbEvaluation,
                                                                         NbTimeStepsPerEvaluation);
    OffPolicyProblemFactory problemFactory = new OffPolicyRLProblemFactoryTest(NbEpisode, NbTimeSteps);
    testSweep(new OffPolicySweepDescriptor(problemFactory, evaluation));
    checkFile("Problem/Action01", 1, Integer.MAX_VALUE);
    checkFile("Problem/Action02", 1, Integer.MAX_VALUE);
    Assert.assertTrue(isBehaviourPerformanceChecked());
  }

  @Override
  protected void checkParameters(String testFolder, String filename, int divergedOnSlice, FrozenParameters parameters,
      int multiplier) {
    for (String label : parameters.labels()) {
      if (!label.contains("Reward"))
        continue;
      int checkPoint = Integer.parseInt(label.substring(label.length() - 2, label.length()));
      if (label.contains("Behaviour"))
        checkBehaviourParameter(filename, checkPoint, label, (int) parameters.get(label));
      if (label.contains("Target"))
        checkTargetParameter(testFolder, checkPoint, label, (int) parameters.get(label));
    }
  }

  private void checkBehaviourParameter(String filename, int checkPoint, String label, double value) {
    int sliceSize = NbEpisode / NbRewardCheckPoint;
    if (label.contains("Start")) {
      Assert.assertEquals(checkPoint * sliceSize, (int) value);
      return;
    }
    double adjustedValue = 0.0;
    if (label.contains("Slice"))
      adjustedValue = value / (sliceSize * NbTimeSteps);
    if (label.contains("Cumulated"))
      adjustedValue = value / ((NbRewardCheckPoint - checkPoint) * sliceSize * NbTimeSteps);
    checkBehaviourPerformanceValue(filename, label, value, adjustedValue);
  }

  private void checkTargetParameter(String testFolder, int checkPoint, String label, int value) {
    int multiplier = Integer.parseInt(testFolder.substring(testFolder.length() - 2));
    Assert.assertTrue(checkPoint < NbEvaluation);
    if (label.contains("Start"))
      Assert.assertEquals(checkPoint, value);
    if (label.contains("Slice"))
      Assert.assertEquals(NbTimeStepsPerEvaluation * multiplier, value);
    if (label.contains("Cumulated"))
      Assert.assertEquals((NbEvaluation - checkPoint) * NbTimeStepsPerEvaluation * multiplier, value);
  }
}
