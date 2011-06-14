package rltoys.environments.envio.actions;

import java.util.Arrays;

import rltoys.algorithms.representations.actions.Action;
import rltoys.math.vector.PVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;


public class ActionArray implements Action {
  private static final long serialVersionUID = 6468757011578627902L;
  @Monitor
  final public double[] actions;

  public ActionArray(double... actions) {
    this.actions = actions == null ? null : actions.clone();
    assert actions == null || new PVector(actions).checkValues();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (super.equals(obj))
      return true;
    ActionArray other = (ActionArray) obj;
    if (actions == other.actions)
      return true;
    if (actions == null || other.actions == null)
      return false;
    if (actions.length != other.actions.length)
      return false;
    for (int i = 0; i < actions.length; i++)
      if (actions[i] != other.actions[i])
        return false;
    return true;
  }

  static public Action merge(Action... actions) {
    if (actions.length == 1)
      return actions[0];
    int size = 0;
    double[][] actionData = new double[actions.length][];
    for (int i = 0; i < actions.length; i++) {
      ActionArray action = (ActionArray) actions[i];
      actionData[i] = action.actions;
      size += actionData[i].length;
    }
    double[] result = new double[size];
    int index = 0;
    for (double[] data : actionData) {
      System.arraycopy(data, 0, result, index, data.length);
      index += data.length;
    }
    return new ActionArray(result);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(actions);
  }

  @Override
  public String toString() {
    return Arrays.toString(actions);
  }
}
