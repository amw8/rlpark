package rltoys.math.vector;

public class Vectors {
  static public boolean equals(RealVector a, RealVector b) {
    if (a == b)
      return true;
    if (a != null && b == null || a == null && b != null)
      return false;
    if (a.getDimension() != b.getDimension())
      return false;
    for (int i = 0; i < a.getDimension(); ++i)
      if (a.getEntry(i) != b.getEntry(i))
        return false;
    return true;
  }
}
