package rltoys.math.vector.implementations;

import java.util.List;

import rltoys.math.representations.Function;
import rltoys.utils.Utils;

public class PVectors {
  static public void set(PVector vector, List<? extends Function> functions) {
    assert vector.size == functions.size();
    double a[] = vector.data;
    for (int i = 0; i < vector.size; i++) {
      final Function function = functions.get(i);
      double value = function.value();
      assert Utils.checkValue(value);
      a[i] = value;
    }
  }

  static public double mean(PVector vector) {
    double[] a = vector.data;
    double sum = 0.0;
    for (int i = 0; i < vector.size; i++)
      sum += a[i];
    return sum / vector.size;
  }

  static public double min(PVector vector) {
    double min = Double.MAX_VALUE;
    for (double value : vector.data)
      min = Math.min(value, min);
    return min;
  }

  static public double max(PVector vector) {
    double max = -Double.MAX_VALUE;
    for (double value : vector.data)
      max = Math.max(value, max);
    return max;
  }
}
