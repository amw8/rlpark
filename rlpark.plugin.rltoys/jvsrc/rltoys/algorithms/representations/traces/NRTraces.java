package rltoys.algorithms.representations.traces;

import java.util.Iterator;

import rltoys.math.vector.BinaryVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.SVector;
import rltoys.math.vector.SparseVector;
import rltoys.utils.NotImplemented;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class NRTraces implements Traces {
  private static final long serialVersionUID = 5864334927346220904L;
  protected final int nbActiveFeatures;
  protected SVector vector;
  @Monitor
  int nbActive;

  public NRTraces(int nbActiveFeatures) {
    this.nbActiveFeatures = nbActiveFeatures;
    vector = null;
  }

  public NRTraces(int size, int nbActiveFeatures) {
    this.nbActiveFeatures = nbActiveFeatures;
    vector = new SVector(size);
  }

  @Override
  public Traces newTraces(int size) {
    return new RTraces(size, nbActiveFeatures);
  }

  @Override
  public Traces update(double lambda, RealVector phi) {
    return update(lambda, phi, 1.0);
  }

  protected void clearBelowThreshold(double epsilon) {
    for (Iterator<Double> iterator = vector.values.values().iterator(); iterator.hasNext();) {
      double value = iterator.next();
      if (Math.abs(value) < epsilon)
        iterator.remove();
    }
  }

  private double findMin() {
    double min = Double.MAX_VALUE;
    for (double value : vector.values.values())
      min = Math.min(min, Math.abs(value));
    return min;
  }

  protected void checkSize(double lambda, RealVector phi) {
    int additionalElements = vector.nonZeroElements() - nbActiveFeatures;
    if (additionalElements <= 0)
      return;
    double min = findMin();
    double timeShift = (double) additionalElements / ((SparseVector) phi).nonZeroElements();
    clearBelowThreshold(min * 1.0 / Math.pow(lambda - 1e-8, timeShift));
  }

  @Override
  public Traces update(double lambda, RealVector phi, double rho) {
    checkVector(phi);
    BinaryVector bphi = (BinaryVector) phi;
    vector.mapMultiplyToSelf(lambda);
    checkSize(lambda, phi);
    for (Integer i : bphi)
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
