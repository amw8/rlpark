package rltoys.algorithms.representations.tilescoding;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import rltoys.algorithms.representations.discretizer.partitions.PartitionFactory;
import rltoys.algorithms.representations.tilescoding.TileCodersNoHashingTest.TileCodersFactory;
import rltoys.algorithms.representations.tilescoding.hashing.ColisionDetection;
import rltoys.algorithms.representations.tilescoding.hashing.JavaHashing;
import rltoys.algorithms.representations.tilescoding.hashing.MurmurHashing;
import rltoys.algorithms.representations.tilescoding.hashing.UNH;
import rltoys.math.ranges.Range;

public class TileCodersHashingTest {
  static int memorySize = 100000;

  @Test
  public void testMemorySize() {
    TileCoders tileCoders = new TileCodersHashing(new UNH(new Random(0), memorySize), new Range(0, 1), new Range(0, 1));
    tileCoders.addFullTilings(2, 1);
    Assert.assertEquals(memorySize, tileCoders.vectorSize());
    tileCoders.addFullTilings(2, 1);
    Assert.assertEquals(memorySize, tileCoders.vectorSize());
  }

  @Test
  public void testUNHHashingActivationFrequency() {
    TileCodersNoHashingTest.checkFeatureActivationFrequency(new TileCodersFactory() {
      @Override
      public TileCoders create(int nbInputs, double min, double max) {
        TileCoders coders = new TileCodersHashing(new UNH(new Random(0), memorySize), nbInputs, 0, 1);
        return coders;
      }
    });
  }

  @Test
  public void testUNHHashingActivationFrequencyWithRandom() {
    TileCodersNoHashingTest.checkFeatureActivationFrequency(new TileCodersFactory() {
      @Override
      public TileCoders create(int nbInputs, double min, double max) {
        PartitionFactory discretizerFactory = new PartitionFactory(0, 1, nbInputs);
        discretizerFactory.setRandom(new Random(0), 0.1);
        TileCoders coders = new TileCodersHashing(new UNH(new Random(0), memorySize), discretizerFactory, nbInputs);
        return coders;
      }
    });
  }

  @Test
  public void testCollisionCounting() {
    final ColisionDetection hashing = new ColisionDetection(new UNH(new Random(0), 2));
    TileCoders tileCoders = new TileCodersHashing(hashing, 2, 0, 1);
    tileCoders.addFullTilings(2, 1);
    int nbSamples = 10000;
    Random random = new Random(0);
    for (int i = 0; i < nbSamples; i++)
      tileCoders.project(new double[] { random.nextDouble(), random.nextDouble() });
    Assert.assertEquals((double) nbSamples / 2, hashing.nbCollisions(), 1000);
  }

  @Test
  public void testUNHWithCollisionHashingActivationFrequencyWithRandom() {
    final ColisionDetection hashing = new ColisionDetection(new UNH(new Random(0), memorySize));
    int missingTiles = TileCodersNoHashingTest.checkFeatureActivationFrequency(new TileCodersFactory() {
      @Override
      public TileCoders create(int nbInputs, double min, double max) {
        PartitionFactory discretizerFactory = new PartitionFactory(0, 1, nbInputs);
        discretizerFactory.setRandom(new Random(0), 0.1);
        TileCoders coders = new TileCodersHashing(hashing, discretizerFactory, nbInputs);
        return coders;
      }
    });
    Assert.assertTrue(hashing.nbCollisions() >= missingTiles);
  }

  @Test
  public void testJavaHashingWithCollisionHashingActivationFrequency() {
    final ColisionDetection hashing = new ColisionDetection(new JavaHashing(memorySize));
    int missingTiles = TileCodersNoHashingTest.checkFeatureActivationFrequency(new TileCodersFactory() {
      @Override
      public TileCoders create(int nbInputs, double min, double max) {
        return new TileCodersHashing(hashing, nbInputs, 0, 1);
      }
    });
    Assert.assertTrue(hashing.nbCollisions() >= missingTiles);
  }

  @Test
  public void testMurmurHashingWithCollisionHashingActivationFrequency() {
    final ColisionDetection hashing = new ColisionDetection(new MurmurHashing(new Random(0), memorySize));
    int missingTiles = TileCodersNoHashingTest.checkFeatureActivationFrequency(new TileCodersFactory() {
      @Override
      public TileCoders create(int nbInputs, double min, double max) {
        return new TileCodersHashing(hashing, nbInputs, 0, 1);
      }
    });
    Assert.assertTrue(hashing.nbCollisions() >= missingTiles);
  }
}
