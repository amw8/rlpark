package rltoys.algorithms.learning.control.actorcritic.onpolicy;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.learning.predictions.td.OnPolicyTD;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class ActorCritic implements Control {
  private static final long serialVersionUID = 3772938582043052714L;
  public final OnPolicyTD critic;
  public final Actor[] actors;
  protected final Action[] lastAction;
  protected double reward = 0.0;

  public ActorCritic(OnPolicyTD critic, Actor... actors) {
    this.critic = critic;
    this.actors = actors;
    lastAction = new Action[actors.length];
  }

  protected double updateCritic(RealVector x_t, RealVector x_tp1, double r_tp1) {
    return critic.update(x_t, x_tp1, r_tp1);
  }

  protected void updateActors(RealVector x_t, double delta) {
    for (int i = 0; i < actors.length; i++)
      if (lastAction[i] != null)
        actors[i].update(x_t, lastAction[i], delta);
  }

  protected Action computeAction(RealVector x_tp1) {
    for (int i = 0; i < actors.length; i++)
      lastAction[i] = x_tp1 != null ? actors[i].proposeAction(x_tp1) : null;
    return x_tp1 != null ? ActionArray.merge(lastAction) : null;
  }

  @Override
  public Action step(RealVector x_t, Action a_t, RealVector x_tp1, double r_tp1) {
    reward = r_tp1;
    double delta = updateCritic(x_t, x_tp1, r_tp1);
    updateActors(x_t, delta);
    return computeAction(x_tp1);
  }

  @Override
  public Action proposeAction(RealVector x) {
    return computeAction(x);
  }
}
