package rltoys.algorithms.representations.states;


import org.junit.Assert;
import org.junit.Test;

import rltoys.algorithms.representations.features.Feature;
import rltoys.algorithms.representations.features.Identity;
import rltoys.algorithms.representations.features.LinearCombination;
import rltoys.algorithms.representations.features.Positive;
import rltoys.algorithms.representations.featuresnetwork.InputFeatureNetwork;
import rltoys.math.representations.Function;
import rltoys.math.vector.PVector;


public class LinearCombinationTest {

  static final private double[] a = { 2.0, 3.0 };
  static final private double[] b = { 4.0, 5.0 };
  static private final double[] weights01 = { 1, 2 };
  static private final double[] weights02 = { 3, 4 };
  private final InputFeatureNetwork featureNetwork = new InputFeatureNetwork();
  private final Identity[] inputs = { featureNetwork.newInput(), featureNetwork.newInput() };

  @Test
  public void testInitialLinearCombination() {
    LinearCombination lf = new LinearCombination(inputs);
    featureNetwork.add(lf);
    featureNetwork.setInputAndUpdate(a);
    Assert.assertEquals(0, lf.value(), 0.0);
    featureNetwork.setInputAndUpdate(b);
    Assert.assertEquals(0, lf.value(), 0.0);
  }

  @Test
  public void testLinearCombination() {
    LinearCombination lf = new LinearCombination(new PVector(weights01, true), inputs);
    featureNetwork.add(lf);
    featureNetwork.setInputAndUpdate(a);
    Assert.assertEquals(8, lf.value(), 0.0);
    featureNetwork.setInputAndUpdate(b);
    Assert.assertEquals(14, lf.value(), 0.0);
  }

  @Test
  public void testLinearCombinationSetWeights() {
    LinearCombination lf = new LinearCombination(new PVector(weights01, true), inputs);
    featureNetwork.add(lf);
    featureNetwork.setInputAndUpdate(a);
    Assert.assertEquals(8, lf.value(), 0.0);
    lf.weights().set(new PVector(weights02));
    featureNetwork.setInputAndUpdate(a);
    Assert.assertEquals(18, lf.value(), 0.0);
  }

  @Test
  public void testLinearCombinationSetFeature() {
    LinearCombination lf = new LinearCombination(new PVector(weights01, true), inputs);
    featureNetwork.add(lf);
    featureNetwork.setInputAndUpdate(a);
    Assert.assertEquals(8, lf.value(), 0.0);

    LinearCombination lf2 = new LinearCombination(new PVector(weights02, true), inputs);
    lf.setFeature(0, 3, lf2);
    featureNetwork.resetStructure();
    featureNetwork.setInputAndUpdate(a);
    Assert.assertEquals(18 * 3 + 3.0 * 2.0, lf.value(), 0.0);
  }

  @Test
  public void testAbs() {
    Feature m = new LinearCombination(new PVector(1.0, -1.0), inputs[0], inputs[1]);
    Feature abs = createAbs(m);
    featureNetwork.add(abs);
    featureNetwork.setInputAndUpdate(2.0, 1.0);
    Assert.assertEquals(1.0, abs.value(), 0.0);
    featureNetwork.setInputAndUpdate(2.0, 4.0);
    Assert.assertEquals(2.0, abs.value(), 0.0);
  }

  private Feature createAbs(Function f) {
    Positive p = new Positive(f);
    Positive m = new Positive(new LinearCombination(new PVector(-1.0), f));
    return new LinearCombination(new PVector(1.0, 1.0), p, m);
  }
}
