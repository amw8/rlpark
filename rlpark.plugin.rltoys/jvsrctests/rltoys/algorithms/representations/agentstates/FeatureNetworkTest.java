package rltoys.algorithms.representations.agentstates;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import rltoys.algorithms.representations.features.Feature;
import rltoys.algorithms.representations.features.Identity;
import rltoys.algorithms.representations.featuresnetwork.FeatureNetwork;
import rltoys.math.representations.Function;


public class FeatureNetworkTest {
  static class NbUpdateFeature implements Feature {
    private static final long serialVersionUID = -4871363650322688572L;
    int nbUpdate = 0;

    @Override
    public List<Function> dependencies() {
      return null;
    }

    @Override
    public void update() {
      nbUpdate++;
    }

    @Override
    public double value() {
      return nbUpdate;
    }

    @Override
    public String toString() {
      return String.format("NbUp(%d)", nbUpdate);
    }
  }


  @Test
  public void featureNetworkTest() {
    Feature nbUpdateFeature = new NbUpdateFeature();
    Feature identity01 = new Identity(nbUpdateFeature);
    Feature identity02 = new Identity(nbUpdateFeature);
    FeatureNetwork featureNetwork = new FeatureNetwork(identity01, identity02);

    featureNetwork.update();
    Assert.assertEquals(1, (int) identity01.value());
    Assert.assertEquals(1, (int) identity02.value());

    featureNetwork.update();
    Assert.assertEquals(2, (int) identity01.value());
    Assert.assertEquals(2, (int) identity02.value());
  }
}
