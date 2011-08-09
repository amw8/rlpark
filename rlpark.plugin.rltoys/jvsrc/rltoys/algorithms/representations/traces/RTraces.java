package rltoys.algorithms.representations.traces;

import java.util.Iterator;

import rltoys.math.vector.BinaryVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.SVector;
import rltoys.utils.NotImplemented;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

/**
 * Replacing traces for binary vectors
 */
@Monitor
public class RTraces implements Traces {
  private static final long serialVersionUID = -5822244731227112021L;
  protected double epsilon;
  protected SVector vector;
  int nbActive;

  public RTraces() {
    this(ATraces.DefaultZeroValue);
  }

  public RTraces(double epsilon) {
    this.epsilon = epsilon;
    vector = null;
  }

  public RTraces(int size, double epsilon) {
    this.epsilon = epsilon;
    vector = new SVector(size);
  }

  @Override
  public Traces newTraces(int size) {
    return new RTraces(size, epsilon);
  }

  @Override
  public Traces update(double lambda, RealVector phi) {
    return update(lambda, phi, 1.0);
  }

  protected void clearBelowThreshold() {
    for (Iterator<Double> iterator = vector.values.values().iterator(); iterator.hasNext();) {
      double value = iterator.next();
      if (Math.abs(value) < epsilon)
        iterator.remove();
    }
  }

  @Override
  public Traces update(double lambda, RealVector phi, double rho) {
    checkVector(phi);
    BinaryVector bphi = (BinaryVector) phi;
    vector.mapMultiplyToSelf(lambda);
    clearBelowThreshold();
    for (int i : bphi.activeIndexes())
      vector.setEntry(i, 1.0);
    if (rho != 1.0)
      vector.mapMultiplyToSelf(rho);
    nbActive = vector.nonZeroElements();
    return this;
  }

  private void checkVector(RealVector phi) {
    if (!(phi instanceof BinaryVector))
      throw new NotImplemented();
  }

  @Override
  public void clear() {
    vector.set(0);
  }

  @Override
  public RealVector vect() {
    return vector;
  }

}
