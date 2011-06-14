package rltoys.algorithms.representations.tilescoding.hashing;

import java.io.Serializable;

public interface Hashing extends Serializable {
  int memorySize();

  int hash(Tiling tiling, int[] inputs);
}
