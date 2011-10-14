package rltoys.experiments.reinforcementlearning;

import org.junit.Assert;
import org.junit.Test;

import rltoys.experiments.parametersweep.offpolicy.evaluation.ContinuousOffPolicyEvaluation;
import rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;
import rltoys.experiments.reinforcementlearning.OffPolicyComponentTest.OffPolicySweepDescriptor;

public class OffPolicyContinuousEvaluationSweepTest extends AbstractOffPolicyRLSweepTest {
  @Test
  public void testSweepOneEpisode() {
    ProblemFactory problemFactory = new RLProblemFactoryTest(1, NbTimeSteps);
    ContinuousOffPolicyEvaluation evaluation = new ContinuousOffPolicyEvaluation(10);
    testSweep(new OffPolicySweepDescriptor(problemFactory, evaluation));
    checkFile("Problem/Action01", Integer.MAX_VALUE);
    checkFile("Problem/Action02", Integer.MAX_VALUE);
    Assert.assertTrue(isBehaviourPerformanceChecked());
  }

  @Override
  protected void checkParameters(String testFolder, String filename, int divergedOnSlice, FrozenParameters parameters) {
    for (String label : parameters.labels()) {
      int checkPoint = 0;
      if (label.contains("Reward"))
        checkPoint = Integer.parseInt(label.substring(label.length() - 2, label.length()));
      int sliceSize = NbTimeSteps / NbRewardCheckPoint;
      if (label.contains("Start")) {
        Assert.assertEquals(checkPoint * sliceSize, (int) parameters.get(label));
        continue;
      }
      if (label.contains("Behaviour")) {
        checkBehaviourPerformanceValue(filename, label, parameters.get(label));
        continue;
      }
      int multiplierAdjusted = Integer.parseInt(testFolder.substring(testFolder.length() - 2));
      if (label.contains("Slice"))
        assertValue(checkPoint >= divergedOnSlice, multiplierAdjusted, parameters.get(label));
      if (label.contains("Cumulated"))
        assertValue(divergedOnSlice <= NbRewardCheckPoint, multiplierAdjusted, parameters.get(label));
    }
  }
}
