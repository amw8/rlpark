package rltoys.algorithms.learning.control.sarsa;

import org.junit.Test;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.learning.control.acting.EpsilonGreedy;
import rltoys.algorithms.learning.control.mountaincar.MountainCarOnPolicyTest;
import rltoys.algorithms.learning.control.sarsa.SarsaTest.SarsaControlFactory;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.algorithms.representations.traces.AMaxTraces;
import rltoys.algorithms.representations.traces.ATraces;
import rltoys.algorithms.representations.traces.MaxLengthTraces;
import rltoys.algorithms.representations.traces.RTraces;
import rltoys.algorithms.representations.traces.Traces;
import rltoys.math.vector.MutableVector;
import rltoys.math.vector.implementations.PVector;
import rltoys.math.vector.implementations.SVector;

public class TracesTest extends MountainCarOnPolicyTest {
  private void testTraces(final Traces traces) {
    runTestOnOnMountainCar(2000, new SarsaControlFactory(traces) {
      @Override
      protected Control createControl(EpsilonGreedy acting, StateToStateAction toStateAction, Sarsa predictor) {
        return new SarsaControl(acting, toStateAction, predictor);
      }
    });
  }

  private void testTraces(MutableVector prototype) {
    testTraces(new ATraces(prototype));
    testTraces(new AMaxTraces(prototype));
  }

  @Test
  public void testSarsaOnMountainCarSVectorTraces() {
    testTraces(new SVector(0));
    testTraces(new RTraces());
  }

  @Test
  public void testSarsaOnMountainCarPVectorTraces() {
    testTraces(new PVector(0));
  }

  @Test
  public void testSarsaOnMountainCarMaxLengthTraces() {
    testTraces(new MaxLengthTraces(new ATraces(new SVector(0)), 100));
    testTraces(new MaxLengthTraces(new AMaxTraces(new SVector(0)), 100));
    testTraces(new MaxLengthTraces(new RTraces(), 100));
  }
}
