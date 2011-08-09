package rltoys.math.vector;

import java.io.Serializable;

public interface RealVector extends Serializable {
  double[] accessData();

  RealVector getSubVector(int index, int n);

  RealVector copy();

  ModifiableVector mapMultiply(double d);

  int getDimension();

  double getEntry(int i);

  double dotProduct(RealVector other);

  ModifiableVector subtract(RealVector other);

  ModifiableVector add(RealVector other);

  ModifiableVector newInstance(int size);

  boolean checkValues();

  ModifiableVector ebeMultiply(RealVector v);

  ModifiableVector copyAsMutable();
}
