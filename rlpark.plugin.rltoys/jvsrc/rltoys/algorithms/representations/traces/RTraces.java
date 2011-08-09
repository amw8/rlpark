package rltoys.algorithms.representations.traces;

import rltoys.math.vector.BinaryVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.SVector;


/**
 * Replacing traces for binary vectors
 */
public class RTraces extends ATraces {
  private static final long serialVersionUID = -324210619484987917L;

  public RTraces() {
    this(0);
  }

  public RTraces(int size) {
    this(size, DefaultZeroValue);
  }

  public RTraces(int size, double epsilon) {
    super(size, epsilon, new SVector(0));
  }

  @Override
  public RTraces newTraces(int size) {
    return new RTraces(size, epsilon);
  }

  @Override
  protected void addToSelf(RealVector phi) {
    for (int i : ((BinaryVector) phi).activeIndexes())
      vector.setEntry(i, 1);
  }
}
