package rltoys.algorithms.representations.features;

import java.util.Random;

public class BernoulliRandom extends DistributionRandom {

  private static final long serialVersionUID = 8105337240901039320L;
  private final double p;

  public static class Factory extends DistributionRandom.Factory {
    private final double p;

    Factory(double p) {
      this.p = p;
    }

    public Factory() {
      this(0.5);
    }

    @Override
    public DistributionRandom createFactory(Random random) {
      return new BernoulliRandom(random, p);
    }

    @Override
    public DistributionRandom[] createFactoryArray(int nbInputs) {
      return new BernoulliRandom[nbInputs];
    }
  }

  public BernoulliRandom(Random random, double p) {
    super(random);
    this.p = p;
  }

  @Override
  public void update() {
    value = random.nextDouble() < p ? 1.0 : 0.0;
  }

  @Override
  public String toString() {
    return "BerRand";
  }
}
