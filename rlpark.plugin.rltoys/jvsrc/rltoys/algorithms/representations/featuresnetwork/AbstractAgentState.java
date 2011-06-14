package rltoys.algorithms.representations.featuresnetwork;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import rltoys.algorithms.representations.features.Constant;
import rltoys.algorithms.representations.features.Feature;
import rltoys.algorithms.representations.features.Functions;
import rltoys.algorithms.representations.features.Normalize;
import rltoys.math.normalization.Normalizer;
import rltoys.math.representations.Function;
import rltoys.math.vector.CachedVector;
import rltoys.math.vector.PVector;
import zephyr.plugin.core.api.labels.Labels;


public abstract class AbstractAgentState extends AbstractFeatureNetwork {
  private static final long serialVersionUID = 5500998189323677374L;
  protected transient final CachedVector stateVector = new CachedVector();
  private final List<Feature> state = new ArrayList<Feature>();
  private List<Feature> normalizers = null;
  private Constant bias = null;
  private Normalizer normalizerPrototype;

  public void normalizeState(Normalizer normalizer) {
    normalizers = new ArrayList<Feature>();
    normalizerPrototype = normalizer;
    addBias();
  }

  public void addBias() {
    assert state.isEmpty();
    bias = new Constant(1.0);
    addStateFeature(bias);
    if (isStateNormalized())
      normalizers.set(0, bias);
  }

  private boolean isStateNormalized() {
    return normalizers != null;
  }

  public void addStateFeature(Feature feature) {
    assert state.size() == features().size();
    state.add(null);
    features().add(null);
    if (isStateNormalized())
      normalizers.add(null);
    setStateFeature(state.size() - 1, feature);
  }

  public void setStateFeature(int index, Feature feature) {
    assert index >= 0 && index < state.size();
    state.set(index, feature);
    set(index, feature);
    if (isStateNormalized())
      normalizers.set(index, new Normalize(normalizerPrototype, feature));
  }

  protected void update() {
    assert state.size() > 0;
    assert !isStateNormalized() || normalizers.size() == stateSize();
    updateFeatures();
  }

  @Override
  protected void featuresUpdated() {
    if (!isStateNormalized())
      stateVector.set(state);
    else {
      updateNormalizers();
      stateVector.set(normalizers);
    }
  }

  private void updateNormalizers() {
    for (Feature normalizer : normalizers)
      normalizer.update();
  }

  public int stateSize() {
    return state.size();
  }

  public List<Feature> getStateFeatures() {
    if (bias == null)
      return new ArrayList<Feature>(state);
    LinkedList<Feature> stateFeatures = new LinkedList<Feature>(state);
    Feature removedFeature = stateFeatures.removeFirst();
    assert removedFeature == bias;
    return stateFeatures;
  }

  public PVector currentState() {
    assert state.size() > 0;
    return stateVector.values();
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (Function function : state)
      result.append(String.format("%s: %f\n", Labels.label(function), function.value()));
    return String.format("State: \n%s", result.toString());
  }


  public List<String> getLabels() {
    if (normalizers != null)
      return Functions.getLabels(normalizers);
    return Functions.getLabels(state);
  }

  public Feature feature(int featureIndex) {
    if (normalizers != null)
      return normalizers.get(featureIndex);
    return state.get(featureIndex);
  }
}
