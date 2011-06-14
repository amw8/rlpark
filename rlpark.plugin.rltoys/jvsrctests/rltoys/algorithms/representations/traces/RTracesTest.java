package rltoys.algorithms.representations.traces;

import org.junit.Assert;
import org.junit.Test;

import rltoys.math.vector.PVector;
import rltoys.math.vector.VectorsTestsUtils;


public class RTracesTest {
  @Test
  public void testRTraces() {
    AMaxTraces traces = new AMaxTraces(2, 0.01, 1.0);
    traces.update(0, new PVector(0.2, 1.2));
    VectorsTestsUtils.assertEquals(new PVector(0.2, 1.0), traces);
    traces.mapMultiplyToSelf(0.01);
    VectorsTestsUtils.assertEquals(new PVector(0.002, .01), traces);
    Assert.assertEquals(2, traces.nonZeroElements());
    traces.mapMultiplyToSelf(100);
    VectorsTestsUtils.assertEquals(new PVector(0.2, 1.0), traces);
    Assert.assertEquals(2, traces.nonZeroElements());
  }
}
