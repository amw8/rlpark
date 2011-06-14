package rltoys.algorithms.representations.actions;

import rltoys.math.vector.RealVector;

public class TabularAction implements StateToStateAction {

  private static final long serialVersionUID = 1705117400022134128L;
  private final Action[] actions;
  private final int stateFeatureSize;

  public TabularAction(Action[] actions, int stateFeatureSize) {
    this.actions = actions;
    this.stateFeatureSize = stateFeatureSize;
  }

  @Override
  public int actionStateFeatureSize() {
    return stateFeatureSize * actions.length;
  }

  @Override
  public RealVector stateAction(RealVector s, Action a) {
    if (s == null || a == null)
      return null;
    RealVector phi_sa = s.newInstance(actionStateFeatureSize());
    for (int i = 0; i < actions.length; i++)
      if (actions[i].equals(a)) {
        phi_sa.setSubVector(stateFeatureSize * i, s);
        return phi_sa;
      }
    return null;
  }
}
