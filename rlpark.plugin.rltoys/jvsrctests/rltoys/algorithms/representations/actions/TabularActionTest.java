package rltoys.algorithms.representations.actions;

import org.junit.Test;

import rltoys.math.vector.implementations.PVector;
import rltoys.math.vector.testing.VectorsTestsUtils;

@SuppressWarnings("serial")
public class TabularActionTest {
  static private Action a0 = new Action() {
  };

  static private Action a1 = new Action() {
  };

  @Test
  public void testTabularAction() {
    TabularAction tabularAction = new TabularAction(new Action[] { a0, a1 }, 2);
    PVector s = new PVector(2.0, 3.0);
    VectorsTestsUtils.assertEquals(new PVector(new double[] { 2.0, 3.0, 0.0, 0.0 }), tabularAction.stateAction(s, a0));
    VectorsTestsUtils.assertEquals(new PVector(new double[] { 0.0, 0.0, 2.0, 3.0 }), tabularAction.stateAction(s, a1));
  }
}
