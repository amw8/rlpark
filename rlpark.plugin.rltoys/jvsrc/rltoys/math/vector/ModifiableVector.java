package rltoys.math.vector;



public interface ModifiableVector extends RealVector {
  @Override
  ModifiableVector copy();

  ModifiableVector addToSelf(RealVector other);

  ModifiableVector subtractToSelf(RealVector other);

  ModifiableVector mapMultiplyToSelf(double d);

  void setSubVector(int i, RealVector other);

  void set(double d);

  void setEntry(int i, double d);

  ModifiableVector mapAbsToSelf();

  ModifiableVector ebeMultiplyToSelf(RealVector phi_t);
}
