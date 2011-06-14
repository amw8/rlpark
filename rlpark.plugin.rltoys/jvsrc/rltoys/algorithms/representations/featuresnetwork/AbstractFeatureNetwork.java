package rltoys.algorithms.representations.featuresnetwork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import rltoys.algorithms.representations.features.Feature;
import rltoys.math.representations.Function;
import zephyr.plugin.core.api.signals.Signal;

public abstract class AbstractFeatureNetwork implements Serializable {

  private static final long serialVersionUID = 4642271039243091677L;
  public final transient Signal<Integer> onUpdated = new Signal<Integer>();
  protected final List<Feature> updateOrder = new ArrayList<Feature>();
  protected int nbUpdate = 0;
  private final List<Feature> features = new ArrayList<Feature>();

  public void add(Feature feature) {
    features.add(null);
    set(features.size() - 1, feature);
  }

  public void set(int index, Feature feature) {
    assert feature != null;
    resetStructure();
    features.set(index, feature);
  }

  protected void updateFeatures() {
    if (updateOrder.isEmpty())
      computeUpdateOrder();
    for (Feature feature : new ArrayList<Feature>(updateOrder))
      feature.update();
    featuresUpdated();
    onUpdated.fire(nbUpdate);
    nbUpdate += 1;
  }

  protected void featuresUpdated() {
  }

  private void addChildren(List<Feature> dependentFeatures, Feature parent) {
    dependentFeatures.add(parent);
    List<Function> dependencies = parent.dependencies();
    if (dependencies == null)
      return;
    for (Function child : dependencies)
      if (child instanceof Feature)
        addChildren(dependentFeatures, (Feature) child);
  }

  protected void computeUpdateOrder() {
    assert updateOrder.isEmpty();
    List<Feature> dependentFeatures = new ArrayList<Feature>();
    for (Feature feature : features)
      addChildren(dependentFeatures, feature);
    Collections.reverse(dependentFeatures);
    Set<Feature> addedFeatures = new LinkedHashSet<Feature>();
    for (Feature feature : dependentFeatures) {
      if (addedFeatures.contains(feature))
        continue;
      updateOrder.add(feature);
      addedFeatures.add(feature);
    }
    orderUpdated();
  }

  protected void orderUpdated() {
  }

  public void resetStructure() {
    updateOrder.clear();
  }

  public List<Feature> features() {
    return features;
  }
}
