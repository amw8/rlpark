package rlpark.plugin.robot.statemachine;

import junit.framework.Assert;

import org.junit.Test;

import rltoys.environments.envio.observations.TStep;


public class StateMachineTest {
  static private class StateNodeTest extends TimedState {
    protected int v = 0;

    public StateNodeTest(int nbTimeStep) {
      super(nbTimeStep);
    }

    @Override
    public void step(TStep step) {
      super.step(step);
      v++;
    }
  }

  private final StateNodeTest a = new StateNodeTest(20);
  private final StateNodeTest b = new StateNodeTest(10);

  @Test
  public void testStateMachine() {
    @SuppressWarnings("unchecked")
    StateMachine<TStep> stateMachine = new StateMachine<TStep>(a, b);
    for (int i = 0; i < 30; i++)
      stateMachine.step(null);
    Assert.assertEquals(a.v, 20);
    Assert.assertEquals(b.v, 10);
  }
}
