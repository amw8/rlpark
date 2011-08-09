package rltoys.math.vector;

import org.junit.Assert;
import org.junit.Test;


public class BVectorTest {
  private final BVector a = new BVector(10, new int[] { 1, 2, 4, 5 });
  private final PVector b = new PVector(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0);
  private final SVector c = new SVector(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0);

  @Test
  public void testAdd() {
    PVector e = new PVector(0.1, 1.2, 1.3, 0.4, 1.5, 1.6, 0.7, 0.8, 0.9, 1.0);
    VectorsTestsUtils.assertEquals(e, a.add(b));
    VectorsTestsUtils.assertEquals(e, a.add(c));
  }

  @Test
  public void testSubtract() {
    PVector e = new PVector(-0.1, 0.8, 0.7, -0.4, 0.5, 0.4, -0.7, -0.8, -0.9, -1.0);
    VectorsTestsUtils.assertEquals(e, a.subtract(b));
    VectorsTestsUtils.assertEquals(e, a.subtract(c));
  }

  @Test
  public void testDotProduct() {
    Assert.assertEquals(0.2 + 0.3 + 0.5 + 0.6, a.dotProduct(b), .0);
    Assert.assertEquals(0.2 + 0.3 + 0.5 + 0.6, a.dotProduct(c), .0);
  }

  @Test
  public void testDimension() {
    Assert.assertEquals(10, a.getDimension());
  }

  @Test
  public void testMapMultiply() {
    PVector e = new PVector(0.0, 2.0, 2.0, 0.0, 2.0, 2.0, 0.0, 0.0, 0.0, 0.0);
    VectorsTestsUtils.assertEquals(a.mapMultiply(2.0), e);
    e = new PVector(0.0, 4.0, 4.0, 0.0, 4.0, 4.0, 0.0, 0.0, 0.0, 0.0);
    VectorsTestsUtils.assertEquals(a.mapMultiply(2.0).mapMultiply(2.0), e);
    VectorsTestsUtils.assertEquals(a.mapMultiply(2.0).mapMultiplyToSelf(2.0), e);
  }
}
