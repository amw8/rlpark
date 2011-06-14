package rltoys.algorithms.representations.traces;

import java.util.Map.Entry;

import rltoys.math.vector.RealVector;

/**
 * Replacing traces with a close to constant number of active features
 */
public class NAMaxTraces extends NATraces {
  private static final long serialVersionUID = 3624335720811519365L;
  final private double maximumValue;

  public NAMaxTraces(int nbActiveFeatures) {
    this(nbActiveFeatures, 0, AMaxTraces.DefaultMaxValue);
  }

  public NAMaxTraces(int nbActiveFeatures, int size, double maximumValue) {
    super(nbActiveFeatures, size);
    this.maximumValue = maximumValue;
  }

  @Override
  public NAMaxTraces newTraces(int size) {
    return new NAMaxTraces(nbActiveFeatures, size, maximumValue);
  }

  @Override
  public Traces update(double lambda, RealVector phi, double rho) {
    mapMultiplyToSelf(lambda).addToSelf(phi);
    for (Entry<Integer, Double> entry : values.entrySet()) {
      double value = entry.getValue();
      if (Math.abs(value) > maximumValue)
        entry.setValue(maximumValue * Math.signum(value));
    }
    checkSize(lambda, phi);
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
