package rltoys.math.vector;

public interface BinaryVector extends SparseVector, Iterable<Integer> {
  @Override
  BinaryVector copy();

  void clear();

  void setOn(int i);

  void setOn(int[] indexes);

  int[] activeIndexes();
}
