package rltoys.algorithms.learning.predictions.td;

import rltoys.math.vector.RealVector;

public class GTD extends TDC {

  private static final long serialVersionUID = 3930476106142858179L;

  public GTD(double gamma, double alpha_v, double alpha_w, int nbFeatures) {
    super(gamma, alpha_v, alpha_w, nbFeatures);
  }

  public double update(double gamma, RealVector phi_t, RealVector phi_tp1, double r_tp1, double rho) {
    if (phi_t == null)
      return initEpisode();
    v_t = v.dotProduct(phi_t);
    v_tp1 = phi_tp1 != null ? v.dotProduct(phi_tp1) : 0.0;
    delta_t = r_tp1 + gamma * v_tp1 - v_t;
    RealVector tdCorrection = tdCorrection(phi_t, phi_tp1).mapMultiply(rho);
    v.addToSelf(phi_t.mapMultiply(alpha_v * delta_t).subtract(tdCorrection));
    w.addToSelf(phi_t.mapMultiply(alpha_w * (delta_t - phi_t.dotProduct(w))));
    return delta_t;
  }

  public double update(RealVector phi_t, RealVector phi_tp1, double r_tp1, double rho) {
    return update(gamma, phi_t, phi_tp1, r_tp1, rho);
  }

  @Override
  public double update(double gamma, RealVector phi_t, RealVector phi_tp1, double r_tp1) {
    return update(gamma, phi_t, phi_tp1, r_tp1, 1.0);
  }

  @Override
  public double update(RealVector phi_t, RealVector phi_tp1, double r_tp1) {
    return update(gamma, phi_t, phi_tp1, r_tp1);
  }
}
