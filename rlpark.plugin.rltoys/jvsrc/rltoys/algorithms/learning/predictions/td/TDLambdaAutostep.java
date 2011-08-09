package rltoys.algorithms.learning.predictions.td;

import rltoys.algorithms.representations.traces.PATraces;
import rltoys.algorithms.representations.traces.Traces;
import rltoys.math.vector.ModifiableVector;
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
  protected double mu = 0.01;
  protected double tau = 1000;
  @Monitor(level = 4)
  final private PVector v;
  private double v_t;
  private double v_tp1;
  @Monitor(wrappers = { Squared.ID, Abs.ID })
  private double delta_t;

  final protected Traces e;
  @Monitor(level = 4)
  final protected PVector alpha;
  @Monitor(level = 4)
  final protected PVector h;
  @Monitor(level = 4)
  final protected PVector normalizer;
  protected double maxOneM2;
  private final double gamma;
  private final double lambda;
  private double m;
  double tempM = 0.0;

  public TDLambdaAutostep(double lambda, double gamma, int nbFeatures) {
    this(lambda, gamma, 0.1, nbFeatures, new PATraces());
  }

  public TDLambdaAutostep(double lambda, double gamma, int nbFeatures, Traces prototype) {
    this(lambda, gamma, 0.1, nbFeatures, prototype);
  }

  public TDLambdaAutostep(double lambda, double gamma, double initAlpha, int nbFeatures) {
    this(lambda, gamma, initAlpha, nbFeatures, new PATraces());
  }

  public TDLambdaAutostep(double lambda, double gamma, double initAlpha, int nbFeatures, Traces prototype) {
    this.lambda = lambda;
    e = prototype.newTraces(nbFeatures);
    this.gamma = gamma;
    v = new PVector(nbFeatures);
    alpha = new PVector(nbFeatures);
    alpha.set(initAlpha);
    h = new PVector(nbFeatures);
    normalizer = new PVector(nbFeatures);
    normalizer.set(1.0);
  }

  public void setMu(double mu) {
    this.mu = mu;
  }

  public void setTau(double tau) {
    this.tau = tau;
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
    if (e.vect() instanceof SparseVector)
      updateNormalizationAndStepSizeSparse(delta_t, phi_t);
    else
      updateNormalizationAndStepSizeNonSparse(delta_t, phi_t);
    ModifiableVector alphaDeltaE = e.vect().ebeMultiply(alpha).mapMultiplyToSelf(delta_t);
    v.addToSelf(alphaDeltaE);
    h.addToSelf(alphaDeltaE.subtractToSelf(phi_t.ebeMultiply(alpha).ebeMultiplyToSelf(e.vect()).mapAbsToSelf()
        .ebeMultiplyToSelf(h)));
    return delta_t;
  }

  private void updateNormalizationAndStepSizeSparse(final double delta_t, final RealVector phi_t) {
    SparseVector pe = (SparseVector) e.vect();
    final SparseVector absDeltaEH = (SparseVector) computeAbsDeltaEH(delta_t);
    pe.forEach(new ElementIterator() {
      @Override
      public void element(int i, double peDatai) {
        normalizer.data[i] = Math.max(
                                      absDeltaEH.getEntry(i),
                                      normalizer.data[i]
                                          + (alpha.data[i] * Math.abs(peDatai * phi_t.getEntry(i)) / tau)
                                          * (absDeltaEH.getEntry(i) - normalizer.data[i]));
        normalizer.data[i] = Math.max(Math.pow(10.0, -10) / phi_t.getDimension(), normalizer.data[i]);
        alpha.data[i] = alpha.data[i] * Math.exp(mu * delta_t * peDatai * h.data[i] / normalizer.data[i]);
        alpha.data[i] = Math.max(Math.pow(10.0, -10) / phi_t.getDimension(), alpha.data[i]);
      }
    });
    tempM = 0.0;
    pe.forEach(new ElementIterator() {
      @Override
      public void element(int index, double peDatai) {
        tempM += alpha.data[index] * Math.abs(peDatai * phi_t.getEntry(index));
      }
    });
    m = tempM;
    maxOneM2 = Math.max(1, m);
    pe.forEach(new ElementIterator() {
      @Override
      public void element(int index, double value) {
        if (phi_t.getEntry(index) != 0)
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
      normalizer.data[i] = Math.max(absDeltaEH.data[i],
                                    normalizer.data[i]
                                        + (alpha.data[i] * Math.abs(pe.data[i] * phi_t.getEntry(i)) / tau)
                                        * (absDeltaEH.data[i] - normalizer.data[i]));
      normalizer.data[i] = Math.max(Math.pow(10.0, -10) / phi_t.getDimension(), normalizer.data[i]);
      alpha.data[i] = alpha.data[i] * Math.exp(mu * delta_t * pe.data[i] * h.data[i] / normalizer.data[i]);
      alpha.data[i] = Math.max(Math.pow(10.0, -10) / phi_t.getDimension(), alpha.data[i]);
    }
    tempM = 0.0;
    for (int i = 0; i < pe.size; i++) {
      tempM += alpha.data[i] * Math.abs(pe.data[i] * phi_t.getEntry(i));
    }
    m = tempM;
    maxOneM2 = Math.max(1, m);
    for (int i = 0; i < pe.size; i++) {
      if (phi_t.getEntry(i) * pe.data[i] != 0)
        alpha.data[i] = alpha.data[i] / maxOneM2;
    }
  }

  public Traces eligibility() {
    return e;
  }

  @Override
  public double predict(RealVector phi) {
    return v.dotProduct(phi);
  }

  @Override
  public PVector weights() {
    return v;
  }

  @Override
  public void resetWeight(int index) {
    v.data[index] = 0;
    alpha.data[index] = .1;
    h.data[index] = 0;
    normalizer.data[index] = 0;
  }

  @Override
  public double error() {
    return delta_t;
  }

  @Override
  public double prediction() {
    return v_t;
  }

  public double gamma() {
    return gamma;
  }
}
