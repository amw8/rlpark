package rltoys.algorithms.representations.tilescoding.hashing;

import rltoys.algorithms.representations.discretizer.Discretizer;

public class Identity implements Hashing {
  private static final long serialVersionUID = -3659050449466106614L;
  private final int memorySize;

  public Identity(Tiling tilings) {
    memorySize = computeMemorySize(tilings);
  }

  private int computeMemorySize(Tiling tilings) {
    int memorySize = 1;
    for (Discretizer discretizer : tilings.discretizers())
      memorySize *= discretizer.resolution();
    return memorySize;
  }

  @Override
  public int hash(Tiling tiling, int[] inputs) {
    int activatedIndex = 0;
    Discretizer[] discretizers = tiling.discretizers();
    for (int i = 0; i < discretizers.length; i++) {
      Discretizer partition = discretizers[i];
      activatedIndex += inputs[i] * Math.pow(partition.resolution(), i);
    }
    return activatedIndex;
  }

  @Override
  public int memorySize() {
    return memorySize;
  }
}
