package rltoys.algorithms.representations.ltu;

import java.util.HashSet;
import java.util.Set;


public class LTUConst implements LTU {
  private static final long serialVersionUID = -4023678527356755535L;
  private final int index;
  private final boolean isActive;

  public LTUConst(int index) {
    this(index, true);
  }

  public LTUConst(int index, boolean isActive) {
    this.index = index;
    this.isActive = isActive;
  }

  @Override
  public int index() {
    return index;
  }

  @Override
  public Set<Integer> inputs() {
    return new HashSet<Integer>();
  }

  @Override
  public void setActiveInput(int activeInput) {
  }

  @Override
  public void update() {
  }

  @Override
  public boolean isActive() {
    return isActive;
  }

  @Override
  public LTU newLTU(int ltuIndex, int[] inputs, byte[] weights) {
    throw new RuntimeException("Are you sure you want to clone that?");
  }
}
