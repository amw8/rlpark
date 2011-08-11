package rltoys.algorithms.representations.traces;

import rltoys.math.vector.MutableVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.VectorEntry;
import rltoys.math.vector.implementations.SVector;

/**
 * Accumulating traces with an absolute value on each element
 */
public class AMaxTraces extends ATraces {
  private static final long serialVersionUID = 8063854269195146376L;
  static final protected double DefaultMaxValue = 1.0;
  final private double maximumValue;

  public AMaxTraces() {
    this(0, DefaultZeroValue, DefaultMaxValue, DefaultPrototype);
  }

  public AMaxTraces(int size) {
    this(size, DefaultZeroValue, DefaultMaxValue, DefaultPrototype);
  }


  public AMaxTraces(double epsilon, double maximumValue) {
    this(0, epsilon, maximumValue, DefaultPrototype);
  }

  public AMaxTraces(int size, double epsilon, double maximumValue, MutableVector prototype) {
    super(size, epsilon, prototype);
    this.maximumValue = maximumValue;
  }

  public AMaxTraces(MutableVector prototype) {
    this(0, DefaultZeroValue, DefaultMaxValue, prototype);
  }

  @Override
  public AMaxTraces newTraces(int size) {
    return new AMaxTraces(size, epsilon, maximumValue, prototype);
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
