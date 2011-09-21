package rltoys.algorithms.representations.actions;

import rltoys.math.vector.BinaryVector;
import rltoys.math.vector.MutableVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.VectorEntry;
import rltoys.math.vector.implementations.BVector;

public class TabularAction implements StateToStateAction {
  private static final long serialVersionUID = 1705117400022134128L;
  private final Action[] actions;
  private final int stateFeatureSize;

  public TabularAction(Action[] actions, int stateFeatureSize) {
    this.actions = actions;
    this.stateFeatureSize = stateFeatureSize;
  }

  @Override
  public int vectorSize() {
    return stateFeatureSize * actions.length;
  }

  @Override
  public RealVector stateAction(RealVector s, Action a) {
    if (s == null || a == null)
      return null;
    if (s instanceof BinaryVector)
      return stateAction((BinaryVector) s, a);
    MutableVector phi_sa = s.newInstance(vectorSize());
    for (int i = 0; i < actions.length; i++)
      if (actions[i] == a) {
        int offset = stateFeatureSize * i;
        for (VectorEntry entry : s)
          phi_sa.setEntry(entry.index() + offset, entry.value());
        return phi_sa;
      }
    return null;
  }

  private RealVector stateAction(BinaryVector s, Action a) {
    BVector phi_sa = new BVector(vectorSize(), s.nonZeroElements());
    phi_sa.setOrderedIndexes(s.activeIndexes());
    for (int i = 0; i < actions.length; i++)
      if (actions[i] == a) {
        int offset = stateFeatureSize * i;
        int[] indexes = phi_sa.activeIndexes();
        for (int j = 0; j < indexes.length; j++)
          indexes[j] += offset;
        return phi_sa;
      }
    return null;
  }
}
