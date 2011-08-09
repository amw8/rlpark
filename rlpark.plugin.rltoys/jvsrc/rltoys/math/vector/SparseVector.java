package rltoys.math.vector;

public interface SparseVector extends RealVector {
  public interface ElementIterator {
    void element(int index, double value);
  };

  double dotProduct(double[] data);

  void addSelfTo(double[] data);

  void subtractSelfTo(double[] data);

  void subtractSelfTo(SparseRealVector other);

  int nonZeroElements();

  void forEach(ElementIterator elementIterator);
}
