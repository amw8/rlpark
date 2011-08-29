package rltoys.experiments.parametersweep.onpolicy;

import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.interfaces.AgentFactory;
import rltoys.experiments.parametersweep.interfaces.ProblemFactory;
import rltoys.experiments.parametersweep.onpolicy.internal.LearningCurveJob;
import rltoys.experiments.parametersweep.parameters.Parameters;

public class ContextLearningCurve extends AbstractContextOnPolicy {
  private static final long serialVersionUID = -5926779335932073094L;

  public ContextLearningCurve(ProblemFactory environmentFactory, AgentFactory agentFactory) {
    super(environmentFactory, agentFactory);
  }

  @Override
  public LearningCurveJob createJob(Parameters parameters, ExperimentCounter counter) {
    return new LearningCurveJob(this, parameters, counter);
  }
}
