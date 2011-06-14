package rltoys.algorithms.representations.traces;

import rltoys.math.vector.PVector;
import rltoys.math.vector.RealVector;

public class PAMaxTraces extends PVector implements Traces {
  private static final long serialVersionUID = 5906561828266374507L;
  final private double maximumValue;

  public PAMaxTraces() {
    this(0, AMaxTraces.DefaultMaxValue);
  }

  public PAMaxTraces(double maximumValue) {
    this(0, maximumValue);
  }

  public PAMaxTraces(int size, double maximumValue) {
    super(size);
    this.maximumValue = AMaxTraces.DefaultMaxValue;
  }

  @Override
  public PAMaxTraces newTraces(int size) {
    return new PAMaxTraces(size, maximumValue);
  }

  @Override
  public Traces update(double lambda, RealVector phi) {
    return update(lambda, phi, 1.0);
  }

  @Override
  public Traces update(double lambda, RealVector phi, double rho) {
    mapMultiplyToSelf(lambda).addToSelf(phi);
    for (int i = 0; i < data.length; i++) {
      double value = data[i];
      if (Math.abs(value) > maximumValue)
        data[i] = maximumValue * Math.signum(value);
    }
    if (rho != 1.0)
      mapMultiplyToSelf(rho);
    return this;
  }

  @Override
  public void clear() {
    set(0);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    return super.equals(object);
  }

  @Override
  public RealVector vect() {
    return this;
  }
}
