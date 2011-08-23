package rltoys.experiments.parametersweep.parameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FrozenParameters extends AbstractParameters {
  private static final long serialVersionUID = -1853925775244660996L;
  protected final int hashcode;

  public FrozenParameters(Map<String, Double> parameters, Map<String, Double> results) {
    this(parameters, results, new HashSet<String>());
  }

  public FrozenParameters(Map<String, Double> parameters, Map<String, Double> results, Set<String> flags) {
    super(flags);
    putAllSorted(parameters, this.parameters);
    putAllSorted(results, this.results);
    hashcode = computeHashcode(parameters);
  }

  private void putAllSorted(Map<String, Double> source, Map<String, Double> target) {
    List<String> sortedKeys = new ArrayList<String>(source.keySet());
    Collections.sort(sortedKeys);
    for (String key : sortedKeys)
      target.put(key, source.get(key));
  }

  protected int computeHashcode(Map<String, Double> parameters) {
    int hashcode = 0;
    for (Map.Entry<String, Double> entry : parameters.entrySet())
      hashcode += entry.hashCode();
    for (String flag : flags)
      hashcode += flag.hashCode();
    return hashcode;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (super.equals(obj))
      return true;
    AbstractParameters other = (AbstractParameters) obj;
    return parameters.equals(other.parameters) && flags.equals(other.flags);
  }

  @Override
  public int hashCode() {
    return hashcode;
  }
}
