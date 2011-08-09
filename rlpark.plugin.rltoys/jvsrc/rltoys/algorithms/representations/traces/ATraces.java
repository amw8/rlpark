package rltoys.algorithms.representations.traces;

import java.util.Iterator;

import rltoys.math.vector.DenseVector;
import rltoys.math.vector.MutableVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.VectorEntry;
import rltoys.math.vector.implementations.SVector;
import rltoys.math.vector.implementations.Vectors;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

/**
 * Accumulating traces
 */
public class ATraces implements Traces {
  private static final long serialVersionUID = 6241887723527497111L;
  public static final double DefaultZeroValue = 1e-8;
  public static final SVector DefaultPrototype = new SVector(0);
  protected double epsilon;
  protected final MutableVector prototype;
  @Monitor
  protected final MutableVector vector;

  public ATraces() {
    this(0);
  }

  public ATraces(MutableVector prototype) {
    this(0, DefaultZeroValue, prototype);
  }

  public ATraces(int size) {
    this(size, DefaultZeroValue, DefaultPrototype);
  }

  public ATraces(int size, double epsilon, MutableVector prototype) {
    this.epsilon = epsilon;
    this.prototype = prototype;
    vector = size > 0 ? prototype.newInstance(size) : null;
  }

  @Override
  public ATraces newTraces(int size) {
    return new ATraces(size, epsilon, prototype);
  }

  @Override
  public Traces update(double lambda, RealVector phi) {
    return update(lambda, phi, 1.0);
  }

  @Override
  public Traces update(double lambda, RealVector phi, double rho) {
    vector.mapMultiplyToSelf(lambda);
    addToSelf(phi);
    if (!(vector instanceof DenseVector))
      clearBelowThreshold();
    if (rho != 1.0)
      vector.mapMultiplyToSelf(rho);
    return this;
  }

  protected void addToSelf(RealVector phi) {
    vector.addToSelf(phi);
  }

  protected void clearBelowThreshold() {
    for (Iterator<VectorEntry> iterator = vector.iterator(); iterator.hasNext();) {
      double value = iterator.next().value();
      if (Math.abs(value) < epsilon)
        iterator.remove();
    }
  }

  @Override
  public RealVector vect() {
    return vector;
  }

  @Override
  public void clear() {
    Vectors.clear(vector);
  }
}
