package rltoys.algorithms.learning.control.sarsa;


import java.util.Random;

import org.junit.Test;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.learning.control.acting.EpsilonGreedy;
import rltoys.algorithms.learning.control.mountaincar.ActionValueMountainCarAgentFactory;
import rltoys.algorithms.learning.control.mountaincar.MountainCarOnPolicyTest;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.algorithms.representations.tilescoding.TileCoders;
import rltoys.algorithms.representations.tilescoding.TileCodersHashing;
import rltoys.algorithms.representations.tilescoding.discretizer.PartitionFactory;
import rltoys.algorithms.representations.tilescoding.hashing.UNH;
import rltoys.algorithms.representations.traces.AMaxTraces;
import rltoys.algorithms.representations.traces.ATraces;
import rltoys.algorithms.representations.traces.PATraces;
import rltoys.algorithms.representations.traces.RTraces;
import rltoys.algorithms.representations.traces.Traces;
import rltoys.math.ranges.Range;
import rltoys.math.vector.RealVector;


public class SarsaTest extends MountainCarOnPolicyTest {
  abstract static class SarsaControlFactory extends ActionValueMountainCarAgentFactory {
    private final Traces traces;

    public SarsaControlFactory() {
      this(new PATraces());
    }

    public SarsaControlFactory(Traces traces) {
      this.traces = traces;
    }

    @Override
    protected Predictor createPredictor(Action[] actions, StateToStateAction toStateAction, int nbActiveFatures,
        int nbFeatures) {
      return new Sarsa(0.1 / nbActiveFatures, 0.9, 0.3, nbFeatures, traces);
    }

    @Override
    protected Control createControl(Predictor predictor, TileCoders tilesCoder, StateToStateAction toStateAction,
        EpsilonGreedy acting) {
      return createControl(acting, toStateAction, (Sarsa) predictor);
    }

    abstract protected Control createControl(EpsilonGreedy acting, StateToStateAction toStateAction, Sarsa predictor);
  }

  static protected class ExpectedSarsaControl implements Control {
    private static final long serialVersionUID = 1L;
    private final ExpectedSarsa expectedSarsa;
    private final EpsilonGreedy acting;

    protected ExpectedSarsaControl(ExpectedSarsa expectedSarsa, EpsilonGreedy acting) {
      this.expectedSarsa = expectedSarsa;
      this.acting = acting;
    }

    @Override
    public Action step(RealVector s_t, Action a_t, RealVector s_tp1, double r_tp1) {
      Action a_tp1 = acting.decide(s_tp1);
      expectedSarsa.update(s_t, a_t, r_tp1, s_tp1);
      return a_tp1;
    }
  }

  @Test
  public void testExpectedSarsaOnMountainCar() {
    runTestOnOnMountainCar(2000, new SarsaControlFactory() {
      @Override
      protected Control createControl(EpsilonGreedy acting, StateToStateAction toStateAction, Sarsa predictor) {
        return new ExpectedSarsaControl(new ExpectedSarsa(acting.actions(), acting, toStateAction, predictor), acting);
      }
    });
  }

  @Test
  public void testSarsaOnMountainCar() {
    runTestOnOnMountainCar(2000, new SarsaControlFactory() {
      @Override
      protected Control createControl(EpsilonGreedy acting, StateToStateAction toStateAction, Sarsa predictor) {
        return new SarsaControl(acting, toStateAction, predictor);
      }
    });
  }

  @Test
  public void testSarsaOnMountainCarAccumulatingTraces() {
    runTestOnOnMountainCar(2000, new SarsaControlFactory(new ATraces()) {
      @Override
      protected Control createControl(EpsilonGreedy acting, StateToStateAction toStateAction, Sarsa predictor) {
        return new SarsaControl(acting, toStateAction, predictor);
      }
    });
  }

  @Test
  public void testSarsaOnMountainCarAMaxTraces() {
    runTestOnOnMountainCar(2000, new SarsaControlFactory(new AMaxTraces()) {
      @Override
      protected Control createControl(EpsilonGreedy acting, StateToStateAction toStateAction, Sarsa predictor) {
        return new SarsaControl(acting, toStateAction, predictor);
      }
    });
  }

  @Test
  public void testSarsaOnMountainCarReplacingTraces() {
    runTestOnOnMountainCar(2000, new SarsaControlFactory(new RTraces()) {
      @Override
      protected Control createControl(EpsilonGreedy acting, StateToStateAction toStateAction, Sarsa predictor) {
        return new SarsaControl(acting, toStateAction, predictor);
      }
    });
  }

  @Test
  public void testSarsaOnMountainCarHashingTileCodingWithRandom() {
    runTestOnOnMountainCar(new TileCodersFactory() {
      @Override
      public TileCoders create(Range[] ranges) {
        Random random = new Random(0);
        PartitionFactory discretizerFactory = new PartitionFactory(ranges);
        discretizerFactory.setRandom(random, 0.1);
        TileCoders tileCoders = new TileCodersHashing(new UNH(random, 10000), discretizerFactory, ranges.length);
        return tileCoders;
      }
    }, 2000, new SarsaControlFactory(new AMaxTraces()) {
      @Override
      protected Control createControl(EpsilonGreedy acting, StateToStateAction toStateAction, Sarsa predictor) {
        return new SarsaControl(acting, toStateAction, predictor);
      }
    });
  }
}
