package rltoys.algorithms.representations.acting;

import java.io.Serializable;

import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.RealVector;

public interface Policy extends Serializable {
  double pi(RealVector s, Action a);

  Action decide(RealVector s);
}
