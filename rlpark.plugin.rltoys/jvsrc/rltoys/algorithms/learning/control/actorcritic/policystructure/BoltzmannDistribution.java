package rltoys.algorithms.learning.control.actorcritic.policystructure;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import rltoys.algorithms.representations.acting.PolicyDistribution;
import rltoys.algorithms.representations.acting.StochasticPolicy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.math.vector.MutableVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class BoltzmannDistribution extends StochasticPolicy implements PolicyDistribution, MonitorContainer {
  private static final long serialVersionUID = 7036360201611314726L;
  final Map<Action, Double> distribution = new LinkedHashMap<Action, Double>();
  private final Map<Action, RealVector> actionToPhi_sa = new LinkedHashMap<Action, RealVector>();
  @Monitor(level = 4)
  private PVector u;
  private RealVector lastS;
  private MutableVector averagePhi;
  private final Action[] actions;
  private final StateToStateAction toStateAction;

  public BoltzmannDistribution(Random random, Action[] actions, StateToStateAction toStateAction) {
    super(random);
    this.actions = actions;
    this.toStateAction = toStateAction;
  }

  @Override
  public double pi(RealVector s, Action a) {
    updateDistributionIFN(s);
    return distribution.get(a);
  }

  private void updateDistributionIFN(RealVector s) {
    if (lastS == s)
      return;
    lastS = s;
    double normalizeSum = 0;
    averagePhi = s.newInstance(u.size);
    for (int a_i = 0; a_i < actions.length; a_i++) {
      RealVector phi_sa = toStateAction.stateAction(s, actions[a_i]);
      double probabilityNotNormalized = Math.exp(u.dotProduct(phi_sa));
      distribution.put(actions[a_i], probabilityNotNormalized);
      normalizeSum += probabilityNotNormalized;
      averagePhi.addToSelf(phi_sa.mapMultiply(probabilityNotNormalized));
      actionToPhi_sa.put(actions[a_i], phi_sa);
    }
    for (Action a : actions)
      distribution.put(a, distribution.get(a) / normalizeSum);
    averagePhi.mapMultiplyToSelf(1 / normalizeSum);
  }

  protected Action initialize() {
    lastS = null;
    return null;
  }

  @Override
  public Action decide(RealVector x_t) {
    if (x_t == null)
      return initialize();
    updateDistributionIFN(x_t);
    return super.chooseAction(distribution);
  }

  @Override
  public PVector[] createParameters(int nbFeatures) {
    u = new PVector(toStateAction.vectorSize());
    return new PVector[] { u };
  }

  @Override
  public RealVector[] getGradLog(RealVector x_t, Action a_t) {
    updateDistributionIFN(x_t);
    lastS = null;
    return new RealVector[] { actionToPhi_sa.get(a_t).subtract(averagePhi) };
  }

  @Override
  public void addToMonitor(DataMonitor monitor) {
    for (Action a : actions) {
      final Action action = a;
      monitor.add(a.toString(), 0, new Monitored() {
        @Override
        public double monitoredValue() {
          return distribution.get(action);
        }
      });
    }
  }
}
