package rltoys.algorithms.learning.predictions.td;

import rltoys.algorithms.representations.traces.ATraces;
import rltoys.algorithms.representations.traces.PATraces;
import rltoys.algorithms.representations.traces.Traces;
import rltoys.math.vector.PVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.SparseVector;
import rltoys.math.vector.SparseVector.ElementIterator;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.wrappers.Abs;
import zephyr.plugin.core.api.monitoring.wrappers.Squared;

@Monitor
public class TDLambdaAutostep implements OnPolicyTD {
  private static final long serialVersionUID = 1567652945995637498L;
  static private final double Mu = 0.01;
  @Monitor(level = 4)
  final private PVector v;
  private double v_t;
  private double v_tp1;
  @Monitor(wrappers = { Squared.ID, Abs.ID })
  private double delta_t;

  final protected Traces e;
  final protected double tau;
  @Monitor(level = 4)
  final protected PVector alpha;
  @Monitor(level = 4)
  final protected PVector h;
  @Monitor(level = 4)
  final protected PVector s;
  protected double maxOneM2;
  private final double gamma;
  private final double lambda;
  private double m;

  public TDLambdaAutostep(double lambda, double gamma, int nbFeatures) {
    this(lambda, gamma, 1000.0, nbFeatures, new PATraces());
  }

  public TDLambdaAutostep(double lambda, double gamma, int nbFeatures, ATraces prototype) {
    this(lambda, gamma, 1000.0, nbFeatures, prototype);
  }

  public TDLambdaAutostep(double lambda, double gamma, double tau, int nbFeatures, Traces prototype) {
    this.lambda = lambda;
    e = prototype.newTraces(nbFeatures);
    this.gamma = gamma;
    this.tau = tau;
    v = new PVector(nbFeatures);
    alpha = new PVector(nbFeatures);
    alpha.set(0.1);
    h = new PVector(nbFeatures);
    s = new PVector(nbFeatures);
  }

  protected double initEpisode() {
    e.clear();
    v_tp1 = 0;
    return 0;
  }

  @Override
  public double update(RealVector phi_t, RealVector phi_tp1, double r_tp1) {
    if (phi_t == null)
      return initEpisode();
    v_t = v.dotProduct(phi_t);
    v_tp1 = phi_tp1 != null ? v.dotProduct(phi_tp1) : 0.0;
    delta_t = r_tp1 + gamma * v_tp1 - v_t;
    e.update(lambda * gamma, phi_t);
    if (e instanceof SparseVector)
      updateNormalizationAndStepSizeSparse(delta_t, phi_t);
    else
      updateNormalizationAndStepSizeNonSparse(delta_t, phi_t);
    RealVector alphaDeltaE = e.vect().ebeMultiply(alpha).mapMultiplyToSelf(delta_t);
    v.addToSelf(alphaDeltaE);
    h.addToSelf(alphaDeltaE.subtractToSelf(phi_t.ebeMultiply(alpha).ebeMultiplyToSelf(phi_t).ebeMultiplyToSelf(h)));
    return delta_t;
  }

  private void updateNormalizationAndStepSizeSparse(final double delta_t, final RealVector phi_t) {
    SparseVector pe = (SparseVector) e.vect();
    final SparseVector absDeltaEH = (SparseVector) computeAbsDeltaEH(delta_t);
    pe.forEach(new ElementIterator() {
      @Override
      public void element(int i, double peDatai) {
        s.data[i] = Math.max(absDeltaEH.getEntry(i), s.data[i]
            + (alpha.data[i] * phi_t.getEntry(i) * phi_t.getEntry(i) / tau) * (absDeltaEH.getEntry(i) - s.data[i]));
        s.data[i] = s.data[i] == 0 ? 1 : s.data[i];
        alpha.data[i] = alpha.data[i] * Math.exp(Mu * delta_t * peDatai * h.data[i] / s.data[i]);
      }
    });
    m = computeM(phi_t);
    maxOneM2 = Math.max(1, m / 2);
    pe.forEach(new ElementIterator() {
      @Override
      public void element(int index, double value) {
        alpha.data[index] = alpha.data[index] / maxOneM2;
      }
    });
  }

  private RealVector computeAbsDeltaEH(double delta) {
    return e.vect().ebeMultiply(h).mapMultiplyToSelf(delta).mapAbsToSelf();
  }

  private void updateNormalizationAndStepSizeNonSparse(double delta_t, RealVector phi_t) {
    PVector pe = (PVector) e.vect();
    PVector absDeltaEH = (PVector) computeAbsDeltaEH(delta_t);
    for (int i = 0; i < pe.size; i++) {
      s.data[i] = Math.max(absDeltaEH.data[i], s.data[i]
          + (alpha.data[i] * phi_t.getEntry(i) * phi_t.getEntry(i) / tau) * (absDeltaEH.data[i] - s.data[i]));
      s.data[i] = s.data[i] == 0 ? 1 : s.data[i];
      alpha.data[i] = alpha.data[i] * Math.exp(Mu * delta_t * pe.data[i] * h.data[i] / s.data[i]);
    }
    m = computeM(phi_t);
    maxOneM2 = Math.max(1, m / 2);
    for (int i = 0; i < phi_t.getDimension(); i++) {
      alpha.data[i] = alpha.data[i] / maxOneM2;
    }
  }

  private double computeM(RealVector phi_t) {
    return phi_t.ebeMultiply(alpha).dotProduct(phi_t);
  }

  public Traces eligibility() {
    return e;
  }

  @Override
  public double predict(RealVector phi) {
    return v.dotProduct(phi);
  }

  @Override
  public PVector theta() {
    return v;
  }
}
