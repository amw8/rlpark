package rltoys.environments.envio.observations;

import java.util.List;

import rltoys.algorithms.representations.actions.Action;

public class ActionFilter {
  private final List<Action> actions;
  private final int actionIndex;

  public ActionFilter(List<Action> actions, int actionIndex) {
    this.actionIndex = actionIndex;
    this.actions = actions;
  }

  public TStep filter(TStep step) {
    if (step.isEpisodeStarting() || step.a_t != null && step.a_t != Action.ActionUndef || actionIndex < 0)
      return step;
    Action a_t = findAction(step);
    return new TStep(step.time, step.o_t, a_t, step.o_tp1);
  }

  public Action findAction(TStep step) {
    return findAction(step.o_tp1);
  }

  public Action findAction(double[] o) {
    int actionListIndex = (int) (o != null ? o[actionIndex] : -1);
    return actionListIndex >= 0 ? actions.get(actionListIndex) : null;
  }
}