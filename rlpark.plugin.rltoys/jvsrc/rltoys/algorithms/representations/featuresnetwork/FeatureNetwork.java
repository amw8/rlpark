package rltoys.algorithms.representations.featuresnetwork;

import rltoys.algorithms.representations.features.Feature;

public class FeatureNetwork extends AbstractFeatureNetwork {
  private static final long serialVersionUID = 6188206563006459851L;

  public FeatureNetwork() {
  }

  public FeatureNetwork(Feature... features) {
    for (Feature feature : features)
      add(feature);
  }

  public void update() {
    updateFeatures();
  }
}
