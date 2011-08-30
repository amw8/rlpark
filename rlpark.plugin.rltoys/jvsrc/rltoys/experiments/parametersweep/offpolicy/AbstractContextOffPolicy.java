package rltoys.experiments.parametersweep.offpolicy;

import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.Runner;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.interfaces.AgentFactory;
import rltoys.experiments.parametersweep.interfaces.ProblemFactory;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.ReinforcementLearningContext;

public abstract class AbstractContextOffPolicy implements ReinforcementLearningContext {
  public static final String MaxTimeStepsOffPolicyEval = "MaxTimeStepsOffPolicyEval";
  private static final long serialVersionUID = -6212106048889219995L;
  private final AgentFactory agentFactory;
  private final ProblemFactory environmentFactory;
  private final int maxTimeStepsOffPolicyEval;

  public AbstractContextOffPolicy(ProblemFactory environmentFactory, AgentFactory agentFactory,
      int maxTimeStepsOffPolicyEval) {
    this.environmentFactory = environmentFactory;
    this.agentFactory = agentFactory;
    this.maxTimeStepsOffPolicyEval = maxTimeStepsOffPolicyEval;
  }

  @Override
  public Runner createRunner(int counter, Parameters parameters) {
    RLProblem problem = createEnvironment(counter);
    RLAgent agent = agentFactory.createAgent(problem, parameters, ExperimentCounter.newRandom(counter));
    int nbEpisode = parameters.nbEpisode();
    int maxEpisodeTimeSteps = parameters.maxEpisodeTimeSteps();
    return new Runner(problem, agent, nbEpisode, maxEpisodeTimeSteps);
  }

  protected RLProblem createEnvironment(int counter) {
    return environmentFactory.createEnvironment(ExperimentCounter.newRandom(counter));
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
    parameters.put(MaxTimeStepsOffPolicyEval, maxTimeStepsOffPolicyEval);
    parameters.enableFlag(agentFactory.label());
    parameters.enableFlag(environmentFactory.label());
    return parameters;
  }
}
