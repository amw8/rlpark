package rltoys.algorithms.learning.control.actorcritic.policystructure;

import java.util.Random;

import rltoys.algorithms.representations.acting.PolicyDistribution;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.math.ranges.Range;
import rltoys.math.vector.PVector;
import rltoys.math.vector.RealVector;

public class UniformDistribution implements PolicyDistribution {
  private static final long serialVersionUID = 7284864369595009279L;
  private final Random random;
  private final Range range;
  private final double pdfValue;

  public UniformDistribution(Random random, Range range) {
    this.random = random;
    this.range = range;
    pdfValue = 1.0 / range.length();
  }

  @Override
  public PVector[] createParameters(int nbFeatures) {
    return new PVector[] { new PVector(1) };
  }

  @Override
  public RealVector[] getGradLog(RealVector x_t, Action a_t) {
    return new PVector[] { new PVector(1) };
  }

  @Override
  public Action decide(RealVector x) {
    return new ActionArray(range.choose(random));
  }

  @Override
  public double pi(RealVector s, Action action) {
    double a = ((ActionArray) action).actions[0];
    return range.in(a) ? pdfValue : 0;
  }
}
