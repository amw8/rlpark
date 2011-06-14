package rltoys.algorithms.representations.features;

import java.util.Arrays;
import java.util.List;

import rltoys.math.Constants;
import rltoys.math.representations.Function;
import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.labels.Labels;

public class TrackingNormalization implements Feature, Labeled {

  private static final long serialVersionUID = 8868033334438844360L;
  private final Function function;
  private double mean = 0;
  private double variance = 0;
  private double confidence = 0;
  private final double stepSize;
  private double value;

  public TrackingNormalization(double stepSize, Function function) {
    this.stepSize = stepSize;
    this.function = function;
  }

  @Override
  public List<Function> dependencies() {
    return Arrays.asList(function);
  }

  @Override
  public void update() {
    double x = function.value();
    mean = mean + stepSize * (x - mean);
    variance = variance + stepSize * ((x - mean) * (x - mean) - variance);
    confidence = confidence + stepSize * (1.0 - confidence);
    if (variance < Constants.EPSILON)
      value = 0;
    else
      value = confidence * (x - mean) / Math.sqrt(variance);
  }

  @Override
  public double value() {
    return value;
  }

  @Override
  public String label() {
    return "{" + Labels.label(function) + "}";
  }
}
