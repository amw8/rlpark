package rltoys.algorithms.representations;

import java.io.Serializable;

import rltoys.math.vector.RealVector;

public interface Projector extends Serializable {
  RealVector project(double[] ds);

  int vectorSize();
}
