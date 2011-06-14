package rltoys.algorithms.representations.features;

import java.util.List;

import rltoys.math.representations.Function;
import rltoys.utils.Utils;
import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.labels.Labels;


public class StepDelay implements Feature, Labeled {

  private static final long serialVersionUID = -5818228915542523130L;
  private final Function function;
  private Double v_t = null;
  private Double v_tm1 = null;

  public StepDelay(Function function) {
    this.function = function;
  }

  @Override
  public List<Function> dependencies() {
    return Utils.asList(function);
  }

  @Override
  public void update() {
    v_tm1 = v_t;
    v_t = function.value();
  }

  @Override
  public double value() {
    if (v_tm1 == null)
      return 0.0;
    return v_tm1;
  }

  public double value_tp1() {
    return v_t;
  }

  @Override
  public String toString() {
    return String.format("%s[=%s->%s])", label(), String.valueOf(v_tm1), String.valueOf(v_t));
  }

  @Override
  public String label() {
    int timeShift = 0;
    Function currentFunction = this;
    while (currentFunction instanceof StepDelay) {
      timeShift++;
      currentFunction = ((StepDelay) currentFunction).function;
    }
    return String.format("%s_{t-%02d}", Labels.label(currentFunction), timeShift);
  }
}
