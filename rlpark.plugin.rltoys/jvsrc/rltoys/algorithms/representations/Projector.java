package rltoys.algorithms.representations;

import java.io.Serializable;

import rltoys.math.vector.RealVector;

public interface Projector extends Serializable {
  /**
   * Project an observation. If the observation is null, it should return a
   * non-null vector representing an absorbing state.
   * 
   * @param obs
   *          observation to project
   * @return a non-null vector
   */
  RealVector project(double[] obs);
}
