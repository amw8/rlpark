package rltoys.math.vector.testing;

import org.junit.Assert;

import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.Vectors;


public class VectorsTestsUtils {

  public static void assertEquals(RealVector a, RealVector b) {
    Assert.assertTrue(Vectors.equals(a, b));
    Assert.assertArrayEquals(a.accessData(), b.accessData(), Float.MIN_VALUE);
  }

}
