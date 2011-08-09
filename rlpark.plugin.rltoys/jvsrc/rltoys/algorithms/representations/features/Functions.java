package rltoys.algorithms.representations.features;

import java.util.ArrayList;
import java.util.List;

import rltoys.math.representations.Function;
import rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;


public class Functions {
  public static class FunctionLogged implements Monitored, Labeled {
    private final Function function;

    public FunctionLogged(Function function) {
      this.function = function;
    }

    @Override
    public double monitoredValue() {
      return function.value();
    }

    @Override
    public String label() {
      if (function instanceof Labeled)
        return ((Labeled) function).label();
      return function.toString();
    }
  }

  public static List<String> getLabels(List<? extends Function> functions) {
    List<String> labels = new ArrayList<String>();
    for (Function function : functions)
      labels.add(Labels.label(function));
    return labels;
  }

  static public void set(List<? extends Function> functions, PVector vector) {
    for (int i = 0; i < vector.size; i++)
      vector.data[i] = functions.get(i).value();
  }
}
