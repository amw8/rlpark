package rltoys.agents;

import java.io.Serializable;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.tilescoding.TileCoders;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.observations.TRStep;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class TileCodingAgent implements RLAgent, Serializable {
  private static final long serialVersionUID = 7140738339093983232L;
  @Monitor
  private final Control control;
  private final TileCoders tileCoders;

  public TileCodingAgent(Control control, TileCoders tilesCoder) {
    this.control = control;
    tileCoders = tilesCoder;
  }

  @Override
  public Action getAtp1(TRStep step) {
    double r_tp1 = step.r_tp1;
    tileCoders.project(step.o_t);
    RealVector s_t = tileCoders.getCurrentState();
    tileCoders.project(step.o_tp1);
    RealVector s_tp1 = tileCoders.getCurrentState();
    return control.step(s_t, step.a_t, s_tp1, r_tp1);
  }

  public Control control() {
    return control;
  }

  public TileCoders tileCoders() {
    return tileCoders;
  }
}
