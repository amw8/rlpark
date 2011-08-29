package rltoys.environments.envio.problems;

import rltoys.math.ranges.Range;

public interface ProblemBounded extends RLProblem {
  Range[] getObservationRanges();
}
