package rltoys.experiments.parametersweep.parameters;

import java.util.ArrayList;
import java.util.List;

public class Parameters extends AbstractParameters {
  private static final long serialVersionUID = -3022547944186532000L;

  public Parameters(RunInfo infos) {
    super(infos);
  }

  public Parameters(AbstractParameters parameters) {
    super(parameters.infos(), parameters.parameters, parameters.results);
  }

  public void putSweepParam(String label, double value) {
    parameters.put(label, value);
  }

  public static List<Parameters> combine(List<Parameters> existing, String label, double[] values) {
    assert existing.size() > 0;
    List<Parameters> combination = new ArrayList<Parameters>();
    for (Parameters parameters : existing) {
      for (double value : values) {
        Parameters combinedParameters = new Parameters(parameters);
        combinedParameters.putSweepParam(label, value);
        combination.add(combinedParameters);
      }
    }
    return combination;
  }

  public FrozenParameters froze() {
    return new FrozenParameters(infos(), parameters, results);
  }
}