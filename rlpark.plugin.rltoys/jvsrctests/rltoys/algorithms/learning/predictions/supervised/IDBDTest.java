package rltoys.algorithms.learning.predictions.supervised;

import org.junit.Assert;
import org.junit.Test;

public class IDBDTest {
  @Test
  public void testIDBD() {
    NoisyInputSum problem = new NoisyInputSum();
    double error = problem.evaluateLearner(new IDBD(NoisyInputSum.NbInputs, 0.001));
    Assert.assertEquals(2.0, error, 0.1);
    error = problem.evaluateLearner(new IDBD(NoisyInputSum.NbInputs, 0.01));
    Assert.assertEquals(1.5, error, 0.1);
  }
}
