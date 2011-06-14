package rltoys.algorithms.representations.tilescoding.discretizer;

import rltoys.math.ranges.Range;

public class Partition extends Range implements Discretizer {
  private static final long serialVersionUID = -4881045704029128672L;
  public final int resolution;
  private final double partLength;

  public Partition(double min, double max, int resolution) {
    super(min, max);
    this.resolution = resolution;
    partLength = length() / resolution;
  }

  @Override
  public String toString() {
    return String.format("%f:%d:%f", min(), resolution, max());
  }

  @Override
  public int discretize(double input) {
    double n = (input - min()) / partLength;
    if (n < 0)
      n += ((int) (-n / resolution) + 1) * resolution;
    return (int) (n % resolution);
  }

  @Override
  public int resolution() {
    return resolution;
  }

  @Override
  public int hashCode() {
    return super.hashCode() + resolution;
  }

  @Override
  public boolean equals(Object object) {
    if (!super.equals(object))
      return false;
    return ((Partition) object).resolution == resolution;
  }
}
