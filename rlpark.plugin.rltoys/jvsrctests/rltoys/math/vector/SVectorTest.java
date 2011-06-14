package rltoys.math.vector;


public class SVectorTest extends VectorTest {

  @Override
  protected RealVector newVector(RealVector v) {
    return new SVector(v);
  }

  @Override
  protected RealVector newVector(double... d) {
    return new SVector(d);
  }

  @Override
  protected RealVector newVector(int s) {
    return new SVector(s);
  }
}
