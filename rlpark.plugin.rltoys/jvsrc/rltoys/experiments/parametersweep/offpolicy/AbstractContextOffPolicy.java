package rltoys.experiments.parametersweep.offpolicy;

import rltoys.algorithms.representations.Projector;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.Runner;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.offpolicy.evaluation.OffPolicyEvaluation;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.parameters.RunInfo;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyAgentFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.ProjectorFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.ReinforcementLearningContext;

public abstract class AbstractContextOffPolicy implements ReinforcementLearningContext {
  private static final long serialVersionUID = -6212106048889219995L;
  private final OffPolicyAgentFactory agentFactory;
  protected final ProblemFactory environmentFactory;
  protected final OffPolicyEvaluation evaluation;
  protected final ProjectorFactory projectorFactory;

  public AbstractContextOffPolicy(ProblemFactory environmentFactory, ProjectorFactory projectorFactory,
      OffPolicyAgentFactory agentFactory, OffPolicyEvaluation evaluation) {
    this.evaluation = evaluation;
    this.projectorFactory = projectorFactory;
    this.environmentFactory = environmentFactory;
    this.agentFactory = agentFactory;
  }

  @Override
  public Runner createRunner(int seed, Parameters parameters) {
    RLProblem problem = environmentFactory.createEnvironment(ExperimentCounter.newRandom(seed));
    Projector projector = projectorFactory.createProjector(problem);
    Policy behaviourPolicy = agentFactory.createBehaviourPolicy(problem, ExperimentCounter.newRandom(seed));
    RLAgent agent = agentFactory.createAgent(problem, projector, parameters, behaviourPolicy,
                                             ExperimentCounter.newRandom(seed));
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

  public OffPolicyAgentFactory agentFactory() {
    return agentFactory;
  }

  public ProblemFactory problemFactory() {
    return environmentFactory;
  }

  public Parameters contextParameters() {
    RunInfo infos = new RunInfo();
    infos.enableFlag(agentFactory.label());
    infos.enableFlag(environmentFactory.label());
    Parameters parameters = new Parameters(infos);
    environmentFactory.setExperimentParameters(parameters);
    return parameters;
  }
}
