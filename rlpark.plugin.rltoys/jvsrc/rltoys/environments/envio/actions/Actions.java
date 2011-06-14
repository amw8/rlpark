package rltoys.environments.envio.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rltoys.math.ranges.Range;

public class Actions {
  static private double[][] createActionsAsArrays(Range... actionValues) {
    if (actionValues.length == 0) {
      double[][] result = new double[1][];
      result[0] = new double[0];
      return result;
    }
    Range[] childValues = Arrays.copyOf(actionValues, actionValues.length - 1);
    double[][] childActions = createActionsAsArrays(childValues);
    double[][] result = new double[3 * childActions.length][];
    int actionIndex = 0;
    for (double[] childAction : childActions) {
      double[][] newActions = createNewActions(childAction, actionValues[actionValues.length - 1]);
      for (double[] newAction : newActions) {
        result[actionIndex] = newAction;
        actionIndex++;
      }
    }
    return result;
  }

  private static double[][] createNewActions(double[] childAction, Range actionValue) {
    double[] action = new double[childAction.length + 1];
    for (int i = 0; i < childAction.length; i++)
      action[i] = childAction[i];
    double[][] result = { action.clone(), action.clone(), action.clone() };
    result[0][childAction.length] = actionValue.min();
    result[1][childAction.length] = (actionValue.max() + actionValue.min()) / 2.0;
    result[2][childAction.length] = actionValue.max();
    return result;
  }

  static public List<ActionArray> createActions(Range... actionValues) {
    List<ActionArray> result = new ArrayList<ActionArray>();
    double[][] actions = createActionsAsArrays(actionValues);
    for (double[] action : actions)
      result.add(new ActionArray(action));
    return result;
  }

  static public List<ActionArray> createActions(double... actionValues) {
    Range[] ranges = new Range[actionValues.length];
    for (int i = 0; i < ranges.length; i++)
      ranges[i] = new Range(-actionValues[i], actionValues[i]);
    return createActions(ranges);
  }
}
