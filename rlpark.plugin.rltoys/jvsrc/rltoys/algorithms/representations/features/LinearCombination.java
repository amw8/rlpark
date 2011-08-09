package rltoys.algorithms.representations.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rltoys.math.representations.Function;
import rltoys.math.vector.PVector;

public class LinearCombination implements Feature {

  private static final long serialVersionUID = 1725749969515281465L;
  public final int size;
  private final PVector weights;
  private final List<Function> functions;
  private final PVector functionValues;
  private double value;

  public LinearCombination(Function... functions) {
    this(Arrays.asList(functions));
  }

  public LinearCombination(List<? extends Function> functions) {
    this(new PVector(functions.size()), functions);
  }

  public LinearCombination(PVector weights, Function... functions) {
    this(weights, Arrays.asList(functions));
  }

  public LinearCombination(PVector weights, List<? extends Function> functions) {
    assert weights != null;
    assert functions.isEmpty() || weights.size == functions.size();
    this.weights = weights;
    this.functions = new ArrayList<Function>(functions);
    functionValues = new PVector(weights.size);
    size = this.weights.size;
  }

  @Override
  public double value() {
    return value;
  }

  public Double value(PVector values) {
    if (values == null)
      return null;
    return value(weights, values);
  }

  static public double value(PVector weights, PVector values) {
    double sum = 0.0;
    for (int i = 0; i < values.size; i++)
      sum += weights.data[i] * values.data[i];
    return sum;
  }

  @Override
  public void update() {
    rltoys.algorithms.representations.features.Functions.set(functions, functionValues);
    value = weights.dotProduct(functionValues);
  }

  public void setFeature(int index, double weight, Function function) {
    weights.data[index] = weight;
    functions.set(index, function);
  }

  public PVector weights() {
    return weights;
  }

  public List<Function> functions() {
    return functions;
  }

  @Override
  public List<Function> dependencies() {
    return functions;
  }

  @Override
  public String toString() {
    StringBuffer result = new StringBuffer("(");
    String separator = " + ";
    for (int i = 0; i < size; i++) {
      result.append(weights.data[i]);
      result.append(" ");
      result.append(functions.get(i).toString());
      result.append(separator);
    }
    return result.toString().substring(0, result.length() - separator.length()) + ")";
  }
}
