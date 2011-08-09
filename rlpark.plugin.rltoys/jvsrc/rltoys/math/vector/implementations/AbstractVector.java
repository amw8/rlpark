package rltoys.math.vector.implementations;

import rltoys.math.vector.MutableVector;
import rltoys.math.vector.RealVector;

public abstract class AbstractVector implements RealVector {
  private static final long serialVersionUID = 5863507432853349597L;
  public final int size;

  protected AbstractVector(int size) {
    this.size = size;
  }

  @Override
  public MutableVector add(RealVector other) {
    return copyAsMutable().addToSelf(other);
  }

  @Override
  public MutableVector mapMultiply(double d) {
    return copyAsMutable().mapMultiplyToSelf(d);
  }

  @Override
  public MutableVector subtract(RealVector other) {
    return copyAsMutable().subtractToSelf(other);
  }

  @Override
  public MutableVector ebeMultiply(RealVector other) {
    return copyAsMutable().ebeMultiplyToSelf(other);
  }

  @Override
  public int getDimension() {
    return size;
  }
}
