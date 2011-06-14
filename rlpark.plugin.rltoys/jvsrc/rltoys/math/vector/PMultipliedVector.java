package rltoys.math.vector;

import static rltoys.utils.Utils.notSupported;

import java.util.Arrays;

import rltoys.utils.NotImplemented;

public class PMultipliedVector implements RealVector {
  private static final long serialVersionUID = -6965796493743575894L;
  private final PVector pvector;
  private final double constant;

  protected PMultipliedVector(PVector pvector, double constant) {
    this.pvector = pvector;
    this.constant = constant;
  }

  @Override
  public RealVector copy() {
    notSupported();
    return null;
  }

  @Override
  public RealVector addToSelf(RealVector other) {
    notSupported();
    return null;
  }

  @Override
  public RealVector subtractToSelf(RealVector other) {
    return subtract(other);
  }

  @Override
  public RealVector mapMultiply(double d) {
    return new PMultipliedVector(pvector, d * constant);
  }

  @Override
  public RealVector mapMultiplyToSelf(double d) {
    notSupported();
    return null;
  }

  @Override
  public int getDimension() {
    return pvector.getDimension();
  }

  @Override
  public double getEntry(int i) {
    return pvector.getEntry(i) * constant;
  }

  @Override
  public double dotProduct(RealVector other) {
    notSupported();
    return 0;
  }

  @Override
  public void setSubVector(int i, RealVector other) {
    notSupported();

  }

  @Override
  public void set(double d) {
    notSupported();

  }

  @Override
  public RealVector subtract(RealVector other) {
    double[] data = accessData();
    if (other instanceof SparseVector)
      ((SparseVector) other).subtractSelfTo(data);
    else {
      double[] o = other.accessData();
      for (int i = 0; i < o.length; i++)
        data[i] -= o[i];
    }
    return new PVector(data);
  }

  @Override
  public void setEntry(int i, double d) {
    notSupported();

  }

  @Override
  public RealVector add(RealVector other) {
    double[] s = pvector.data;
    double[] o = other.accessData();
    PVector result = new PVector(pvector.size);
    for (int i = 0; i < o.length; i++)
      result.data[i] = s[i] * constant + o[i];
    return result;
  }

  @Override
  public RealVector newInstance(int size) {
    return new PVector(size);
  }

  @Override
  public boolean checkValues() {
    notSupported();
    return false;
  }

  @Override
  public double[] accessData() {
    double[] result = Arrays.copyOf(pvector.data, getDimension());
    for (int i = 0; i < result.length; i++)
      result[i] *= constant;
    return result;
  }

  public void addSelfTo(PVector other) {
    double[] s = pvector.data;
    double[] o = other.data;
    for (int i = 0; i < o.length; i++)
      o[i] += s[i] * constant;
  }

  @Override
  public RealVector getSubVector(int index, int n) {
    double[] data = new double[n];
    for (int i = 0; i < n; i++)
      data[i] = pvector.data[i * n] * constant;
    return new PVector(data);
  }

  @Override
  public String toString() {
    return Arrays.toString(accessData());
  }

  @Override
  public RealVector ebeMultiply(RealVector v) {
    throw new NotImplemented();
  }

  @Override
  public RealVector mapAbsToSelf() {
    throw new NotImplemented();
  }

  @Override
  public RealVector ebeMultiplyToSelf(RealVector v) {
    throw new NotImplemented();
  }
}
