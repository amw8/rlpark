package rltoys.experiments.parametersweep.onpolicy;

import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.Runner;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.interfaces.AgentFactory;
import rltoys.experiments.parametersweep.interfaces.ProblemFactory;
import rltoys.experiments.parametersweep.onpolicy.internal.ContextOnPolicy;
import rltoys.experiments.parametersweep.onpolicy.internal.RewardMonitor;
import rltoys.experiments.parametersweep.parameters.Parameters;

public abstract class AbstractContextOnPolicy implements ContextOnPolicy {
  private static final long serialVersionUID = -6212106048889219995L;
  private final AgentFactory agentFactory;
  private final ProblemFactory environmentFactory;

  public AbstractContextOnPolicy(ProblemFactory environmentFactory, AgentFactory agentFactory) {
    this.environmentFactory = environmentFactory;
    this.agentFactory = agentFactory;
  }

  @Override
  public Runner createRunner(int counter, Parameters parameters) {
    RLProblem problem = environmentFactory.createEnvironment(ExperimentCounter.newRandom(counter));
    RLAgent agent = agentFactory.createAgent(problem, parameters, ExperimentCounter.newRandom(counter));
    int nbEpisode = parameters.nbEpisode();
    int maxEpisodeTimeSteps = parameters.maxEpisodeTimeSteps();
    return new Runner(problem, agent, nbEpisode, maxEpisodeTimeSteps);
  }

  @Override
  public String fileName() {
    return ExperimentCounter.DefaultFileName;
  }

  @Override
  public String folderPath() {
    return environmentFactory.label() + "/" + agentFactory.label();
  }

  public AgentFactory agentFactory() {
    return agentFactory;
  }

  public ProblemFactory problemFactory() {
    return environmentFactory;
  }

  public Parameters contextParameters() {
    Parameters parameters = new Parameters();
    environmentFactory.setExperimentParameters(parameters);
    parameters.enableFlag(agentFactory.label());
    parameters.enableFlag(environmentFactory.label());
    return parameters;
  }

  @Override
  public RewardMonitor createRewardMonitor(Parameters parameters) {
    return null;
  }
}
