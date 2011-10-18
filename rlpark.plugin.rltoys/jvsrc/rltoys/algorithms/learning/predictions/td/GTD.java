package rltoys.algorithms.learning.predictions.td;

import rltoys.math.vector.RealVector;

public class GTD extends TDC implements GVF {

  private static final long serialVersionUID = 3930476106142858179L;

  public GTD(double gamma, double alpha_v, double alpha_w, int nbFeatures) {
    super(gamma, alpha_v, alpha_w, nbFeatures);
  }

  @Override
  public double update(double rho_t, RealVector x_t, RealVector x_tp1, double r_tp1, double gamma_tp1, double z_tp1) {
    if (x_t == null)
      return initEpisode();
    v_t = v.dotProduct(x_t);
    delta_t = r_tp1 + (1 - gamma_tp1) * z_tp1 + gamma_tp1 * v.dotProduct(x_tp1) - v_t;
    RealVector tdCorrection = tdCorrection(x_t, x_tp1).mapMultiply(rho_t);
    v.addToSelf(x_t.mapMultiply(alpha_v * delta_t).subtract(tdCorrection));
    w.addToSelf(x_t.mapMultiply(alpha_w * (delta_t - x_t.dotProduct(w))));
    return delta_t;
  }

  @Override
  public double update(RealVector x_t, RealVector x_tp1, double r_tp1) {
    return update(1.0, x_t, x_tp1, r_tp1, gamma, 0);
  }

  @Override
  public double update(RealVector x_t, RealVector x_tp1, double r_tp1, double gamma_tp1) {
    return update(1.0, x_t, x_tp1, r_tp1, gamma_tp1, 0);
  }

  @Override
  public double update(double rho_t, RealVector x_t, RealVector x_tp1, double r_tp1) {
    return update(rho_t, x_t, x_tp1, r_tp1, gamma, 0);
  }

  public double update(double rho_t, RealVector x_t, RealVector x_tp1, double r_tp1, double gamma_tp1) {
    return update(rho_t, x_t, x_tp1, r_tp1, gamma_tp1, 0);
  }
}
