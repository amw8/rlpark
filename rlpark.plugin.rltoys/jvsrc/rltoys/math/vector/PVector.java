package rltoys.math.vector;

import static rltoys.utils.Utils.notImplemented;

import java.util.Arrays;

import rltoys.math.vector.SparseVector.ElementIterator;
import rltoys.utils.NotImplemented;
import rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;


@Monitor
public class PVector implements RealVector {
  private static final long serialVersionUID = -3114589590234820246L;

  final public int size;
  final public double[] data;

  public PVector(int m) {
    size = m;
    data = new double[m];
  }

  public PVector(double... v) {
    this(v, false);
  }

  public PVector(double[] d, boolean copyArray) {
    assert d != null && d.length > 0;
    data = copyArray ? d.clone() : d;
    size = data.length;
  }

  public PVector(RealVector v) {
    data = new double[v.getDimension()];
    for (int i = 0; i < data.length; ++i)
      data[i] = v.getEntry(i);
    size = data.length;
  }

  public PVector set(RealVector other) {
    if (other instanceof PVector)
      return set(((PVector) other).data);
    if (other instanceof SparseVector) {
      set(0);
      ((SparseVector) other).forEach(new ElementIterator() {
        @Override
        public void element(int index, double value) {
          data[index] = value;
        }
      });
      return this;
    }
    throw new NotImplemented();
  }

  public PVector set(double[] v) {
    System.arraycopy(v, 0, data, 0, size);
    return this;
  }

  protected PVector newInstance(double[] data) {
    return new PVector(data, false);
  }

  @Override
  public PVector copy() {
    PVector result = (PVector) newInstance(size);
    result.set(data);
    return result;
  }

  @Override
  public RealVector add(RealVector other) {
    return copy().addToSelf(other);
  }

  @Override
  public PVector subtractToSelf(RealVector other) {
    if (other instanceof SparseVector) {
      ((SparseVector) other).subtractSelfTo(data);
      return this;
    }
    double[] o = other.accessData();
    for (int i = 0; i < data.length; i++)
      data[i] -= o[i];
    return this;
  }

  @Override
  public RealVector addToSelf(RealVector other) {
    if (other instanceof SparseVector) {
      ((SparseVector) other).addSelfTo(data);
      return this;
    }
    if (other instanceof PMultipliedVector) {
      ((PMultipliedVector) other).addSelfTo(this);
      return this;
    }
    double[] o = other.accessData();
    for (int i = 0; i < data.length; i++)
      data[i] += o[i];
    return this;
  }

  @Override
  public double dotProduct(RealVector other) {
    if (other instanceof SparseVector)
      return ((SparseVector) other).dotProduct(data);
    double result = 0;
    double[] o = ((PVector) other).data;
    for (int i = 0; i < data.length; i++)
      result += data[i] * o[i];
    return result;
  }

  @Override
  public int getDimension() {
    return size;
  }

  @Override
  public double getEntry(int i) {
    return data[i];
  }

  @Override
  public RealVector newInstance(int size) {
    return new PVector(size);
  }

  @Override
  public void set(double d) {
    Arrays.fill(data, d);
  }

  @Override
  public void setEntry(int i, double d) {
    data[i] = d;
  }

  @Override
  public RealVector mapMultiply(double d) {
    return new PMultipliedVector(this, d);
  }

  @Override
  public RealVector mapMultiplyToSelf(double d) {
    for (int i = 0; i < data.length; i++)
      data[i] *= d;
    return this;
  }

  @Override
  public void setSubVector(int i, RealVector other) {
    System.arraycopy(other.accessData(), 0, data, i, other.getDimension());
  }

  public void setSubVector(int i, double[] other) {
    System.arraycopy(other, 0, data, i, other.length);
  }

  @Override
  public PVector subtract(RealVector other) {
    return copy().subtractToSelf(other);
  }

  @Override
  public boolean checkValues() {
    for (int i = 0; i < data.length; i++)
      if (!Utils.checkValue(data[i]))
        return false;
    return true;
  }

  @Override
  public double[] accessData() {
    return data;
  }

  @Override
  public RealVector getSubVector(int index, int n) {
    notImplemented();
    return null;
  }

  public void addToSelf(double[] array) {
    assert array.length == size;
    for (int i = 0; i < array.length; i++)
      data[i] += array[i];
  }

  @Override
  public RealVector ebeMultiply(RealVector v) {
    return copy().ebeMultiplyToSelf(v);
  }

  @Override
  public RealVector mapAbsToSelf() {
    for (int i = 0; i < data.length; i++)
      data[i] = Math.abs(data[i]);
    return this;
  }

  @Override
  public RealVector ebeMultiplyToSelf(RealVector v) {
    if (v instanceof PVector)
      return ebeMultiplyToSelf(((PVector) v).data);
    throw new NotImplemented();
  }

  private RealVector ebeMultiplyToSelf(double[] other) {
    for (int i = 0; i < other.length; i++)
      data[i] *= other[i];
    return this;
  }
}