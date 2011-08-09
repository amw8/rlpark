package rltoys.algorithms.representations.traces;

import java.util.Iterator;

import rltoys.math.vector.RealVector;
import rltoys.math.vector.SVector;
import rltoys.math.vector.SparseVector;

/**
 * Accumulating traces with a close to constant number of active features
 */
public class NATraces extends SVector implements Traces {
  private static final long serialVersionUID = -5088638081927847850L;
  protected final int nbActiveFeatures;

  public NATraces(int nbActiveFeatures) {
    this(nbActiveFeatures, 0);
  }

  public NATraces(int nbActiveFeatures, int size) {
    super(size);
    this.nbActiveFeatures = nbActiveFeatures;
  }

  @Override
  public NATraces newTraces(int size) {
    return new NATraces(nbActiveFeatures, size);
  }

  @Override
  public Traces update(double lambda, RealVector phi) {
    return update(lambda, phi, 1.0);
  }

  private double findMin() {
    double min = Double.MAX_VALUE;
    for (double value : values.values())
      min = Math.min(min, Math.abs(value));
    return min;
  }

  @Override
  public Traces update(double lambda, RealVector phi, double rho) {
    mapMultiplyToSelf(lambda);
    addToSelf(phi);
    checkSize(lambda, phi);
    if (rho != 1.0)
      mapMultiplyToSelf(rho);
    return this;
  }

  private void clearBelowThreshold(double epsilon) {
    for (Iterator<Double> ite = values.values().iterator(); ite.hasNext();) {
      double value = ite.next();
      if (Math.abs(value) < epsilon)
        ite.remove();
    }
  }

  protected void checkSize(double lambda, RealVector phi) {
    int additionalElements = nonZeroElements() - nbActiveFeatures;
    if (additionalElements <= 0)
      return;
    double min = findMin();
    double timeShift = (double) additionalElements / ((SparseVector) phi).nonZeroElements();
    clearBelowThreshold(min * 1.0 / Math.pow(lambda - 1e-8, timeShift));
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
