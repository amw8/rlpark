package rltoys.math.vector;

public interface BinaryVector extends SparseVector {
  @Override
  BinaryVector copy();

  void setOn(int i);

  int[] activeIndexes();
}
