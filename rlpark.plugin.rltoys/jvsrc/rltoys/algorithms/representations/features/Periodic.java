package rltoys.algorithms.representations.features;

import java.util.List;

import rltoys.math.representations.Function;
import rltoys.utils.Utils;


public class Periodic implements Feature {
  private static final long serialVersionUID = 8864753881817143085L;
  private final int period;
  private final double multiplicator;
  private int time = -1;
  private double value = 0.0;

  public Periodic(int period) {
    assert period > 0;
    this.period = period;
    multiplicator = Math.PI * 2.0 / period;
    assert Utils.checkValue(multiplicator);
  }

  @Override
  public List<Function> dependencies() {
    return null;
  }

  @Override
  public void update() {
    time += 1;
    value = Math.sin(multiplicator * time);
  }

  @Override
  public double value() {
    return value;
  }

  @Override
  public String toString() {
    return String.format("P(%d)", period);
  }

}
