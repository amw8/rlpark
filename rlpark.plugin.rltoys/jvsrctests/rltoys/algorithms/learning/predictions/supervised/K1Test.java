package rltoys.algorithms.learning.predictions.supervised;

import org.junit.Assert;
import org.junit.Test;

public class K1Test {
  @Test
  public void testK1() {
    NoisyInputSum problem = new NoisyInputSum();
    double error = problem.evaluateLearner(new K1(NoisyInputSum.NbInputs, 0.001));
    Assert.assertEquals(1.7, error, 0.1);
    error = problem.evaluateLearner(new K1(NoisyInputSum.NbInputs, 0.01));
    Assert.assertEquals(1.0, error, 0.1);
  }
}
