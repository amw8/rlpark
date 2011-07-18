package rltoys.agents;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.learning.control.OffPolicyControl;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.featuresnetwork.ObservationAgentState;
import rltoys.environments.envio.Agent;
import rltoys.environments.envio.OffPolicyLearner;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.observations.TStep;
import rltoys.math.vector.PVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class ContinuousAgent implements Agent, OffPolicyLearner {
  @Monitor
  private final Control control;
  @Monitor
  protected final ObservationAgentState agentState;

  public ContinuousAgent(Control control, ObservationAgentState agentState) {
    this.control = control;
    this.agentState = agentState;
  }

  @Override
  public Action getAtp1(TStep step) {
    double r_tp1 = ((TRStep) step).r_tp1;
    PVector s_t = agentState.currentState() == null ? null : agentState.currentState().copy();
    agentState.update(step);
    PVector s_tp1 = agentState.currentState() == null ? null : agentState.currentState().copy();
    return control.step(s_t, step.a_t, s_tp1, r_tp1);
  }

  @Override
  public void learn(TStep step, Action a_tp1) {
    double r_tp1 = ((TRStep) step).r_tp1;
    PVector s_t = step.isEpisodeStarting() ? null : agentState.currentState().copy();
    agentState.update(step);
    PVector s_tp1 = agentState.currentState().copy();
    ((OffPolicyControl) control).learn(s_t, step.a_t, s_tp1, r_tp1, a_tp1);
  }

  @Override
  public Action proposeAction(TStep o) {
    agentState.update(o);
    PVector s = agentState.currentState().copy();
    return ((OffPolicyControl) control).proposeAction(s);
  }

  public Control control() {
    return control;
  }

  public ObservationAgentState agentState() {
    return agentState;
  }
}
