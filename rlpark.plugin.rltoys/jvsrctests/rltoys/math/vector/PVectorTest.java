package rltoys.math.vector;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import rltoys.math.representations.Function;
import rltoys.utils.Utils;


public class PVectorTest extends VectorTest {
  private final List<Function> fs = Utils.asList(new Function[] { new Function() {
    private static final long serialVersionUID = -8858291195286906960L;

    @Override
    public double value() {
      return 79;
    }
  }, new Function() {
    private static final long serialVersionUID = 1221630240720817368L;

    @Override
    public double value() {
      return 81;
    }
  } });

  @Test
  public void testMean() {
    Assert.assertEquals(1.5, PVectors.mean((PVector) a), 0.0);
    Assert.assertEquals(3.5, PVectors.mean((PVector) b), 0.0);
  }


  @Test
  public void testSetFunction() {
    PVector v = newVector(2);
    PVectors.set(v, fs);
    VectorsTestsUtils.assertEquals(newVector(79.0, 81.0), v);
  }

  @Test
  public void testSetPVector() {
    PVector v = newVector(2);
    v.set(b);
    VectorsTestsUtils.assertEquals(v, b);
  }

  @Test
  public void testSetDoubleArray() {
    PVector v = newVector(2);
    v.set(new double[] { 1.0, 2.0 });
    VectorsTestsUtils.assertEquals(v, a);
  }

  @Override
  protected PVector newVector(int s) {
    return new PVector(s);
  }

  @Override
  protected PVector newVector(double... d) {
    return new PVector(d);
  }

  @Override
  protected PVector newVector(RealVector v) {
    return new PVector(v);
  }
}
