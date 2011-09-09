package rltoys.algorithms.representations.actions;

import java.util.LinkedHashMap;
import java.util.Map;

import rltoys.math.vector.RealVector;

public class StateToStateActionCache implements StateToStateAction {
  private static final long serialVersionUID = 5563111606416014268L;
  private final StateToStateAction toStateAction;
  private final Action[] actions;
  private Map<Action, RealVector> sa_t = null;
  private Map<Action, RealVector> sa_tp1 = null;
  private RealVector s_t = null;
  private RealVector s_tp1 = null;

  public StateToStateActionCache(StateToStateAction toStateAction, Action[] actions) {
    this.toStateAction = toStateAction;
    this.actions = actions;
  }

  public void update(RealVector s) {
    s_t = s_tp1;
    sa_t = sa_tp1;
    s_tp1 = s;
    sa_tp1 = new LinkedHashMap<Action, RealVector>();
    for (Action a : actions)
      sa_tp1.put(a, toStateAction.stateAction(s, a));
  }

  @Override
  public RealVector stateAction(RealVector s, Action a) {
    if (s == null)
      return null;
    assert s == s_t || s == s_tp1;
    if (s == s_t)
      return sa_t.get(a);
    return sa_tp1.get(a);
  }

  @Override
  public int vectorSize() {
    return toStateAction.vectorSize();
  }
}
