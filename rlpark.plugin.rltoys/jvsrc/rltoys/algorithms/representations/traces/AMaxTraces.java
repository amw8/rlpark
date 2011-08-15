package rltoys.algorithms.representations.traces;

import rltoys.math.vector.MutableVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.SparseVector;
import rltoys.math.vector.VectorEntry;
import rltoys.math.vector.implementations.SVector;

/**
 * Accumulating traces with an absolute value on each element
 */
public class AMaxTraces extends ATraces {
  private static final long serialVersionUID = 8063854269195146376L;
  final private double maximumValue;

  public AMaxTraces() {
    this(1);
  }

  public AMaxTraces(double maximumValue) {
    this(maximumValue, DefaultPrototype);
  }

  public AMaxTraces(double maximumValue, MutableVector prototype) {
    this(maximumValue, DefaultPrototype, -1, 0);
  }

  public AMaxTraces(double maximumValue, MutableVector prototype, int targetSize, double targetTolerance) {
    this(0, targetSize, targetTolerance, maximumValue, DefaultPrototype);
    assert prototype instanceof SparseVector;
  }

  protected AMaxTraces(int size, int targetSize, double targetTolerance, double maximumValue, MutableVector prototype) {
    super(size, targetSize, targetTolerance, prototype);
    this.maximumValue = maximumValue;
  }

  @Override
  public AMaxTraces newTraces(int size) {
    return new AMaxTraces(size, targetSize, targetTolerance, maximumValue, prototype);
  }

  private void addToSelfAsSVector(RealVector phi) {
    SVector svector = (SVector) vector;
    int thisPosition = 0;
    for (VectorEntry entry : phi) {
      int otherIndex = entry.index();
      int search = svector.searchFrom(thisPosition, otherIndex);
      int position = search;
      if (position < 0) {
        position = SVector.notFoundToPosition(search);
        svector.insertElementAtPosition(position, otherIndex, adjustValue(entry.value()));
      } else
        svector.values[position] = adjustValue(entry.value() + svector.values[position]);
      thisPosition = position + 1;
    }
  }

  @Override
  protected void addToSelf(RealVector phi) {
    if (phi instanceof SVector) {
      addToSelfAsSVector(phi);
      return;
    }
    for (VectorEntry entry : phi) {
      int index = entry.index();
      double value = adjustValue(vector.getEntry(index) + entry.value());
      vector.setEntry(index, value);
    }
  }

  private double adjustValue(double value) {
    if (Math.abs(value) <= maximumValue)
      return value;
    return Math.signum(value) * maximumValue;
  }
}
