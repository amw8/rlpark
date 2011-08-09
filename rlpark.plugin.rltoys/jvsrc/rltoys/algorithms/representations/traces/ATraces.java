package rltoys.algorithms.representations.traces;

import java.util.Iterator;

import rltoys.math.vector.RealVector;
import rltoys.math.vector.SVector;

/**
 * Accumulating traces
 */
public class ATraces extends SVector implements Traces {
  private static final long serialVersionUID = 8878754041042713717L;
  public static final double DefaultZeroValue = 1e-8;
  protected double epsilon;

  public ATraces() {
    this(0);
  }

  public ATraces(int size) {
    this(size, DefaultZeroValue);
  }

  public ATraces(int size, double epsilon) {
    super(size);
    this.epsilon = epsilon;
  }

  public ATraces(RealVector orig) {
    super(orig);
    epsilon = orig instanceof ATraces ? ((ATraces) orig).epsilon : DefaultZeroValue;
  }

  public ATraces(double... o) {
    super(o);
    epsilon = DefaultZeroValue;
  }

  @Override
  public ATraces newTraces(int size) {
    return new ATraces(size, epsilon);
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
    if (rho != 1.0)
      mapMultiplyToSelf(rho);
    return this;
  }

  protected void clearBelowThreshold() {
    for (Iterator<Double> iterator = values.values().iterator(); iterator.hasNext();) {
      double value = iterator.next();
      if (Math.abs(value) < epsilon)
        iterator.remove();
    }
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
