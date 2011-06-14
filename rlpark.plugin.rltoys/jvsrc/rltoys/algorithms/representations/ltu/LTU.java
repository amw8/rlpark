package rltoys.algorithms.representations.ltu;

import java.io.Serializable;
import java.util.Set;

public interface LTU extends Serializable {
  int index();

  Set<Integer> inputs();

  void setActiveInput(int activeInput);

  void update();

  boolean isActive();

  LTU newLTU(int ltuIndex, int[] inputs, byte[] weights);
}
