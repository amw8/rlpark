package rltoys.algorithms.representations.features;

import java.util.Random;

public class NormalRandom extends DistributionRandom {

  private static final long serialVersionUID = -2378321288359373116L;
  private final double mean;
  private final double stdDev;

  public static class Factory extends DistributionRandom.Factory {

    private final double mean;
    private final double stdDev;

    public Factory(double mean, double stdDev) {
      this.mean = mean;
      this.stdDev = stdDev;
    }

    public Factory() {
      this(0.0, 1.0);
    }

    @Override
    public DistributionRandom createFactory(Random random) {
      return new NormalRandom(random, mean, stdDev);
    }

    @Override
    public DistributionRandom[] createFactoryArray(int nbInputs) {
      return new NormalRandom[nbInputs];
    }
  }

  public NormalRandom(Random random, double mean, double stdDev) {
    super(random);
    this.mean = mean;
    this.stdDev = stdDev;
  }

  @Override
  public void update() {
    value = random.nextGaussian() * stdDev + mean;
  }

  @Override
  public String toString() {
    return "NRand";
  }
}
