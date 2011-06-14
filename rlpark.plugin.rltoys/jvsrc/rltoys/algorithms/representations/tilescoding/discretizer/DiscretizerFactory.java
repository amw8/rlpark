package rltoys.algorithms.representations.tilescoding.discretizer;

import java.io.Serializable;

public interface DiscretizerFactory extends Serializable {
  Discretizer createDiscretizer(int inputIndex, int resolution, int tilingIndex, int nbTilings);
}
