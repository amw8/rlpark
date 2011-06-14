package rltoys.algorithms.representations.features;

import java.util.List;
import java.util.Random;

import rltoys.math.representations.Function;

public abstract class DistributionRandom implements Feature {

  private static final long serialVersionUID = -2868322596947256891L;

  public static abstract class Factory {
    public abstract DistributionRandom[] createFactoryArray(int nbInputs);

    public abstract DistributionRandom createFactory(Random random);
  }

  protected final Random random;
  protected Double value;

  public DistributionRandom(Random random) {
    assert random != null;
    this.random = random;
  }

  @Override
  public double value() {
    return value;
  }

  @Override
  public List<Function> dependencies() {
    return null;
  }

  @Override
  public abstract void update();
}
