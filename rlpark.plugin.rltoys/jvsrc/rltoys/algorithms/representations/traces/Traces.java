package rltoys.algorithms.representations.traces;

import java.io.Serializable;

import rltoys.math.vector.RealVector;

public interface Traces extends Serializable {
  Traces newTraces(int size);

  Traces update(double lambda, RealVector phi);

  Traces update(double lambda, RealVector phi, double rho);

  void clear();

  RealVector vect();
}
