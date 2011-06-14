package rltoys.math.vector;

import java.io.Serializable;

public interface RealVector extends Serializable {
  RealVector copy();

  RealVector addToSelf(RealVector other);

  RealVector subtractToSelf(RealVector other);

  RealVector mapMultiply(double d);

  RealVector mapMultiplyToSelf(double d);

  int getDimension();

  double getEntry(int i);

  double dotProduct(RealVector other);

  void setSubVector(int i, RealVector other);

  void set(double d);

  RealVector subtract(RealVector other);

  void setEntry(int i, double d);

  RealVector add(RealVector other);

  RealVector newInstance(int size);

  boolean checkValues();

  double[] accessData();

  RealVector getSubVector(int index, int n);

  RealVector mapAbsToSelf();

  RealVector ebeMultiply(RealVector v);

  RealVector ebeMultiplyToSelf(RealVector phi_t);
}
