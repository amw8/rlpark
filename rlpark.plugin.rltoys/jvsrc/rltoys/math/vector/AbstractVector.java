package rltoys.math.vector;

public abstract class AbstractVector implements RealVector {
  private static final long serialVersionUID = 5863507432853349597L;

  @Override
  public ModifiableVector add(RealVector other) {
    return copyAsMutable().addToSelf(other);
  }

  @Override
  public ModifiableVector mapMultiply(double d) {
    return copyAsMutable().mapMultiplyToSelf(d);
  }

  @Override
  public ModifiableVector subtract(RealVector other) {
    return copyAsMutable().subtractToSelf(other);
  }

  @Override
  public ModifiableVector ebeMultiply(RealVector other) {
    return copyAsMutable().ebeMultiplyToSelf(other);
  }
}
