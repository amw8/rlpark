package rltoys.algorithms.representations.features;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rltoys.math.normalization.IncMeanVarNormalizer;

public class FeatureTest {
  private static final double EPSILON = 0.1;

  @Test
  public void testConstant() {
    Constant feature = new Constant(5.0);
    Assert.assertNull(feature.dependencies());
    Assert.assertEquals(5.0, feature.value(), 0.0);
    feature.update();
    Assert.assertEquals(5.0, feature.value(), 0.0);
    feature.setValue(7.0);
    Assert.assertEquals(7.0, feature.value(), 0.0);
  }

  @Test
  public void testIdentity() {
    Identity feature = new Identity();
    Assert.assertEquals(feature.value(), 0, 0);
    feature.setValue(5.0);
    Assert.assertEquals(feature.value(), 5.0, 0.0);
    feature.setValue(3.0);
    Assert.assertEquals(feature.value(), 3.0, 0.0);
    Assert.assertNull(feature.dependencies());
  }

  @Test
  public void testNormalize() {
    final double mean = 45.0;
    final double variance = 1.0;
    UniformRandom uniformRandom = new UniformRandom(new Random(0), mean, variance);
    IncMeanVarNormalizer normalizer = new IncMeanVarNormalizer();
    Normalize normalize01 = new Normalize(normalizer, uniformRandom);
    Normalize normalize02 = new Normalize(normalizer, normalize01);
    for (int i = 0; i < 100; i++) {
      uniformRandom.update();
      normalize01.update();
      normalize02.update();
    }
    Assert.assertEquals(mean, ((IncMeanVarNormalizer) normalize01.normalizer).mean(), EPSILON);
    Assert.assertTrue(((IncMeanVarNormalizer) normalize01.normalizer).variance(0) < 1.0);
    Assert.assertEquals(0.0, ((IncMeanVarNormalizer) normalize02.normalizer).mean(), EPSILON);
    Assert.assertEquals(1.0, ((IncMeanVarNormalizer) normalize02.normalizer).variance(1), EPSILON);
  }

  @Test
  public void testMultiply() {
    Identity feature01 = new Identity();
    Identity feature02 = new Identity();
    Multiply multiply = new Multiply(feature01, feature02);
    Assert.assertEquals(multiply.value(), 0, 0);
    feature01.setValue(5.0);
    feature02.setValue(3.0);
    multiply.update();
    Assert.assertEquals(multiply.value(), 15.0, 0.0);
  }

  @Test
  public void testPositive() {
    Identity feature = new Identity();
    Positive positive = new Positive(feature);
    Assert.assertEquals(positive.value(), 0.0, 0);
    feature.setValue(5.0);
    positive.update();
    Assert.assertEquals(positive.value(), 5.0, 0.0);
    feature.setValue(-5.0);
    positive.update();
    Assert.assertEquals(positive.value(), 0.0, 0.0);
    feature.setValue(0.0);
    positive.update();
    Assert.assertEquals(positive.value(), 0.0, 0.0);
  }

  @Test
  public void testNegative() {
    Identity feature = new Identity();
    Positive positive = new Positive(feature, true);
    Assert.assertEquals(positive.value(), 0.0, 0);
    feature.setValue(5.0);
    positive.update();
    Assert.assertEquals(positive.value(), 0.0, 0.0);
    feature.setValue(-5.0);
    positive.update();
    Assert.assertEquals(positive.value(), 5.0, 0.0);
    feature.setValue(0.0);
    positive.update();
    Assert.assertEquals(positive.value(), 0.0, 0.0);
  }

  @Test
  public void testTrackingHistory() {
    Identity feature = new Identity();
    TrackingHistory history = new TrackingHistory(0.2, feature);
    feature.setValue(1.0);
    history.update();
    Assert.assertEquals(0.8, history.value(), 0.0);
    feature.setValue(35.0);
    history.update();
    Assert.assertEquals(0.8 * 0.2 + 0.8 * 35, history.value(), 0.0);
  }

  @Test
  public void testPeriodicFeature() {
    final int period = 1000;
    Periodic feature = new Periodic(period);
    for (int i = 0; i < period * 2; i++) {
      feature.update();
      if (i % (period / 2) == 0)
        Assert.assertEquals(feature.value(), 0.0, 1e-6);
      else
        Assert.assertTrue(Math.abs(feature.value()) > 1e-6);
    }
  }

}
