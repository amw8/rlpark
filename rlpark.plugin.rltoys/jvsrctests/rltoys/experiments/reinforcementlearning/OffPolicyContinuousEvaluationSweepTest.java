package rltoys.experiments.reinforcementlearning;

import org.junit.Assert;
import org.junit.Test;

import rltoys.experiments.parametersweep.offpolicy.evaluation.ContinuousOffPolicyEvaluation;
import rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rltoys.experiments.reinforcementlearning.OffPolicyComponentTest.OffPolicyRLProblemFactoryTest;
import rltoys.experiments.reinforcementlearning.OffPolicyComponentTest.OffPolicySweepDescriptor;

public class OffPolicyContinuousEvaluationSweepTest extends AbstractOffPolicyRLSweepTest {
  @Test
  public void testSweepOneEpisode() {
    OffPolicyProblemFactory problemFactory = new OffPolicyRLProblemFactoryTest(1, NbEvaluations);
    ContinuousOffPolicyEvaluation evaluation = new ContinuousOffPolicyEvaluation(10);
    testSweep(new OffPolicySweepDescriptor(problemFactory, evaluation));
    checkFile("Problem/Action01", 1, Integer.MAX_VALUE);
    checkFile("Problem/Action02", 1, Integer.MAX_VALUE);
    Assert.assertTrue(isBehaviourPerformanceChecked());
  }

  @Override
  protected void checkParameters(String testFolder, String filename, int divergedOnSlice, FrozenParameters parameters,
      int multiplier) {
    for (String label : parameters.labels()) {
      int checkPoint = 0;
      if (label.contains("Reward"))
        checkPoint = Integer.parseInt(label.substring(label.length() - 2, label.length()));
      int sliceSize = NbEvaluations / NbRewardCheckPoint;
      if (label.contains("Start")) {
        Assert.assertEquals(checkPoint * sliceSize, (int) parameters.get(label));
        continue;
      }
      if (label.contains("Behaviour")) {
        checkBehaviourPerformance(testFolder, filename, label, sliceSize, checkPoint, parameters.get(label));
        continue;
      }
      int multiplierAdjusted = Integer.parseInt(testFolder.substring(testFolder.length() - 2));
      if (label.contains("Slice"))
        assertValue(checkPoint >= divergedOnSlice, sliceSize * multiplierAdjusted, parameters.get(label));
      if (label.contains("Cumulated"))
        assertValue(divergedOnSlice <= NbRewardCheckPoint, (NbRewardCheckPoint - checkPoint) * sliceSize
            * multiplierAdjusted, parameters.get(label));
    }
  }

  private void checkBehaviourPerformance(String testFolder, String filename, String label, int sliceSize,
      int checkPoint, double value) {
    double adjustedValue;
    if (label.contains("Slice"))
      adjustedValue = value / sliceSize;
    else
      adjustedValue = value / (NbRewardCheckPoint - checkPoint) / sliceSize;
    checkBehaviourPerformanceValue(filename, label, value, adjustedValue);
  }
}
