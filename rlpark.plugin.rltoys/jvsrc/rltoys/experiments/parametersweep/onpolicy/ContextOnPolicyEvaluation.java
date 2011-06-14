package rltoys.experiments.parametersweep.onpolicy;

import rltoys.environments.envio.Agent;
import rltoys.environments.envio.RLProblem;
import rltoys.environments.envio.Runner;
import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.interfaces.Context;
import rltoys.experiments.parametersweep.parameters.Parameters;

public class ContextOnPolicyEvaluation implements Context {
  private static final long serialVersionUID = -6212106048889219995L;
  private final AgentFactory agentFactory;
  private final ProblemFactory environmentFactory;

  public ContextOnPolicyEvaluation(ProblemFactory environmentFactory, AgentFactory agentFactory) {
    this.environmentFactory = environmentFactory;
    this.agentFactory = agentFactory;
  }

  public Runner createRunner(ExperimentCounter counter, Parameters parameters) {
    RLProblem problem = environmentFactory.createEnvironment(counter.newRandom());
    Agent agent = agentFactory.createAgent(problem, parameters, counter.newRandom());
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

  @Override
  public SweepJob createSweepJob(Parameters parameters, ExperimentCounter counter) {
    return new SweepJob(this, parameters, counter);
  }

  @Override
  public LearningCurveJob createLearningCurveJob(Parameters parameters, ExperimentCounter counter) {
    return new LearningCurveJob(this, parameters, counter);
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
}
