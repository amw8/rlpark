package rltoys.algorithms.representations.traces;

import java.io.Serializable;

import rltoys.math.vector.MutableVector;
import rltoys.math.vector.RealVector;

public interface Traces extends Serializable {
  Traces newTraces(int size);

  void update(double lambda, RealVector phi);

  void update(double lambda, RealVector phi, double rho);

  void clear();

  MutableVector vect();
}
