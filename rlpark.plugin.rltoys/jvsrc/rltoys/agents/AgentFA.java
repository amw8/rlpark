package rltoys.agents;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.representations.Projector;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.observations.TRStep;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class AgentFA implements RLAgent {
  private static final long serialVersionUID = -8694734303900854141L;
  @Monitor
  protected final Control control;
  @Monitor
  protected final Projector projector;
  protected RealVector x_t;

  public AgentFA(Control control, Projector projector) {
    this.control = control;
    this.projector = projector;
  }

  @Override
  public Action getAtp1(TRStep step) {
    RealVector x_tp1 = projector.project(step.o_tp1);
    Action a_tp1 = control.step(x_t, step.a_t, x_tp1, step.r_tp1);
    x_t = x_tp1;
    return a_tp1;
  }

  public Control control() {
    return control;
  }

  public Projector projector() {
    return projector;
  }
}
