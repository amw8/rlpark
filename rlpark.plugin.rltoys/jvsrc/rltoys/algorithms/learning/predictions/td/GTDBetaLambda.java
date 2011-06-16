package rltoys.algorithms.learning.predictions.td;

import rltoys.algorithms.learning.predictions.LinearLearner;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.traces.PATraces;
import rltoys.algorithms.representations.traces.Traces;
import rltoys.math.vector.PVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.SVector;

public class GTDBetaLambda implements Predictor, LinearLearner {
  private static final long serialVersionUID = 8687476023177671278L;
  protected double alpha_v;
  protected double alpha_w;
  protected double lambda;

  protected final PVector v;
  private final PVector w;
  private final Traces e;
  private double delta_t;

  public GTDBetaLambda(double alpha_v, double alpha_w, double lambda, int nbFeatures) {
    this(alpha_v, alpha_w, lambda, nbFeatures, new PATraces());
  }

  public GTDBetaLambda(double alpha_v, double alpha_w, double lambda, int nbFeatures, Traces prototype) {
    this.alpha_v = alpha_v;
    this.alpha_w = alpha_w;
    this.lambda = lambda;
    v = new PVector(nbFeatures);
    w = new PVector(nbFeatures);
    e = prototype.newTraces(nbFeatures);
  }

  public double update(double rho_tm1, double beta_t, double rho_t, RealVector phi_t, double beta_tp1,
      RealVector phi_tp1, double z_tp1, double r_tp1) {
    if (phi_t == null)
      return initEpisode();
    double v_tp1 = phi_tp1 != null ? v.dotProduct(phi_tp1) : 0.0;
    delta_t = rho_t * (r_tp1 + beta_tp1 * z_tp1 + (1 - beta_tp1) * v_tp1) - v.dotProduct(phi_t);
    e.update(rho_tm1 * (1 - beta_t) * lambda, phi_t);
    RealVector e_delta_t = e.vect().mapMultiply(delta_t);
    RealVector tdCorrection = null;
    if (phi_tp1 != null)
      tdCorrection = phi_tp1.mapMultiply(rho_t * (1 - beta_tp1) * (1 - lambda) * e.vect().dotProduct(w));
    else
      tdCorrection = new SVector(v.size);
    v.addToSelf(e_delta_t.subtract(tdCorrection).mapMultiplyToSelf(alpha_v));
    w.addToSelf(e_delta_t.subtract(phi_t.mapMultiply(w.dotProduct(phi_t))).mapMultiplyToSelf(alpha_w));
    assert v.checkValues();
    return delta_t;
  }

  private double initEpisode() {
    e.clear();
    return 0.0;
  }

  @Override
  public double predict(RealVector x) {
    return v.dotProduct(x);
  }

  @Override
  public void resetWeight(int index) {
    v.data[index] = 0;
    w.data[index] = 0;
  }

  @Override
  public PVector weights() {
    return v;
  }

  @Override
  public double error() {
    return delta_t;
  }
}
