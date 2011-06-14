package rltoys.math.normalization;

import rltoys.math.Constants;
import rltoys.math.ranges.Range;
import rltoys.utils.Utils;

public class MinMaxNormalizer implements Normalizer {
  private static final long serialVersionUID = 4495161964136798707L;
  public final static double MIN = -1;
  public final static double MAX = 1;

  private double min = Double.MAX_VALUE;
  private double max = -Double.MAX_VALUE;
  private int nbUpdate = 0;
  private final Range range;

  public MinMaxNormalizer() {
    this(new Range(MIN, MAX));
  }

  public MinMaxNormalizer(Range range) {
    this.range = range;
  }

  @Override
  public double normalize(double x) {
    if (!Utils.checkValue(x) || !Utils.checkValue(min) || !Utils.checkValue(max))
      return Double.NaN;
    if (max - min == 0.0 || nbUpdate == 0)
      return 0;
    return (x - min) / (max - min + Constants.EPSILON) * range.length() + range.min();
  }

  public float normalize(float x) {
    return (float) normalize((double) x);
  }

  @Override
  public void update(double x) {
    min = Math.min(x, min);
    max = Math.max(x, max);
    nbUpdate++;
  }

  @Override
  public MinMaxNormalizer newInstance() {
    return new MinMaxNormalizer(range);
  }

  public void reset() {
    min = Double.MAX_VALUE;
    max = -Double.MAX_VALUE;
    nbUpdate = 0;
  }

  public Range range() {
    return range;
  }
}
