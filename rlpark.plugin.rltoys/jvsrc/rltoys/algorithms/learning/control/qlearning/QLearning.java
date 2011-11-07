package rltoys.algorithms.learning.control.qlearning;

import rltoys.algorithms.learning.control.acting.Greedy;
import rltoys.algorithms.learning.predictions.LinearLearner;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.algorithms.representations.traces.Traces;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;

public class QLearning implements Predictor, LinearLearner {
  private static final long serialVersionUID = -404558746167490755L;
  protected final PVector theta;
  private final Traces e;
  private final double lambda;
  private final double gamma;
  private final double alpha;
  private final StateToStateAction toStateAction;
  private double delta;
  private final Greedy greedy;
  private Action at_star;

  public QLearning(Action[] actions, double alpha, double gamma, double lambda, StateToStateAction toStateAction,
      int nbFeatures, Traces prototype) {
    this.alpha = alpha;
    this.gamma = gamma;
    this.lambda = lambda;
    this.toStateAction = toStateAction;
    greedy = new Greedy(this, actions, toStateAction);
    theta = new PVector(nbFeatures);
    e = prototype.newTraces(nbFeatures);
  }

  public double update(RealVector x_t, Action a_t, RealVector x_tp1, Action a_tp1, double r_tp1) {
    if (x_t == null)
      return initEpisode();
    Action atp1_star = greedy.decide(x_tp1);
    RealVector phi_sa_t = toStateAction.stateAction(x_t, a_t);
    delta = r_tp1 + gamma * greedy.bestActionValue() - theta.dotProduct(phi_sa_t);
    if (a_t == at_star)
      e.update(gamma * lambda, phi_sa_t);
    else {
      e.clear();
      e.update(0, phi_sa_t);
    }
    theta.addToSelf(alpha * delta, e.vect());
    at_star = atp1_star;
    return delta;
  }

  private double initEpisode() {
    if (e != null)
      e.clear();
    delta = 0.0;
    at_star = null;
    return delta;
  }

  @Override
  public double predict(RealVector phi_sa) {
    return theta.dotProduct(phi_sa);
  }

  public PVector theta() {
    return theta;
  }

  @Override
  public void resetWeight(int index) {
    theta.setEntry(index, 0);
  }

  @Override
  public PVector weights() {
    return theta;
  }

  @Override
  public double error() {
    return delta;
  }

  public Policy greedy() {
    return greedy;
  }
}
