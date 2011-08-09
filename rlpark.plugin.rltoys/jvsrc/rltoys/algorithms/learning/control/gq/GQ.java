package rltoys.algorithms.learning.control.gq;

import rltoys.algorithms.learning.predictions.LinearLearner;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.traces.ATraces;
import rltoys.algorithms.representations.traces.Traces;
import rltoys.math.vector.MutableVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;
import rltoys.math.vector.implementations.SVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class GQ implements Predictor, LinearLearner {
  private static final long serialVersionUID = -4971665888576276439L;
  @Monitor(level = 4)
  public final PVector theta;
  protected double alpha_theta;
  protected double alpha_w;
  protected double beta_tp1;
  protected double lambda_t;
  @Monitor(level = 4)
  private final PVector w;
  private final Traces e;
  private double delta_t;

  public GQ(double alpha_theta, double alpha_w, double beta, double lambda, int nbFeatures) {
    this(alpha_theta, alpha_w, beta, lambda, nbFeatures, new ATraces());
  }

  public GQ(double alpha_theta, double alpha_w, double beta, double lambda, int nbFeatures, Traces prototype) {
    this.alpha_theta = alpha_theta;
    this.alpha_w = alpha_w;
    beta_tp1 = beta;
    lambda_t = lambda;
    e = prototype.newTraces(nbFeatures);
    theta = new PVector(nbFeatures);
    w = new PVector(nbFeatures);
  }

  private double initEpisode() {
    e.clear();
    return 0.0;
  }

  public double update(RealVector x_t, double rho_t, double r_tp1, RealVector x_bar_tp1, double z_tp1) {
    if (x_t == null)
      return initEpisode();
    double v_tp1 = x_bar_tp1 != null ? theta.dotProduct(x_bar_tp1) : 0;
    delta_t = r_tp1 + beta_tp1 * z_tp1 + (1 - beta_tp1) * v_tp1 - theta.dotProduct(x_t);
    e.update((1 - beta_tp1) * lambda_t * rho_t, x_t);
    MutableVector delta_e = e.vect().mapMultiply(delta_t);
    RealVector tdCorrection = x_bar_tp1 != null ?
        x_bar_tp1.mapMultiply((1 - beta_tp1) * (1 - lambda_t) * e.vect().dotProduct(w)) :
        new SVector(theta.size);
    theta.addToSelf(delta_e.subtract(tdCorrection).mapMultiply(alpha_theta));
    w.addToSelf(delta_e.subtractToSelf(x_t.mapMultiply(w.dotProduct(x_t))).mapMultiply(alpha_w));
    return delta_t;
  }

  @Override
  public double predict(RealVector x) {
    return theta.dotProduct(x);
  }

  @Override
  public PVector weights() {
    return theta;
  }

  @Override
  public void resetWeight(int index) {
    theta.data[index] = 0;
  }

  @Override
  public double error() {
    return delta_t;
  }
}
