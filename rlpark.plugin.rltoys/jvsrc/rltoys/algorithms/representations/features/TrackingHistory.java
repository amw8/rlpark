package rltoys.algorithms.representations.features;

import java.util.List;

import rltoys.math.representations.Function;
import rltoys.utils.Utils;
import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.labels.Labels;


public class TrackingHistory implements Feature, Labeled {

  private static final long serialVersionUID = -1403834964852295749L;
  private final Function function;
  private double value = 0.0;
  private final double alpha;

  public TrackingHistory(double alpha, Function function) {
    this.function = function;
    this.alpha = alpha;
  }

  @Override
  public List<Function> dependencies() {
    return Utils.asList(function);
  }

  @Override
  public void update() {
    value = alpha * value + (1.0 - alpha) * function.value();
  }

  @Override
  public double value() {
    return value;
  }

  @Override
  public String toString() {
    return String.format("TH(%s=%f[%.2f])", String.valueOf(function), value, alpha);
  }

  @Override
  public String label() {
    return String.format("TH[%.2f](%s)", alpha, Labels.label(function));
  }
}
