package rltoys.algorithms.learning.predictions.td;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rltoys.algorithms.learning.predictions.td.TDTest.OnPolicyTDFactory;
import rltoys.algorithms.representations.acting.ConstantPolicy;
import rltoys.environments.stategraph.FSGAgentState;
import rltoys.environments.stategraph.FiniteStateGraph.StepData;
import rltoys.environments.stategraph.RandomWalk;
import rltoys.math.vector.CachedVector;
import rltoys.math.vector.PVector;
import rltoys.math.vector.RealVector;


public class GTDLambdaBetaTest {
  static class OnPolicyGTD implements OnPolicyTD {
    private static final long serialVersionUID = -5884555501316460266L;
    private final GTDBetaLambda gtdlambda;
    private final double beta;

    public OnPolicyGTD(double beta, double alpha_theta, double alpha_w, double lambda, int nbFeatures) {
      this.beta = beta;
      gtdlambda = new GTDBetaLambda(alpha_theta, alpha_w, lambda, nbFeatures);
    }

    @Override
    public double update(RealVector phi_t, RealVector phi_tp1, double r_tp1) {
      return gtdlambda.update(1.0, beta, 1.0, phi_t, beta, phi_tp1, 0.0, r_tp1);
    }

    @Override
    public double predict(RealVector x) {
      return gtdlambda.predict(x);
    }

    @Override
    public PVector weights() {
      return gtdlambda.weights();
    }

    @Override
    public void resetWeight(int index) {
      gtdlambda.resetWeight(index);
    }
  }

  static class OffPolicyGTD {
    private final GTDBetaLambda gtdlambda;
    private final double beta;
    private double rho_tm1;
    private final CachedVector phi_t = new CachedVector();

    public OffPolicyGTD(double beta, double alpha_theta, double alpha_w, double lambda, int nbFeatures) {
      this.beta = beta;
      gtdlambda = new GTDBetaLambda(alpha_theta, alpha_w, lambda, nbFeatures);
    }

    public void update(double rho_t, PVector phi_tp1, double r_tp1) {
      gtdlambda.update(rho_tm1, beta, rho_t, phi_t.values(), beta, phi_tp1, 0, r_tp1);
      rho_tm1 = rho_t;
      phi_t.set(phi_tp1);
    }

    public PVector theta() {
      return gtdlambda.v;
    }
  }

  private final RandomWalk randomWalkProblem = new RandomWalk(new Random(0));

  @Test
  public void testOnPolicyGTD() {
    TDTest.testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new OnPolicyGTD(0.1, 0.1, 0.0, 0.0, nbFeatures);
      }
    });
  }

  @Test
  public void testOnPolicyGTDCorrection() {
    TDTest.testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new OnPolicyGTD(0.1, 0.01, 0.5, 0.0, nbFeatures);
      }
    });
  }

  @Test
  public void testOnPolicyGTDLambda() {
    TDTest.testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new OnPolicyGTD(0.1, 0.01, 0.5, 0.1, nbFeatures);
      }
    });
  }

  @Test
  public void testOffPolicyGTD() {
    testOffPolicyGTD(0.1, 0.01, 0.5, 0.0, 0.2, 0.5);
    testOffPolicyGTD(0.1, 0.01, 0.5, 0.0, 0.5, 0.2);
  }

  @Test
  public void testOffPolicyGTDWithEligibility() {
    testOffPolicyGTD(0.1, 0.01, 0.0, 0.1, 0.2, 0.5);
    testOffPolicyGTD(0.1, 0.01, 0.5, 0.1, 0.2, 0.5);
    testOffPolicyGTD(0.1, 0.01, 0.5, 0.1, 0.5, 0.2);
  }

  private void testOffPolicyGTD(double beta, double alpha_theta, double alpha_w, double lambda,
      double targetLeftProbability, double behaviourLeftProbability) {
    Random random = new Random(0);
    ConstantPolicy behaviourPolicy = RandomWalk.newPolicy(random, behaviourLeftProbability);
    ConstantPolicy targetPolicy = RandomWalk.newPolicy(random, targetLeftProbability);
    RandomWalk problem = new RandomWalk(behaviourPolicy);
    FSGAgentState agentState = new FSGAgentState(problem);
    OffPolicyGTD gtd = new OffPolicyGTD(beta, alpha_theta, alpha_w, lambda, agentState.size);
    int nbEpisode = 0;
    double[] solution = agentState.computeSolution(targetPolicy, 1 - beta, lambda);
    while (TDTest.distanceToSolution(solution, gtd.theta()) > 0.05) {
      StepData stepData = agentState.step();
      double rho = 0.0;
      if (stepData.a_t != null)
        rho = targetPolicy.pi(stepData.v_t(), stepData.a_t) /
            behaviourPolicy.pi(stepData.v_t(), stepData.a_t);
      PVector currentFeatureState = agentState.currentFeatureState();
      gtd.update(rho, currentFeatureState, stepData.r_tp1);
      if (currentFeatureState == null) {
        nbEpisode += 1;
        Assert.assertTrue(nbEpisode < 100000);
      }
    }
    Assert.assertTrue(nbEpisode > 100);
    Assert.assertTrue(gtd.theta().checkValues());
  }
}
