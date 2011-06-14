package rltoys.algorithms.learning.predictions.supervised;

import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rltoys.algorithms.representations.features.Feature;
import rltoys.algorithms.representations.features.LinearCombination;
import rltoys.algorithms.representations.features.UniformRandom;
import rltoys.algorithms.representations.featuresnetwork.AgentState;
import rltoys.math.History;
import rltoys.math.representations.Function;
import rltoys.math.vector.PVector;
import rltoys.utils.Utils;


public class AdalineTest {
  @Test
  public void testAdaline() {
    Random random = new Random(0);
    Feature[] inputs = { new UniformRandom(random, 0.0, 1.0), new UniformRandom(random, 0.0, 1.0) };
    double[] weights = { 1.0, 2.0 };
    LinearCombination target = new LinearCombination(new PVector(weights), inputs);
    Adaline adaline = new Adaline(2, 0.05);
    learnTarget(target, target.functions(), adaline);
    Assert.assertEquals(1.0, adaline.weights().data[0], 1e-2);
    Assert.assertEquals(2.0, adaline.weights().data[1], 1e-2);
  }

  @Test
  public void testAdalineOnTracking() {
    NoisyInputSum problem = new NoisyInputSum();
    double error = problem.evaluateLearner(new Adaline(NoisyInputSum.NbInputs, 0));
    Assert.assertEquals(NoisyInputSum.NbNonZeroWeight, error, 0.2);
    error = problem.evaluateLearner(new Adaline(NoisyInputSum.NbInputs, 0.03));
    Assert.assertEquals(3.5, error, 0.2);
  }

  public static void learnTarget(LinearCombination target, List<Function> state,
      Adaline learner) {
    AgentState agentState = new AgentState(state);
    agentState.add(target);
    int nbUpdate = 0;
    double threshold = 1e-3;
    History history = new History(5);
    history.fill(threshold);
    while (history.sum() > threshold) {
      agentState.update();
      PVector currentState = agentState.currentState();
      double error = learner.predict(currentState) - target.value();
      Assert.assertTrue(Utils.checkValue(error));
      history.append(Math.abs(error));
      learner.learn(currentState, target.value());
      nbUpdate++;
      Assert.assertTrue(nbUpdate < 100000);
    }
    Assert.assertTrue(nbUpdate > 30);
    Assert.assertEquals(target.value(), learner.predict(agentState.currentState()), threshold * 10);
  }
}
