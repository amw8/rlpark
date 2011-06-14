package rltoys.experiments.parametersweep.interfaces;

import java.io.Serializable;
import java.util.List;

import rltoys.experiments.parametersweep.parameters.Parameters;


public interface ParametersProvider extends Serializable {
  List<Parameters> provideParameters(Context context);
}
