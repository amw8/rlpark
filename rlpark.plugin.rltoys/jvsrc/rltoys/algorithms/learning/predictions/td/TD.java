package rltoys.algorithms.learning.predictions.td;

import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.wrappers.Abs;
import zephyr.plugin.core.api.monitoring.wrappers.Squared;

public class TD implements OnPolicyTD {
  private static final long serialVersionUID = -3640476464100200081L;
  final public double alpha_v;
  protected double gamma;
  @Monitor(level = 4)
  final public PVector v;
  @Monitor(wrappers = { Squared.ID, Abs.ID })
  protected double delta_t;
  protected double v_t;

  public TD(double alpha_v, int nbFeatures) {
    this(Double.NaN, alpha_v, nbFeatures);
  }

  public TD(double gamma, double alpha_v, int nbFeatures) {
    this.alpha_v = alpha_v;
    this.gamma = gamma;
    v = new PVector(nbFeatures);
  }

  protected double initEpisode() {
    v_t = 0;
    return 0;
  }

  @Override
  public double update(RealVector phi_t, RealVector phi_tp1, double r_tp1) {
    return update(gamma, phi_t, phi_tp1, r_tp1);
  }

  public double update(double gamma, RealVector phi_t, RealVector phi_tp1, double r_tp1) {
    if (phi_t == null)
      return initEpisode();
    v_t = v.dotProduct(phi_t);
    delta_t = r_tp1 + gamma * v.dotProduct(phi_tp1) - v_t;
    v.addToSelf(alpha_v * delta_t, phi_t);
    return delta_t;
  }

  @Override
  public double predict(RealVector phi) {
    return v.dotProduct(phi);
  }

  public double gamma() {
    return gamma;
  }

  @Override
  public PVector weights() {
    return v;
  }

  @Override
  public void resetWeight(int index) {
    v.data[index] = 0;
  }

  @Override
  public double error() {
    return delta_t;
  }

  @Override
  public double prediction() {
    return v_t;
  }
}
