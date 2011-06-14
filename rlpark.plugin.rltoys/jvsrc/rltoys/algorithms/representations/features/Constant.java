package rltoys.algorithms.representations.features;

import java.util.List;

import rltoys.math.representations.Function;

public class Constant implements Feature {

  private static final long serialVersionUID = -7047425518830758955L;
  private double value;

  public Constant(double value) {
    this.value = value;
  }

  @Override
  public double value() {
    return value;
  }

  @Override
  public List<Function> dependencies() {
    return null;
  }

  @Override
  public void update() {
  }

  public void setValue(double value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.format("cst(%.2f)", value);
  }
}
