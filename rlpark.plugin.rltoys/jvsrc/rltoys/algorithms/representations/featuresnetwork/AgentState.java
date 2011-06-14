package rltoys.algorithms.representations.featuresnetwork;

import java.util.List;

import rltoys.algorithms.representations.features.Constant;
import rltoys.algorithms.representations.features.Feature;
import rltoys.math.representations.Function;
import rltoys.utils.Utils;


public class AgentState extends AbstractAgentState {
  private static final long serialVersionUID = -5137795720223046479L;

  public AgentState(Function... stateFeatures) {
    this(Utils.asList(stateFeatures));
  }

  public AgentState(List<Function> stateFeatures) {
    for (Function function : stateFeatures)
      addStateFeature((Feature) function);
  }

  public AgentState(int size) {
    this();
    for (int i = 0; i < size; i++)
      addStateFeature(new Constant(0));
  }

  @Override
  public void update() {
    super.update();
  }
}
