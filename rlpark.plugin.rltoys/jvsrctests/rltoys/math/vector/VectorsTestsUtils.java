package rltoys.math.vector;

import org.junit.Assert;

public class VectorsTestsUtils {

  public static void assertEquals(RealVector a, RealVector b) {
    Assert.assertTrue(Vectors.equals(a, b));
  }

}
