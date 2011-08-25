package rltoys.algorithms.representations.discretizer.avebins;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import rltoys.math.normalization.IncMeanVarNormalizer;
import rltoys.math.vector.implementations.BVector;
import rltoys.math.vector.implementations.Vectors;

public class AveBinsTreeTest {
  @Test
  public void testAveBinsTree() {
    final int nbBins = 8;
    AveBinsTree tree = new AveBinsTree(new IncMeanVarNormalizer(), nbBins);
    Random random = new Random(0);
    for (int i = 0; i < 10000; i++)
      tree.toBinary(random.nextFloat() * nbBins);
    for (int i = 0; i < nbBins; i++) {
      final int[] expected = new int[] { i, 8 + i / 2, 12 + i / 4 };
      Assert.assertTrue(Vectors.equals(BVector.toBVector(14, expected), tree.toBinary(i)));
    }
  }
}
