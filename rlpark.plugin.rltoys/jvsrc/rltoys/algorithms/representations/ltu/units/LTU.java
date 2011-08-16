package rltoys.algorithms.representations.ltu.units;

import java.io.Serializable;

public interface LTU extends Serializable {
  int index();

  int[] inputs();

  boolean update(int time, double[] inputVector);

  LTU newLTU(int ltuIndex, int[] inputs, byte[] weights);

  boolean isActive();
}
