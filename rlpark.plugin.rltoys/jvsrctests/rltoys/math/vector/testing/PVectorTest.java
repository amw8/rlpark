package rltoys.math.vector.testing;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import rltoys.math.representations.Function;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;
import rltoys.math.vector.implementations.PVectors;
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
    Assert.assertEquals(6.0 / 5.0, PVectors.mean((PVector) a), 0.0);
    Assert.assertEquals(11.0 / 5.0, PVectors.mean((PVector) b), 0.0);
  }

  @Test
  public void testSetDouble() {
    PVector v = newVector(2);
    v.set(84.0);
    VectorsTestsUtils.assertEquals(v, newVector(84.0, 84.0));
    v.set(0.0);
    VectorsTestsUtils.assertEquals(v, newVector(0.0, 0.0));
  }

  @Test
  public void testAddDataToSelf() {
    PVector v = new PVector(1.0, 1.0, 1.0, 1.0, 1.0);
    v.addToSelf(new PVector(a).data);
    VectorsTestsUtils.assertEquals(new PVector(1.0, 2.0, 3.0, 1.0, 4.0), v);
  }

  @Test
  public void testSetFunction() {
    PVector v = newVector(2);
    PVectors.set(v, fs);
    VectorsTestsUtils.assertEquals(newVector(79.0, 81.0), v);
  }

  @Test
  public void testSetPVector() {
    PVector v = newVector(b.getDimension());
    v.set(b);
    VectorsTestsUtils.assertEquals(v, b);
  }

  @Test
  public void testSetDoubleArray() {
    PVector v = newVector(a.getDimension());
    v.set(new double[] { 0.0, 1.0, 2.0, 0.0, 3.0 });
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
