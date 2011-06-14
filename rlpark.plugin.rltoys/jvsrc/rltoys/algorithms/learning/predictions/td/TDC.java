package rltoys.algorithms.learning.predictions.td;

import rltoys.math.vector.PVector;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class TDC extends TD {

  private static final long serialVersionUID = 7305877522126081130L;
  @Monitor(level = 4)
  protected final PVector w;
  public final double alpha_w;

  public TDC(double gamma, double alpha_v, double alpha_w, int nbFeatures) {
    super(gamma, alpha_v, nbFeatures);
    w = new PVector(nbFeatures);
    this.alpha_w = alpha_w;
  }

  @Override
  public double update(double gamma, RealVector phi_t, RealVector phi_tp1, double r_tp1) {
    if (phi_t == null)
      return initEpisode();
    v_tp1 = phi_tp1 != null ? v.dotProduct(phi_tp1) : 0.0;
    delta_t = r_tp1 + gamma * v_tp1 - v.dotProduct(phi_t);
    RealVector tdCorrection = tdCorrection(phi_t, phi_tp1);
    v.addToSelf(phi_t.mapMultiply(alpha_v * delta_t).subtract(tdCorrection));
    w.addToSelf(phi_t.mapMultiply(alpha_w * (delta_t - phi_t.dotProduct(w))));
    return delta_t;
  }

  protected RealVector tdCorrection(RealVector phi_t, RealVector phi_tp1) {
    if (phi_tp1 == null)
      return new PVector(phi_t.getDimension());
    return phi_tp1.mapMultiply(phi_t.dotProduct(w)).mapMultiply(alpha_v * gamma);
  }
}
