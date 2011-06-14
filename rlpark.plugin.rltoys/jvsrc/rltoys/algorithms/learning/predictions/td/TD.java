package rltoys.algorithms.learning.predictions.td;

import rltoys.math.vector.PVector;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.wrappers.Abs;
import zephyr.plugin.core.api.monitoring.wrappers.Squared;

public class TD implements OnPolicyTD {
  private static final long serialVersionUID = -3640476464100200081L;
  final public double alpha_v;
  protected double gamma;
  @Monitor(level = 4)
  final public PVector v;
  @Monitor
  protected double v_t;
  @Monitor
  protected double v_tp1;
  @Monitor(wrappers = { Squared.ID, Abs.ID })
  public double delta_t;

  public TD(double alpha_v, int nbFeatures) {
    this(Double.NaN, alpha_v, nbFeatures);
  }

  public TD(double gamma, double alpha_v, int nbFeatures) {
    this.alpha_v = alpha_v;
    this.gamma = gamma;
    v = new PVector(nbFeatures);
  }

  protected double initEpisode() {
    v_tp1 = 0;
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
    v_tp1 = phi_tp1 != null ? v.dotProduct(phi_tp1) : 0.0;
    delta_t = r_tp1 + gamma * v_tp1 - v_t;
    v.addToSelf(phi_t.mapMultiply(alpha_v * delta_t));
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
  public PVector theta() {
    return v;
  }

  public double v_t() {
    return v_t;
  }
}
