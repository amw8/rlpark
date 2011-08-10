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
  public static final double DefaultZeroValue = 1e-8;
  public static final SVector DefaultPrototype = new SVector(0);
  protected double epsilon;
  protected final MutableVector prototype;
  @Monitor
  protected final MutableVector vector;
  private int lastActiveElements;
  private int nbAcceptedElements;

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
    if (clearRequired(phi, lambda))
      clearBelowThreshold();
    if (rho != 1.0)
      vector.mapMultiplyToSelf(rho);
    return this;
  }

  private boolean clearRequired(RealVector phi, double lambda) {
    if (phi instanceof DenseVector)
      return false;
    if (vector instanceof DenseVector)
      return false;
    SparseVector sparsePhi = (SparseVector) phi;
    int nbAcceptedElements = updateAcceptedElements(sparsePhi.nonZeroElements(), lambda);
    return ((SparseVector) vector).nonZeroElements() >= nbAcceptedElements;
  }

  private int updateAcceptedElements(int activeElements, double lambda) {
    if (lastActiveElements == activeElements)
      return nbAcceptedElements;
    lastActiveElements = activeElements;
    nbAcceptedElements = (int) (Math.log(epsilon) / Math.log(lambda)) * activeElements;
    return nbAcceptedElements;
  }

  protected void addToSelf(RealVector phi) {
    vector.addToSelf(phi);
  }

  protected void clearBelowThreshold() {
    SVector svector = (SVector) vector;
    double[] values = svector.values();
    int i = 0;
    while (i < svector.nonZeroElements()) {
      if (values[i] <= epsilon)
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
