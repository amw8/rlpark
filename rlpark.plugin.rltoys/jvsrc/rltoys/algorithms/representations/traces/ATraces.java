package rltoys.algorithms.representations.traces;

import rltoys.math.vector.DenseVector;
import rltoys.math.vector.MutableVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.SparseVector;
import rltoys.math.vector.implementations.SVector;
import rltoys.math.vector.implementations.Vectors;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

/**
 * Accumulating traces
 */
public class ATraces implements Traces {
  private static final long serialVersionUID = 6241887723527497111L;
  public static final SVector DefaultPrototype = new SVector(0);
  @Monitor
  protected double threshold = 1e-8;
  protected final MutableVector prototype;
  @Monitor
  protected final MutableVector vector;
  protected final int targetSize;
  protected final double targetTolerance;
  @Monitor
  private double minimumValue;
  @Monitor
  private final int targetMin;
  @Monitor
  private final int targetMax;

  public ATraces() {
    this(DefaultPrototype);
  }

  public ATraces(MutableVector prototype) {
    this(0, -1, 0, DefaultPrototype);
  }

  public ATraces(int targetSize, double targetTolerance) {
    this(DefaultPrototype, targetSize, targetTolerance);
  }

  public ATraces(MutableVector prototype, int targetSize, double targetTolerance) {
    this(0, targetSize, targetTolerance, DefaultPrototype);
    assert prototype instanceof SparseVector;
  }

  protected ATraces(int size, int targetSize, double targetTolerance, MutableVector prototype) {
    this.prototype = prototype;
    this.targetSize = targetSize;
    this.targetTolerance = targetTolerance;
    vector = size > 0 ? prototype.newInstance(size) : null;
    targetMin = targetSize > 0 ? (int) (targetSize - targetSize * targetTolerance) : -1;
    targetMax = targetSize > 0 ? (int) (targetSize + targetSize * targetTolerance) : -1;
  }

  @Override
  public ATraces newTraces(int size) {
    return new ATraces(size, targetSize, targetTolerance, prototype);
  }

  @Override
  public void update(double lambda, RealVector phi) {
    update(lambda, phi, 1.0);
  }

  @Override
  public void update(double lambda, RealVector phi, double rho) {
    vector.mapMultiplyToSelf(lambda);
    addToSelf(phi);
    if (clearRequired(phi, lambda)) {
      clearBelowThreshold();
      adjustThreshold(lambda);
    }
    if (rho != 1.0)
      vector.mapMultiplyToSelf(rho);
  }

  private void adjustThreshold(double lambda) {
    SparseVector sparseVector = (SparseVector) vector;
    if (sparseVector.nonZeroElements() > targetMax) {
      threshold = minimumValue / (lambda * Math.exp(.3 * Math.log(lambda)));
    }
    if (sparseVector.nonZeroElements() < targetMin) {
      threshold = minimumValue * (lambda * Math.exp(.3 * Math.log(lambda)));
    }
  }

  private boolean clearRequired(RealVector phi, double lambda) {
    if (threshold == 0 || targetSize <= 0)
      return false;
    if (vector instanceof DenseVector)
      return false;
    return true;
  }

  protected void addToSelf(RealVector phi) {
    vector.addToSelf(phi);
  }

  protected void clearBelowThreshold() {
    SVector svector = (SVector) vector;
    double[] values = svector.values;
    int i = 0;
    minimumValue = Double.MAX_VALUE;
    while (i < svector.nonZeroElements()) {
      final double absValue = Math.abs(values[i]);
      minimumValue = Math.min(minimumValue, absValue);
      if (absValue <= threshold)
        svector.removeExistingEntry(i);
      else
        i++;
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
