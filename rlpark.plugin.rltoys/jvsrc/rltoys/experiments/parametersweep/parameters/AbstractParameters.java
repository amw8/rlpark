package rltoys.experiments.parametersweep.parameters;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import rltoys.experiments.parametersweep.reinforcementlearning.RLParameters;

public abstract class AbstractParameters implements Comparable<AbstractParameters>, Serializable {
  private static final long serialVersionUID = 8135997315567194984L;
  protected final Map<String, Double> parameters = new LinkedHashMap<String, Double>();
  protected final Map<String, Double> results = new LinkedHashMap<String, Double>();

  public AbstractParameters() {
    this(new HashMap<String, Double>(), new HashMap<String, Double>());
  }

  public AbstractParameters(Map<String, Double> parameters, Map<String, Double> results) {
    this.parameters.putAll(parameters);
    this.results.putAll(results);
  }

  public void putResult(String parameterName, double parameterValue) {
    results.put(parameterName, parameterValue);
  }

  public double get(String name) {
    Double parameterValue = parameters.get(name);
    if (parameterValue != null)
      return parameterValue;
    return results.get(name);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder(parameters.toString());
    if (!results.isEmpty())
      result.append("=" + results.toString());
    return result.toString();
  }

  private Map<String, Double> getAll() {
    Map<String, Double> all = new LinkedHashMap<String, Double>(parameters);
    all.putAll(results);
    return all;
  }

  public String[] labels() {
    Map<String, Double> all = getAll();
    String[] result = new String[all.size()];
    all.keySet().toArray(result);
    return result;
  }

  public double[] values() {
    Map<String, Double> all = getAll();
    double[] result = new double[all.size()];
    int index = 0;
    for (Double value : all.values()) {
      result[index] = value;
      index++;
    }
    return result;
  }

  public boolean hasFlag(String flag) {
    return parameters.containsKey(flag);
  }

  public int maxEpisodeTimeSteps() {
    return (int) ((double) parameters.get(RLParameters.MaxEpisodeTimeSteps));
  }

  public int nbEpisode() {
    return (int) ((double) parameters.get(RLParameters.NbEpisode));
  }

  @Override
  public int compareTo(AbstractParameters other) {
    for (Map.Entry<String, Double> entry : other.parameters.entrySet()) {
      Double d1 = parameters.get(entry.getKey());
      if (d1 == null)
        return -1;
      Double d2 = entry.getValue();
      if (d2 == null)
        return 1;
      int compared = Double.compare(d1, d2);
      if (compared != 0)
        return compared;
    }
    return 0;
  }
}