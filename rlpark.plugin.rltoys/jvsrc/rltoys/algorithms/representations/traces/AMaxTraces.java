package rltoys.algorithms.representations.traces;

import java.util.Map;

import rltoys.math.vector.RealVector;


/**
 * Replacing traces
 */
public class AMaxTraces extends ATraces {
  private static final long serialVersionUID = 8063854269195146376L;
  static final protected double DefaultMaxValue = 1.0;
  final private double maximumValue;

  public AMaxTraces() {
    this(0, ATraces.DefaultZeroValue, DefaultMaxValue);
  }

  public AMaxTraces(int size) {
    this(size, DefaultZeroValue, DefaultMaxValue);
  }


  public AMaxTraces(double epsilon, double maximumValue) {
    this(0, epsilon, maximumValue);
  }

  public AMaxTraces(int size, double epsilon, double maximumValue) {
    super(size, epsilon);
    this.maximumValue = maximumValue;
  }

  @Override
  public AMaxTraces newTraces(int size) {
    return new AMaxTraces(size, epsilon, maximumValue);
  }

  @Override
  public Traces update(double lambda, RealVector phi) {
    return update(lambda, phi, 1.0);
  }

  @Override
  public Traces update(double lambda, RealVector phi, double rho) {
    mapMultiplyToSelf(lambda);
    addToSelf(phi);
    clearBelowThreshold();
    for (Map.Entry<Integer, Double> entry : values.entrySet()) {
      double value = entry.getValue();
      if (Math.abs(value) > maximumValue)
        entry.setValue(maximumValue * Math.signum(value));
    }
    if (rho != 1.0)
      mapMultiplyToSelf(rho);
    return this;
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    return super.equals(object);
  }
}
