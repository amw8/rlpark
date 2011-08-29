package rltoys.experiments.parametersweep.parameters;

import java.util.ArrayList;
import java.util.List;

public class Parameters extends AbstractParameters {
  private static final long serialVersionUID = -3022547944186532000L;
  public Parameters() {
    super();
  }

  public Parameters(AbstractParameters parameters) {
    super(parameters.parameters, parameters.results);
  }

  public void put(String label, double value) {
    parameters.put(label, value);
  }

  public void enableFlag(String flag) {
    parameters.put(flag, 1.0);
  }

  public static List<Parameters> combine(List<Parameters> existing, String label, double[] values) {
    if (existing == null || existing.isEmpty())
      return createParameters(label, values);
    List<Parameters> combination = new ArrayList<Parameters>();
    for (Parameters parameters : existing) {
      for (double value : values) {
        Parameters combinedParameters = new Parameters(parameters);
        combinedParameters.put(label, value);
        combination.add(combinedParameters);
      }
    }
    return combination;
  }

  private static List<Parameters> createParameters(String label, double[] values) {
    List<Parameters> parametersList = new ArrayList<Parameters>();
    for (double value : values) {
      Parameters parameters = new Parameters();
      parameters.put(label, value);
      parametersList.add(parameters);
    }
    return parametersList;
  }

  public FrozenParameters froze() {
    return new FrozenParameters(parameters, results);
  }
}