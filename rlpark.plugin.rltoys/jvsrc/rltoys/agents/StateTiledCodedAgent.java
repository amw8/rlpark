package rltoys.agents;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.featuresnetwork.ObservationAgentState;
import rltoys.algorithms.representations.tilescoding.TileCoders;
import rltoys.environments.envio.Agent;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.observations.TStep;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class StateTiledCodedAgent implements Agent {
  private static final long serialVersionUID = 3256878947900519007L;
  @Monitor
  private final Control control;
  @Monitor
  private final TileCoders tilesCoder;
  @Monitor
  private final ObservationAgentState agentState;
  private RealVector phi_t;

  public StateTiledCodedAgent(Control control, TileCoders tilesCoder, ObservationAgentState agentState) {
    this.control = control;
    this.tilesCoder = tilesCoder;
    this.agentState = agentState;
  }

  @Override
  public Action getAtp1(TStep step) {
    double r_tp1 = ((TRStep) step).r_tp1;
    agentState.update(step);
    RealVector phi_tp1 = tilesCoder.project(agentState.currentState().data);
    Action a_tp1 = control.step(phi_t, step.a_t, phi_tp1, r_tp1);
    phi_t = phi_tp1;
    return a_tp1;
  }

  public Control control() {
    return control;
  }

  public TileCoders tileCoder() {
    return tilesCoder;
  }
}
