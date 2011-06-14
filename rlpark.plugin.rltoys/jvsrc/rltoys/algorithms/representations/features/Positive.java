package rltoys.algorithms.representations.features;

import java.util.List;

import rltoys.math.representations.Function;
import rltoys.utils.Utils;


public class Positive implements Feature {

  private static final long serialVersionUID = -2732728651685449599L;
  private final Function function;
  private final boolean inverse;
  private double value;

  public Positive(Function function) {
    this(function, false);
  }

  public Positive(Function function, boolean inverse) {
    this.function = function;
    this.inverse = inverse;
  }

  @Override
  public List<Function> dependencies() {
    return Utils.asList(function);
  }

  @Override
  public void update() {
    value = function.value();
    if (inverse)
      value *= -1;
    if (value < 0)
      value = 0.0;
  }

  @Override
  public double value() {
    return value;
  }

  @Override
  public String toString() {
    String funstr = String.valueOf(function);
    if (inverse)
      funstr = "-" + funstr;
    return String.format("P(%s)", funstr);
  }
}
