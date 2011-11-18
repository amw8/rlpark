package rltoys.algorithms.learning.predictions.td;


import rltoys.algorithms.representations.traces.ATraces;
import rltoys.algorithms.representations.traces.EligibilityTraceAlgorithm;
import rltoys.algorithms.representations.traces.Traces;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class TDLambda extends TD implements EligibilityTraceAlgorithm {
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
  public double update(RealVector x_t, RealVector x_tp1, double r_tp1, double gamma_tp1) {
    if (x_t == null)
      return initEpisode();
    v_t = v.dotProduct(x_t);
    delta_t = r_tp1 + gamma_tp1 * v.dotProduct(x_tp1) - v_t;
    e.update(lambda * gamma_tp1, x_t);
    v.addToSelf(alpha_v * delta_t, e.vect());
    return delta_t;
  }

  @Override
  public void resetWeight(int index) {
    super.resetWeight(index);
    e.vect().setEntry(index, 0);
  }

  @Override
  public Traces trace() {
    return e;
  }
}
