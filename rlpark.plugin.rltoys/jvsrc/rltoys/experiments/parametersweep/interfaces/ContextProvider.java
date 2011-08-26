package rltoys.experiments.parametersweep.interfaces;

import java.io.Serializable;
import java.util.List;

public interface ContextProvider extends Serializable {
  public List<? extends Context> provideContexts();
}
