package rltoys.algorithms.learning.control.gq;

import java.util.Map;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rltoys.algorithms.learning.predictions.td.TDTest;
import rltoys.algorithms.representations.acting.ConstantPolicy;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.environments.stategraph.FSGAgentState;
import rltoys.environments.stategraph.FiniteStateGraph.StepData;
import rltoys.environments.stategraph.GraphState;
import rltoys.environments.stategraph.RandomWalk;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;
import rltoys.math.vector.implementations.Vectors;


public class GQTest {
  public interface GQControlFactory {
    GreedyGQ createGQControl(GQ gq, Action[] actions, StateToStateAction toStateAction, Policy target, Policy behaviour);
  }

  private final GQControlFactory otherGQFactory = new GQControlFactory() {
    @Override
    public GreedyGQ createGQControl(GQ gq, Action[] actions, StateToStateAction toStateAction, Policy target,
        Policy behaviour) {
      return new GreedyGQ(gq, toStateAction, target, behaviour);
    }
  };
  private final GQControlFactory defaultGQFactory = new GQControlFactory() {
    @Override
    public GreedyGQ createGQControl(GQ gq, Action[] actions, StateToStateAction toStateAction, Policy target,
        Policy behaviour) {
      return new ExpectedGQ(gq, actions, toStateAction, target, behaviour);
    }
  };

  @Test
  public void testOnPolicyGQ() {
    testGQOnRandomWalk(0.0, 0.01, 0.0, 0.0, 0.5, 0.5);
    testGQOnRandomWalk(0.1, 0.01, 0.0, 0.0, 0.5, 0.5);
    testGQOnRandomWalk(0.1, 0.01, 0.0, 0.1, 0.5, 0.5);
    testGQOnRandomWalk(0.1, 0.01, 0.5, 0.1, 0.5, 0.5);
    testGQOnRandomWalk(0.1, 0.01, 0.5, 0.1, 0.5, 0.5, otherGQFactory);
  }

  @Test
  public void testOffPolicyGQ() {
    testGQOnRandomWalk(0.1, 0.01, 0.0, 0.0, 0.3, 0.5);
    testGQOnRandomWalk(0.1, 0.01, 0.0, 0.1, 0.3, 0.5);
    testGQOnRandomWalk(0.1, 0.01, 0.5, 0.1, 0.3, 0.5);
  }

  private void testGQOnRandomWalk(double beta, double alpha_theta, double alpha_w, double lambda,
      double targetLeftProbability, double behaviourLeftProbability) {
    testGQOnRandomWalk(beta, alpha_theta, alpha_w, lambda, targetLeftProbability, behaviourLeftProbability,
                       defaultGQFactory);
  }

  private void testGQOnRandomWalk(double beta, double alpha_theta, double alpha_w, double lambda,
      double targetLeftProbability, double behaviourLeftProbability, GQControlFactory gqControlFactory) {
    Random random = new Random(0);
    ConstantPolicy behaviourPolicy = RandomWalk.newPolicy(random, behaviourLeftProbability);
    ConstantPolicy targetPolicy = RandomWalk.newPolicy(random, targetLeftProbability);
    RandomWalk problem = new RandomWalk(behaviourPolicy);
    FSGAgentState agentState = new FSGAgentState(problem);
    GQ gq = new GQ(alpha_theta, alpha_w, beta, lambda, agentState.vectorSize());
    GreedyGQ controlGQ = gqControlFactory.createGQControl(gq, agentState.graph().actions(), agentState, targetPolicy,
                                                          behaviourPolicy);
    int nbEpisode = 0;
    double[] solution = agentState.computeSolution(targetPolicy, 1 - beta, lambda);
    PVector v = new PVector(agentState.size);
    while (TDTest.distanceToSolution(solution, v) > 0.05) {
      StepData stepData = agentState.step();
      controlGQ.update(stepData.v_t(), stepData.a_t, stepData.r_tp1, 0.0, stepData.v_tp1(), stepData.a_tp1);
      if (stepData.s_tp1 == null) {
        nbEpisode += 1;
        Assert.assertTrue(nbEpisode < 100000);
      }
      v = computeValueFunction(agentState, gq, targetPolicy);
    }
    Assert.assertTrue(nbEpisode > 100);
    Assert.assertTrue(Vectors.checkValues(controlGQ.theta()));
  }

  private PVector computeValueFunction(FSGAgentState agentState, GQ gq, Policy targetPolicy) {
    PVector v = new PVector(agentState.size);
    for (Map.Entry<GraphState, Integer> entry : agentState.stateIndexes().entrySet()) {
      GraphState s = entry.getKey();
      int si = entry.getValue();
      double v_s = 0;
      for (Action a : agentState.graph().actions()) {
        RealVector phi_sa = agentState.stateAction(s.v(), a);
        v_s += targetPolicy.pi(s.v(), a) * gq.predict(phi_sa);
      }
      v.data[si] = v_s;
    }
    return v;
  }
}
