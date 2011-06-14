package rltoys.algorithms.learning.control.actorcritic.policystructure;

import static rltoys.utils.Utils.square;

import java.util.Random;

import rltoys.algorithms.representations.acting.PolicyDistribution;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.math.vector.PVector;
import rltoys.math.vector.RealVector;
import rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.wrappers.Squared;
import zephyr.plugin.core.api.parsing.LabeledCollection;

public class NormalDistribution implements PolicyDistribution, LabeledCollection {
  private static final long serialVersionUID = -4074721193363280217L;
  private final double initialMean;
  private final double logInitialStddev;
  @Monitor(level = 4)
  private PVector u_mean;
  @Monitor(level = 4)
  private PVector u_stddev;
  @Monitor(wrappers = { Squared.ID })
  protected double mean;
  @Monitor
  protected double stddev;
  private final Random random;
  protected RealVector lastX;
  @Monitor
  public double a_t;
  protected final double stddevGradientFactor;

  public NormalDistribution(Random random, double mean, double sigma) {
    this(random, mean, sigma, 1.0);
  }

  public NormalDistribution(Random random, double mean, double sigma, double stddevGradientFactor) {
    initialMean = mean;
    logInitialStddev = Math.log(sigma);
    this.mean = initialMean;
    this.stddev = sigma;
    this.random = random;
    this.stddevGradientFactor = stddevGradientFactor;
  }

  @Override
  public PVector[] createParameters(int nbFeatures) {
    u_mean = new PVector(nbFeatures);
    u_stddev = new PVector(nbFeatures);
    return new PVector[] { u_mean, u_stddev };
  }

  protected Action initialize() {
    lastX = null;
    return null;
  }

  @Override
  public RealVector[] getGradLog(RealVector x_t, Action a_t) {
    updateDistributionIFN(x_t);
    double sigma2 = square(stddev);
    double a = ((ActionArray) a_t).actions[0];
    RealVector meanGradient = x_t.mapMultiply(1.0 / sigma2 * (a - mean));
    RealVector stddevGradient = x_t.mapMultiply(stddevGradientFactor * (square(a - mean) / sigma2 - 1));
    lastX = null;
    return new RealVector[] { meanGradient, stddevGradient };
  }

  @Override
  public Action decide(RealVector x_t) {
    if (x_t == null)
      return initialize();
    updateDistributionIFN(x_t);
    a_t = random.nextGaussian() * stddev + mean;
    if (!Utils.checkValue(a_t))
      return null;
    return new ActionArray(a_t);
  }

  protected void updateDistributionIFN(RealVector x) {
    if (lastX == x)
      return;
    lastX = x;
    if (lastX == null)
      return;
    mean = u_mean.dotProduct(x) + initialMean;
    stddev = Math.exp(u_stddev.dotProduct(x) + logInitialStddev);
  }

  public double stddev() {
    return stddev;
  }

  public double mean() {
    return mean;
  }

  public double pi_s(double a) {
    double sigma2 = stddev * stddev;
    double ammu2 = (a - mean) * (a - mean);
    return Math.exp(-ammu2 / (2 * sigma2)) / Math.sqrt(2 * Math.PI * sigma2);
  }

  @Override
  public double pi(RealVector s, Action a) {
    updateDistributionIFN(s);
    return pi_s(((ActionArray) a).actions[0]);
  }

  @Override
  public String label(int index) {
    return index == 0 ? "mean" : "stddev";
  }
}
