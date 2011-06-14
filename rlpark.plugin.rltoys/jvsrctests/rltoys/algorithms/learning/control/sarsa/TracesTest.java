package rltoys.algorithms.learning.control.sarsa;

import org.junit.Test;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.learning.control.acting.EpsilonGreedy;
import rltoys.algorithms.learning.control.mountaincar.MountainCarOnPolicyTest;
import rltoys.algorithms.learning.control.sarsa.SarsaTest.SarsaControlFactory;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.algorithms.representations.traces.AMaxTraces;
import rltoys.algorithms.representations.traces.ATraces;
import rltoys.algorithms.representations.traces.NAMaxTraces;
import rltoys.algorithms.representations.traces.NATraces;
import rltoys.algorithms.representations.traces.NRTraces;
import rltoys.algorithms.representations.traces.Traces;

public class TracesTest extends MountainCarOnPolicyTest {
  private void testTraces(final Traces traces) {
    runTestOnOnMountainCar(2000, new SarsaControlFactory(traces) {
      @Override
      protected Control createControl(EpsilonGreedy acting, StateToStateAction toStateAction, Sarsa predictor) {
        return new SarsaControl(acting, toStateAction, predictor);
      }
    });
  }

  @Test
  public void testSarsaOnMountainCarATraces() {
    testTraces(new ATraces());
  }

  @Test
  public void testSarsaOnMountainCarRTraces() {
    testTraces(new AMaxTraces());
  }

  @Test
  public void testSarsaOnMountainCarNATraces() {
    testTraces(new NATraces(100));
  }

  @Test
  public void testSarsaOnMountainCarNAMaxTraces() {
    testTraces(new NAMaxTraces(100));
  }

  @Test
  public void testSarsaOnMountainCarNRTraces() {
    testTraces(new NRTraces(100));
  }
}
