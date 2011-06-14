package rltoys.algorithms.representations.traces;

import rltoys.math.vector.PVector;
import rltoys.math.vector.RealVector;

public class PATraces extends PVector implements Traces {
  private static final long serialVersionUID = 3045584033177895140L;

  public PATraces() {
    super(0);
  }

  public PATraces(int size) {
    super(size);
  }

  @Override
  public PATraces newTraces(int size) {
    return new PATraces(size);
  }

  @Override
  public Traces update(double lambda, RealVector phi) {
    return update(lambda, phi, 1.0);
  }

  @Override
  public Traces update(double lambda, RealVector phi, double rho) {
    mapMultiplyToSelf(lambda).addToSelf(phi);
    if (rho != 1.0)
      mapMultiplyToSelf(rho);
    return this;
  }

  @Override
  public void clear() {
    set(0);
  }

  @Override
  public RealVector vect() {
    return this;
  }
}
