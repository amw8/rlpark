package rltoys.algorithms.representations.features;

import java.util.List;

import rltoys.math.normalization.Normalizer;
import rltoys.math.representations.Function;
import rltoys.utils.Utils;
import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.labels.Labels;


public class Normalize implements Feature, Labeled {

  private static final long serialVersionUID = 5249703664822006090L;
  protected final Normalizer normalizer;
  private final Function function;
  private double value;

  public Normalize(Normalizer normalizer, Function function) {
    assert function != null;
    this.function = function;
    this.normalizer = normalizer.newInstance();
  }

  @Override
  public double value() {
    return value;
  }

  @Override
  public void update() {
    double functionValue = function.value();
    normalizer.update(functionValue);
    value = normalizer.normalize(functionValue);
  }

  @Override
  public String toString() {
    return String.format("Norm(%s)[%s]", String.valueOf(function), String.valueOf(value()));
  }

  @Override
  public List<Function> dependencies() {
    return Utils.asList(function);
  }

  public Function function() {
    return function;
  }

  @Override
  public String label() {
    return String.format("Norm(%s)", Labels.label(function));
  }
}
