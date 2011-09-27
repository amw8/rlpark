package rltoys.algorithms.learning.predictions.td;

import rltoys.algorithms.representations.traces.Traces;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.SVector;

public class GTDLambda extends GTD {
  private static final long serialVersionUID = 8687476023177671278L;
  protected double lambda;
  private final Traces e;

  public GTDLambda(double lambda, double gamma, double alpha_v, double alpha_w, int nbFeatures, Traces prototype) {
    super(gamma, alpha_v, alpha_w, nbFeatures);
    this.lambda = lambda;
    e = prototype.newTraces(nbFeatures);
  }

  @Override
  public double update(double gamma, RealVector phi_t, RealVector phi_tp1, double r_tp1, double rho_t) {
    if (phi_t == null)
      return initEpisode();
    v_t = v.dotProduct(phi_t);
    delta_t = r_tp1 + gamma * v.dotProduct(phi_tp1) - v_t;
    e.update(gamma * lambda, phi_t, rho_t);
    RealVector e_delta = e.vect().mapMultiply(delta_t);
    RealVector correction = phi_tp1 != null ? phi_tp1.mapMultiply(e.vect().dotProduct(w) * gamma * (1 - lambda))
        : new SVector(w.size);
    v.addToSelf(e_delta.subtract(correction).mapMultiplyToSelf(alpha_v));
    w.addToSelf(e_delta.subtract(phi_t.mapMultiply(w.dotProduct(phi_t))).mapMultiplyToSelf(alpha_w));
    return delta_t;
  }

  @Override
  protected double initEpisode() {
    e.clear();
    return super.initEpisode();
  }

  @Override
  public double predict(RealVector x) {
    return v.dotProduct(x);
  }

  @Override
  public void resetWeight(int index) {
    super.resetWeight(index);
    e.vect().setEntry(index, 0);
  }
}
