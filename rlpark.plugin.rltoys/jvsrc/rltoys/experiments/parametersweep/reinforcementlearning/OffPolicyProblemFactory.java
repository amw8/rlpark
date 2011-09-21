package rltoys.experiments.parametersweep.reinforcementlearning;

import java.util.Random;

import rltoys.algorithms.representations.acting.Policy;
import rltoys.environments.envio.problems.RLProblem;

public interface OffPolicyProblemFactory extends ProblemFactory {
  Policy createBehaviourPolicy(RLProblem problem, Random random);
}
