package rltoys.algorithms.learning.predictions.td;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.traces.AMaxTraces;
import rltoys.algorithms.representations.traces.ATraces;
import rltoys.environments.stategraph.FSGAgentState;
import rltoys.environments.stategraph.FiniteStateGraph;
import rltoys.environments.stategraph.FiniteStateGraph.StepData;
import rltoys.environments.stategraph.LineProblem;
import rltoys.environments.stategraph.RandomWalk;
import rltoys.math.vector.CachedVector;
import rltoys.math.vector.PVector;
import rltoys.math.vector.RealVector;


public class TDTest {
  public static class TDHelper implements Predictor {
    private static final long serialVersionUID = 1769015377601578674L;
    private final CachedVector phi_t;
    public final OnPolicyTD td;

    public TDHelper(OnPolicyTD td) {
      this.td = td;
      phi_t = new CachedVector();
    }

    public double learn(double r_tp1, RealVector phi_tp1) {
      double delta_t = 0.0;
      delta_t = td.update(phi_t.values(), phi_tp1, r_tp1);
      phi_t.set(phi_tp1);
      return delta_t;
    }

    @Override
    public double predict(RealVector x) {
      return td.predict(x);
    }
  }

  static public interface OnPolicyTDFactory {
    OnPolicyTD create(int nbFeatures);
  }

  private final LineProblem lineProblem = new LineProblem();
  private final RandomWalk randomWalkProblem = new RandomWalk(new Random(0));

  @Test
  public void testTDOnLineProblem() {
    testTD(lineProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new TD(0.9, 0.01, nbFeatures);
      }
    });
  }

  @Test
  public void testTDCOnLineProblem() {
    testTD(lineProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new TDC(0.9, 0.01, 0.5, nbFeatures);
      }
    });
  }

  @Test
  public void testTDOnRandomWalkProblem() {
    testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new TD(0.9, 0.01, nbFeatures);
      }
    });
  }

  @Test
  public void testTDLambdaOnRandomWalkProblem() {
    testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new TDLambda(0.1, 0.9, 0.01, nbFeatures);
      }
    });
  }

  @Test
  public void testTDCOnRandomWalkProblem() {
    testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new TDC(0.9, 0.01, 0.5, nbFeatures);
      }
    });
  }

  @Test
  public void testGTDOnRandomWalkProblem() {
    testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new GTD(0.9, 0.01, 0.5, nbFeatures);
      }
    });
  }

  @Test
  public void testGTDLambda0OnRandomWalkProblem() {
    testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new GTDLambda(0.0, 0.9, 0.01, 0.5, nbFeatures, new AMaxTraces(1e-8, 1));
      }
    });
  }

  @Test
  public void testGTDLambdaOnRandomWalkProblem() {
    testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new GTDLambda(0.6, 0.9, 0.01, 0.5, nbFeatures, new AMaxTraces());
      }
    });
  }

  @Test
  public void testTDLambdaAutostepOnRandomWalkProblem() {
    testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new TDLambdaAutostep(0.1, 0.9, nbFeatures);
      }
    });
  }

  @Test
  public void testTDLambdaAutostepSparseOnRandomWalkProblem() {
    testTD(randomWalkProblem, new OnPolicyTDFactory() {
      @Override
      public OnPolicyTD create(int nbFeatures) {
        return new TDLambdaAutostep(0.1, 0.9, nbFeatures, new ATraces());
      }
    });
  }

  public static void testTD(FiniteStateGraph problem, OnPolicyTDFactory tdFactory) {
    FSGAgentState agentState = new FSGAgentState(problem);
    TDHelper td = new TDHelper(tdFactory.create(agentState.size));
    int nbEpisode = 0;
    double[] solution = problem.expectedDiscountedSolution();
    while (distanceToSolution(solution, td.td.theta()) > 0.05) {
      StepData stepData = agentState.step();
      RealVector currentFeatureState = agentState.currentFeatureState();
      td.learn(stepData.r_tp1, currentFeatureState);
      if (currentFeatureState == null) {
        nbEpisode += 1;
        Assert.assertTrue(nbEpisode < 100000);
      }
    }
    Assert.assertTrue(nbEpisode > 2);
    Assert.assertTrue(td.td.theta().checkValues());
  }

  static public double distanceToSolution(double[] solution, PVector theta) {
    Assert.assertTrue(solution.length == theta.size);
    double max = 0;
    for (int i = 0; i < solution.length; i++)
      max = Math.max(max, Math.abs(solution[i] - theta.data[i]));
    return max;
  }
}
