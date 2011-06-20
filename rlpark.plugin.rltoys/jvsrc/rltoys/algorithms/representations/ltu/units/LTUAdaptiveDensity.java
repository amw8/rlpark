package rltoys.algorithms.representations.ltu.units;

import java.util.Random;

import rltoys.math.vector.BinaryVector;

public interface LTUAdaptiveDensity extends LTU {
  void increaseDensity(Random random, BinaryVector obs);

  void decreaseDensity(Random random, BinaryVector obs);
}
