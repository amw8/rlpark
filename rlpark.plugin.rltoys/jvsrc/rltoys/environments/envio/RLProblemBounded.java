package rltoys.environments.envio;

import rltoys.math.ranges.Range;

public interface RLProblemBounded extends RLProblem {
  Range[] getObservationRanges();
}
