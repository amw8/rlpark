package rltoys.math.vector.testing;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import rltoys.math.vector.MutableVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.VectorEntry;
import rltoys.math.vector.implementations.BVector;
import rltoys.math.vector.implementations.SVector;
import rltoys.math.vector.implementations.Vectors;


public class SVectorTest extends VectorTest {
  @Test
  public void testIteratorRemove() {
    MutableVector v = a.copyAsMutable();
    Iterator<VectorEntry> iterator = v.iterator();
    iterator.next();
    iterator.remove();
    iterator.next();
    iterator = v.iterator();
    iterator.next();
    iterator.remove();
    VectorsTestsUtils.assertEquals(newVector(0.0, 0.0, 0.0, 0.0, 3.0), v);
  }

  @Test
  public void testActiveIndexes() {
    Assert.assertArrayEquals(new int[] { 1, 2, 4 }, ((SVector) a).activeIndexes());
    Assert.assertArrayEquals(new int[] { 0, 1, 4 }, ((SVector) b).activeIndexes());
  }

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
