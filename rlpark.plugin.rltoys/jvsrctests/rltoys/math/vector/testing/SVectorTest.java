package rltoys.math.vector.testing;

import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.SVector;


public class SVectorTest extends VectorTest {

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
