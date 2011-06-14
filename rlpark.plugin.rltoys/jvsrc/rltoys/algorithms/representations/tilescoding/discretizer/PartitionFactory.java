package rltoys.algorithms.representations.tilescoding.discretizer;

import java.util.Random;

import rltoys.math.ranges.Range;

public class PartitionFactory implements DiscretizerFactory {
  private static final long serialVersionUID = 3356344048646899647L;
  private final Range[] ranges;
  private double randomShiftRatio = Double.NaN;
  private Random random;

  public PartitionFactory(Range... ranges) {
    this.ranges = ranges;
  }

  public PartitionFactory(double min, double max, int inputSize) {
    this(getRanges(min, max, inputSize));
  }

  public void setRandom(Random random, double randomShiftRatio) {
    this.random = random;
    this.randomShiftRatio = randomShiftRatio;
  }

  @Override
  public Discretizer createDiscretizer(int inputIndex, int resolution, int tilingIndex, int nbTilings) {
    Range range = ranges[inputIndex];
    double offset = range.length() / resolution / nbTilings;
    double shift = computeShift(offset, tilingIndex, inputIndex);
    return new Partition(range.min() + shift, range.max() + shift, resolution);
  }

  public static Range[] getRanges(double min, double max, int stateSize) {
    Range[] ranges = new Range[stateSize];
    for (int i = 0; i < ranges.length; i++)
      ranges[i] = new Range(min, max);
    return ranges;
  }

  private double computeShift(double offset, int tilingIndex, int inputIndex) {
    double result = tilingIndex * offset;
    if (random != null)
      return result - random.nextFloat() * offset * randomShiftRatio / 2.0;
    return result;
  }
}
