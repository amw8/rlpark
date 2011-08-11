package rltoys.algorithms.learning.predictions.td;

import rltoys.algorithms.representations.traces.ATraces;
import rltoys.algorithms.representations.traces.Traces;
import rltoys.math.vector.MutableVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.VectorEntry;
import rltoys.math.vector.implementations.PVector;
import rltoys.math.vector.implementations.Vectors;
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
  private final double lowerNumericalBound;

  public TDLambdaAutostep(double lambda, double gamma, int nbFeatures) {
    this(lambda, gamma, 0.1, nbFeatures, new ATraces());
  }

  public TDLambdaAutostep(double lambda, double gamma, int nbFeatures, Traces prototype) {
    this(lambda, gamma, 0.1, nbFeatures, prototype);
  }

  public TDLambdaAutostep(double lambda, double gamma, double initAlpha, int nbFeatures) {
    this(lambda, gamma, initAlpha, nbFeatures, new ATraces());
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
    lowerNumericalBound = Math.pow(10.0, -10) / nbFeatures;
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
    updateNormalizationAndStepSize(delta_t, phi_t);
    MutableVector eAlpha = e.vect().ebeMultiply(alpha);
    MutableVector alphaDeltaE = eAlpha.mapMultiply(delta_t);
    v.addToSelf(alphaDeltaE);
    h.addToSelf(alphaDeltaE.subtractToSelf(Vectors.absToSelf(eAlpha.ebeMultiplyToSelf(phi_t)).ebeMultiplyToSelf(h)));
    return delta_t;
  }

  private void updateNormalizationAndStepSize(final double delta_t, final RealVector phi) {
    final double[] normalizerData = normalizer.data;
    final double[] alphaData = alpha.data;
    final double[] densePhi = phi.accessData();
    for (VectorEntry entry : e.vect()) {
      int i = entry.index();
      double e_i = entry.value();
      double absDeltaEH = computeAbsDeltaEH(i, e_i, delta_t);
      normalizerData[i] = Math.max(absDeltaEH,
                                   normalizerData[i]
                                       + (alphaData[i] * Math.abs(e_i * densePhi[i]) / tau)
                                       * (absDeltaEH - normalizerData[i]));
      normalizerData[i] = Math.max(lowerNumericalBound, normalizerData[i]);
      alphaData[i] = alphaData[i] * Math.exp(mu * delta_t * e_i * h.data[i] / normalizerData[i]);
      alphaData[i] = Math.max(lowerNumericalBound, alphaData[i]);
    }
    tempM = 0.0;
    for (VectorEntry entry : e.vect()) {
      int index = entry.index();
      double peDatai = entry.value();
      tempM += alphaData[index] * Math.abs(peDatai * densePhi[index]);
    }
    m = tempM;
    maxOneM2 = Math.max(1, m);
    for (VectorEntry entry : e.vect()) {
      int index = entry.index();
      if (densePhi[index] != 0)
        alphaData[index] = alphaData[index] / maxOneM2;
    }
  }

  private double computeAbsDeltaEH(int index, double traceValue, double delta) {
    return Math.abs(traceValue * h.data[index] * delta);
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
