package rltoys.algorithms.learning.control.actorcritic.onpolicy;

import rltoys.algorithms.representations.acting.PolicyDistribution;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.traces.ATraces;
import rltoys.algorithms.representations.traces.Traces;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.parsing.LabelProvider;

@Monitor
public class ActorLambda extends Actor {
  private static final long serialVersionUID = -1601184295976574511L;
  public final Traces[] e_u;
  private final double lambda;

  public ActorLambda(double lambda, PolicyDistribution policyDistribution, double alpha_u, int nbFeatures) {
    this(lambda, policyDistribution, alpha_u, nbFeatures, new ATraces());
  }

  public ActorLambda(double lambda, PolicyDistribution policyDistribution, double alpha_u, int nbFeatures,
      Traces prototype) {
    super(policyDistribution, alpha_u, nbFeatures);
    this.lambda = lambda;
    e_u = new Traces[u.length];
    for (int i = 0; i < e_u.length; i++)
      e_u[i] = prototype.newTraces(u[0].size);
  }

  @Override
  public void update(RealVector x_t, Action a_t, double delta) {
    if (x_t == null) {
      initEpisode();
      return;
    }
    RealVector[] gradLog = policyDistribution.getGradLog(x_t, a_t);
    for (int i = 0; i < u.length; i++)
      e_u[i].update(lambda, gradLog[i]);
    updatePolicyParameters(delta);
  }

  protected void updatePolicyParameters(double delta) {
    for (int i = 0; i < u.length; i++)
      u[i].addToSelf(e_u[i].vect().mapMultiply(alpha_u * delta));
  }

  private void initEpisode() {
    for (Traces e : e_u)
      e.clear();
  }

  @LabelProvider(ids = { "e_u" })
  String eligiblityLabelOf(int index) {
    return super.labelOf(index);
  }
}
