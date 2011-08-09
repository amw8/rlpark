package rltoys.math.vector;

public interface DenseVector extends MutableVector {
  double[] accessData();

  void set(double value);
}
