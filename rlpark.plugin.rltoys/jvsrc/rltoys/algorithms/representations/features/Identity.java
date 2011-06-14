package rltoys.algorithms.representations.features;

import java.util.List;

import rltoys.math.representations.Function;
import rltoys.utils.Utils;
import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.labels.Labels;

public class Identity implements Feature, Labeled {

  private static final long serialVersionUID = 2561637687812247824L;
  private double value;
  private final Function function;

  public Identity() {
    this(null);
  }

  public Identity(Function function) {
    this.function = function;
  }

  @Override
  public double value() {
    return value;
  }

  public void setValue(double value) {
    assert function == null;
    this.value = value;
  }

  @Override
  public List<Function> dependencies() {
    if (function == null)
      return null;
    return Utils.asList(function);
  }

  @Override
  public void update() {
    if (function != null)
      value = function.value();
  }

  @Override
  public String label() {
    if (function != null)
      return String.format("Id(%s)", Labels.label(function));
    return toString();
  }

  @Override
  public String toString() {
    String valueRepr;
    if (function != null)
      valueRepr = String.valueOf(function);
    else
      valueRepr = String.valueOf(value);
    return String.format("Id(%s)", valueRepr);
  }

  public Function function() {
    return function;
  }
}
