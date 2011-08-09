package rltoys.algorithms.representations.traces;

import rltoys.math.vector.MutableVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.VectorEntry;

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

  @Override
  protected void addToSelf(RealVector phi) {
    vector.addToSelf(phi);
    for (VectorEntry entry : vector) {
      final double value = entry.value();
      if (Math.abs(value) > maximumValue)
        vector.setEntry(entry.index(), Math.signum(value) * maximumValue);
    }
  }
}
