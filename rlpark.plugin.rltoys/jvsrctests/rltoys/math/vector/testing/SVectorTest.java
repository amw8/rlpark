package rltoys.math.vector.testing;

import junit.framework.Assert;

import org.junit.Test;

import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.BVector;
import rltoys.math.vector.implementations.SVector;
import rltoys.math.vector.implementations.Vectors;


public class SVectorTest extends VectorTest {

  @Test
  public void addBVector() {
    SVector v = new SVector(10);
    BVector b = BVector.toBVector(10, new int[] { 1, 2, 3 });
    v.addToSelf(b);
    Assert.assertEquals(b.nonZeroElements(), v.nonZeroElements());
    Assert.assertTrue(Vectors.equals(b, v));
  }

  @Override
  protected RealVector newVector(RealVector v) {
    return newSVector(v);
  }

  @Override
  protected RealVector newVector(double... d) {
    return newSVector(d);
  }

  @Override
  protected RealVector newVector(int s) {
    return new SVector(s);
  }
}
