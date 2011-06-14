package rltoys.algorithms.representations.traces;

import org.junit.Assert;
import org.junit.Test;

import rltoys.math.vector.PVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.VectorTest;
import rltoys.math.vector.VectorsTestsUtils;


public class ATracesTest extends VectorTest {
  @Test
  public void testATraces() {
    ATraces traces = new ATraces(2, 0.01);
    traces.setEntry(0, 0.2);
    traces.setEntry(1, 1.2);
    VectorsTestsUtils.assertEquals(new PVector(0.2, 1.2), traces);
    traces.mapMultiplyToSelf(0.01);
    VectorsTestsUtils.assertEquals(new PVector(0.002, .012), traces);
    Assert.assertEquals(2, traces.nonZeroElements());
  }

  @Override
  protected RealVector newVector(RealVector v) {
    return new ATraces(v);
  }

  @Override
  protected RealVector newVector(double... d) {
    return new ATraces(d);
  }

  @Override
  protected RealVector newVector(int s) {
    return new ATraces(s);
  }
}
