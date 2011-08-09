package rltoys.algorithms.representations.actions;

import rltoys.math.vector.BVector;
import rltoys.math.vector.BinaryVector;
import rltoys.math.vector.ModifiableVector;
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
    if (s instanceof BinaryVector)
      return stateAction((BinaryVector) s, a);
    ModifiableVector phi_sa = s.newInstance(actionStateFeatureSize());
    for (int i = 0; i < actions.length; i++)
      if (actions[i].equals(a)) {
        phi_sa.setSubVector(stateFeatureSize * i, s);
        return phi_sa;
      }
    return null;
  }

  private RealVector stateAction(BinaryVector s, Action a) {
    BVector phi_sa = new BVector(actionStateFeatureSize(), s.nonZeroElements());
    for (int i = 0; i < actions.length; i++)
      if (actions[i].equals(a)) {
        for (int j : s.activeIndexes())
          phi_sa.setOn(stateFeatureSize * i + j);
        return phi_sa;
      }
    return null;
  }
}
