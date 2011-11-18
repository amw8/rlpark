package rltoys.algorithms.learning.predictions.td;

import rltoys.algorithms.representations.traces.ATraces;
import rltoys.algorithms.representations.traces.EligibilityTraceAlgorithm;
import rltoys.algorithms.representations.traces.Traces;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;
import rltoys.math.vector.implementations.SVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class GTDLambda extends TD implements GVF, EligibilityTraceAlgorithm {
  private static final long serialVersionUID = 8687476023177671278L;
  @Monitor(level = 4)
  protected final PVector w;
  public final double alpha_w;
  protected double lambda;
  private double gamma_t;
  private final Traces e;


  public GTDLambda(double lambda, double gamma, double alpha_v, double alpha_w, int nbFeatures) {
    this(lambda, gamma, alpha_v, alpha_w, nbFeatures, new ATraces());
  }

  public GTDLambda(double lambda, double gamma, double alpha_v, double alpha_w, int nbFeatures, Traces prototype) {
    super(gamma, alpha_v, nbFeatures);
    this.lambda = lambda;
    this.alpha_w = alpha_w;
    w = new PVector(nbFeatures);
    e = prototype.newTraces(nbFeatures);
  }

  @Override
  public double update(double rho_t, RealVector x_t, RealVector x_tp1, double r_tp1, double gamma_tp1, double z_tp1) {
    if (x_t == null) {
      gamma_t = gamma_tp1;
      return initEpisode();
    }
    v_t = v.dotProduct(x_t);
    delta_t = r_tp1 + (1 - gamma_tp1) * z_tp1 + gamma_tp1 * v.dotProduct(x_tp1) - v_t;
    e.update(gamma_t * lambda, x_t);
    e.vect().mapMultiplyToSelf(rho_t);
    RealVector e_delta = e.vect().mapMultiply(delta_t);
    RealVector correction = x_tp1 != null ? x_tp1.mapMultiply(e.vect().dotProduct(w) * gamma_tp1 * (1 - lambda))
        : new SVector(w.size);
    v.addToSelf(e_delta.subtract(correction).mapMultiplyToSelf(alpha_v));
    w.addToSelf(e_delta.subtract(x_t.mapMultiply(w.dotProduct(x_t))).mapMultiplyToSelf(alpha_w));

    gamma_t = gamma_tp1;
    return delta_t;
  }

  @Override
  protected double initEpisode() {
    e.clear();
    return super.initEpisode();
  }

  @Override
  public void resetWeight(int index) {
    super.resetWeight(index);
    e.vect().setEntry(index, 0);
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

  @Override
  public PVector secondaryWeights() {
    return w;
  }

  @Override
  public Traces trace() {
    return e;
  }
}
