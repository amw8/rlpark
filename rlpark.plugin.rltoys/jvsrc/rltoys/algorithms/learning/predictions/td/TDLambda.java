package rltoys.algorithms.learning.predictions.td;


import rltoys.algorithms.representations.traces.ATraces;
import rltoys.algorithms.representations.traces.Traces;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class TDLambda extends TD {
  private static final long serialVersionUID = 8613865620293286722L;
  private final double lambda;
  @Monitor
  public final Traces e;

  public TDLambda(double lambda, double gamma, double alpha, int nbFeatures) {
    this(lambda, gamma, alpha, nbFeatures, new ATraces());
  }

  public TDLambda(double lambda, double gamma, double alpha, int nbFeatures, Traces prototype) {
    super(gamma, alpha, nbFeatures);
    this.lambda = lambda;
    e = prototype.newTraces(nbFeatures);
  }

  @Override
  protected double initEpisode() {
    e.clear();
    return super.initEpisode();
  }

  @Override
  public double update(double gamma, RealVector phi_t, RealVector phi_tp1, double r_tp1) {
    if (phi_t == null)
      return initEpisode();
    v_t = v.dotProduct(phi_t);
    v_tp1 = phi_tp1 != null ? v.dotProduct(phi_tp1) : 0.0;
    delta_t = r_tp1 + gamma * v_tp1 - v_t;
    e.update(lambda * gamma, phi_t);
    v.addToSelf(alpha_v * delta_t, e.vect());
    return delta_t;
  }

  @Override
  public void resetWeight(int index) {
    super.resetWeight(index);
    e.vect().setEntry(index, 0);
  }
}
