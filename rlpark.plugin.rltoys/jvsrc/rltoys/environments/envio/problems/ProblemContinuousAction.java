package rltoys.environments.envio.problems;

import rltoys.math.ranges.Range;

public interface ProblemContinuousAction extends RLProblem {
  Range[] actionRanges();
}
