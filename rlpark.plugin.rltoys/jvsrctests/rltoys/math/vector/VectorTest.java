package rltoys.math.vector;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;


public abstract class VectorTest {

  protected final RealVector a = newVector(1.0, 2.0);
  protected final RealVector b = newVector(3.0, 4.0);
  protected final RealVector c = new BVector(2, new int[] { 1 });

  @After
  public void after() {
    VectorsTestsUtils.assertEquals(a, newVector(1.0, 2.0));
    VectorsTestsUtils.assertEquals(b, newVector(3.0, 4.0));
    VectorsTestsUtils.assertEquals(c, newVector(0.0, 1.0));
  }

  @Test
  public void testVectorVector() {
    RealVector c = newVector(a);
    VectorsTestsUtils.assertEquals(a, c);
    Assert.assertFalse(c.equals(b));
    Assert.assertFalse(a.equals(newVector(1.0)));
  }

  @Test
  public void testEquals() {
    Assert.assertTrue(a.equals(a));
    Assert.assertFalse(a.equals(null));
  }

  @Test
  public void testNewInstance() {
    RealVector v = a.newInstance(a.getDimension());
    VectorsTestsUtils.assertEquals(newVector(a.getDimension()), v);
  }

  @Test
  public void testSetEntry() {
    ModifiableVector v = a.copyAsMutable();
    v.setEntry(1, 3);
    VectorsTestsUtils.assertEquals(newVector(1.0, 3.0), v);
  }

  @Test
  public void testSetDouble() {
    ModifiableVector v = newVector(2).copyAsMutable();
    v.set(84.0);
    VectorsTestsUtils.assertEquals(v, newVector(84.0, 84.0));
    v.set(0.0);
    VectorsTestsUtils.assertEquals(v, newVector(0.0, 0.0));
  }

  @Test
  public void testDotProductPVector() {
    Assert.assertEquals(11.0, a.dotProduct(b), 0.0);
  }

  @Test
  public void testDotProductSVector() {
    Assert.assertEquals(11.0, a.dotProduct(new SVector(b)), 0.0);
    Assert.assertEquals(2.0, a.dotProduct(c), 0.0);
  }

  @Test
  public void testClone() {
    RealVector ca = a.copy();
    Assert.assertNotSame(a, ca);
    VectorsTestsUtils.assertEquals(a, ca);
  }

  @Test
  public void testPlus() {
    VectorsTestsUtils.assertEquals(newVector(4.0, 6.0), a.add(b));
  }

  @Test
  public void testMinus() {
    VectorsTestsUtils.assertEquals(newVector(-2.0, -2.0), a.subtract(b));
    VectorsTestsUtils.assertEquals(newVector(-1.0, 0.0), a.mapMultiply(2.0).subtract(b));
  }

  @Test
  public void testPlusSVector() {
    VectorsTestsUtils.assertEquals(newVector(4.0, 6.0), a.add(new SVector(b)));
    VectorsTestsUtils.assertEquals(newVector(1.0, 3.0), a.add(c));
  }

  @Test
  public void testMinusSVector() {
    VectorsTestsUtils.assertEquals(newVector(-2.0, -2.0), a.subtract(new SVector(b)));
    VectorsTestsUtils.assertEquals(newVector(1.0, 1.0), a.subtract(c));
  }

  @Test
  public void testMapTimes() {
    VectorsTestsUtils.assertEquals(newVector(5.0, 10.0), a.mapMultiply(5.0));
    VectorsTestsUtils.assertEquals(newVector(0.0, 0.0), a.mapMultiply(0.0));
  }

  @Test
  public void testSubtractToSelf() {
    VectorsTestsUtils.assertEquals(newVector(1.0, 1.0), a.copyAsMutable().subtractToSelf(new PVector(0.0, 1.0)));
    VectorsTestsUtils.assertEquals(newVector(1.0, 1.0), a.copyAsMutable().subtractToSelf(new SVector(0.0, 1.0)));
    VectorsTestsUtils.assertEquals(newVector(0.0, 0.0), a.copyAsMutable().subtractToSelf(a));
    VectorsTestsUtils.assertEquals(newVector(1.0, 1.0), a.copyAsMutable().subtractToSelf(c));
  }

  @Test
  public void testAddToSelf() {
    VectorsTestsUtils.assertEquals(newVector(1.0, 1.0), a.copyAsMutable().addToSelf(new PVector(0.0, -1.0)));
    VectorsTestsUtils.assertEquals(newVector(1.0, 1.0), a.copyAsMutable().addToSelf(new SVector(0.0, -1.0)));
    VectorsTestsUtils.assertEquals(newVector(0.0, 0.0), a.copyAsMutable().addToSelf(a.mapMultiply(-1)));
    VectorsTestsUtils.assertEquals(newVector(1.0, 3.0), a.copyAsMutable().addToSelf(c));
  }

  @Test
  public void testEbeMultiply() {
    RealVector a2 = newVector(3, 4, 5);
    RealVector a1 = newVector(-1, 1, 2);
    VectorsTestsUtils.assertEquals(new PVector(-3, 4, 10), a1.ebeMultiply(a2));
  }

  @Test
  public void testEbeMultiplySelf() {
    RealVector a2 = newVector(3, 4, 5);
    RealVector a1 = newVector(-1, 1, 2);
    VectorsTestsUtils.assertEquals(new PVector(-3, 4, 10), a1.copyAsMutable().ebeMultiplyToSelf(a2));
  }

  @Test
  public void testCheckValue() {
    Assert.assertTrue(newVector(1.0, 1.0).checkValues());
    Assert.assertFalse(newVector(1.0, Double.NaN).checkValues());
    Assert.assertFalse(newVector(1.0, Double.POSITIVE_INFINITY).checkValues());
  }

  @Test
  public void testToString() {
    newVector(4, 1.0).toString();
  }

  protected abstract RealVector newVector(RealVector v);

  protected abstract RealVector newVector(double... d);

  protected abstract RealVector newVector(int s);
}