package rltoys.algorithms.representations.features.envio;

import java.util.List;

import rltoys.algorithms.representations.features.Feature;
import rltoys.environments.envio.observations.TStep;
import rltoys.math.representations.Function;
import zephyr.plugin.core.api.labels.Labeled;

public abstract class StepFeature implements Feature, Labeled {

  private static final long serialVersionUID = 4835264228337027430L;
  private double currentValue;
  private final String label;

  public StepFeature(String label) {
    this.label = label;
  }

  abstract protected double computeValue(TStep step);

  public void update(TStep step) {
    assert step != null;
    currentValue = computeValue(step);
  }

  @Override
  public double value() {
    return currentValue;
  }

  @Override
  public void update() {
  }

  @Override
  public List<Function> dependencies() {
    return null;
  }

  @Override
  public String toString() {
    return String.format("%s[%s]", label, String.valueOf(value()));
  }

  @Override
  public String label() {
    return label;
  }
}
