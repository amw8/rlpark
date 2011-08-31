package rltoys.agents;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.tilescoding.TileCoders;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.observations.TRStep;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class StateTiledCodedAgent implements RLAgent {
  @Monitor
  private final Control control;
  @Monitor
  private final TileCoders tilesCoder;
  private RealVector x_t;

  public StateTiledCodedAgent(Control control, TileCoders tilesCoder) {
    this.control = control;
    this.tilesCoder = tilesCoder;
  }

  @Override
  public Action getAtp1(TRStep step) {
    double r_tp1 = step.r_tp1;
    RealVector x_tp1 = tilesCoder.project(step.o_tp1);
    Action a_tp1 = control.step(x_t, step.a_t, x_tp1, r_tp1);
    x_t = x_tp1;
    return a_tp1;
  }

  public Control control() {
    return control;
  }

  public TileCoders tileCoder() {
    return tilesCoder;
  }
}
