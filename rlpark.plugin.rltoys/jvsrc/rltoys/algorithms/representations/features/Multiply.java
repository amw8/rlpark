package rltoys.algorithms.representations.features;

import java.util.List;

import rltoys.math.representations.Function;
import rltoys.utils.Utils;
import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.labels.Labels;


public class Multiply implements Feature, Labeled {

  private static final long serialVersionUID = 8177294912285841100L;
  private final Function f2;
  private final Function f1;
  private double value;

  public Multiply(Function f1, Function f2) {
    this.f1 = f1;
    this.f2 = f2;
  }

  @Override
  public List<Function> dependencies() {
    return Utils.asList(f1, f2);
  }

  @Override
  public void update() {
    value = f1.value() * f2.value();
    assert Utils.checkValue(value);
  }

  @Override
  public double value() {
    return value;
  }

  @Override
  public String toString() {
    return String.format("(%s*%s)", String.valueOf(f1), String.valueOf(f2));
  }

  @Override
  public String label() {
    return String.format("(%s*%s)", Labels.label(f1), Labels.label(f2));
  }
}
