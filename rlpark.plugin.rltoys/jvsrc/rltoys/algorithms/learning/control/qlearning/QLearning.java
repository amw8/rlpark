package rltoys.algorithms.learning.control.qlearning;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.algorithms.representations.traces.Traces;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;

public class QLearning implements Predictor {
  private static final long serialVersionUID = -404558746167490755L;
  protected final PVector theta;
  private final Traces e;
  private final double lambda;
  private final double gamma;
  private final double alpha;
  private final StateToStateAction toStateAction;
  private Action a_star = null;
  private double q_sa_tp1 = 0.0;
  private final Action[] actions;

  public QLearning(Action[] actions, double alpha, double gamma, double lambda, StateToStateAction toStateAction,
      int nbFeatures, Traces prototype) {
    this.alpha = alpha;
    this.gamma = gamma;
    this.lambda = lambda;
    this.toStateAction = toStateAction;
    this.actions = actions;
    theta = new PVector(nbFeatures);
    e = prototype.newTraces(nbFeatures);
  }

  private void pickupBestAction(RealVector s_tp1) {
    if (s_tp1 == null) {
      q_sa_tp1 = 0.0;
      a_star = null;
      return;
    }
    q_sa_tp1 = -Double.MAX_VALUE;
    for (Action a : actions) {
      RealVector phi_sa = toStateAction.stateAction(s_tp1, a);
      double q_sa = theta.dotProduct(phi_sa);
      if (q_sa > q_sa_tp1) {
        a_star = a;
        q_sa_tp1 = q_sa;
      }
    }
  }

  public double update(RealVector s_t, Action a_t, RealVector s_tp1, Action a_tp1, double r_tp1) {
    if (s_t == null)
      return initEpisode();
    pickupBestAction(s_tp1);
    RealVector phi_sa_t = toStateAction.stateAction(s_t, a_t);
    double delta = r_tp1 + gamma * q_sa_tp1 - theta.dotProduct(phi_sa_t);
    if (a_tp1 == a_star)
      e.update(gamma * lambda, phi_sa_t);
    theta.addToSelf(alpha * delta, e.vect());
    return delta;
  }

  private double initEpisode() {
    if (e != null)
      e.clear();
    a_star = null;
    return 0.0;
  }

  @Override
  public double predict(RealVector phi_sa) {
    return theta.dotProduct(phi_sa);
  }

  public PVector theta() {
    return theta;
  }
}
