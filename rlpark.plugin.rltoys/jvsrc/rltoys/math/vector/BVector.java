package rltoys.math.vector;

import static rltoys.utils.Utils.notImplemented;
import static rltoys.utils.Utils.notSupported;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import rltoys.utils.NotImplemented;
import rltoys.utils.Utils;


public class BVector implements BinaryVector {

  private static final long serialVersionUID = -1323411391405098702L;
  public final int size;
  public final Set<Integer> indexes = new LinkedHashSet<Integer>();

  public BVector(int size) {
    this.size = size;
  }

  public BVector(int size, int[] indexes) {
    this(size);
    set(indexes);
  }

  public BVector(BVector v) {
    this(v.size);
    indexes.addAll(v.indexes);
  }

  @Override
  public void addSelfTo(double[] data) {
    for (Integer i : indexes)
      data[i] += 1;
  }

  @Override
  public double dotProduct(double[] data) {
    double result = 0.0;
    for (Integer i : indexes)
      result += data[i];
    return result;
  }

  @Override
  public void subtractSelfTo(double[] data) {
    for (Integer i : indexes)
      data[i] -= 1;
  }

  @Override
  public void subtractSelfTo(SparseVector other) {
    for (Integer i : indexes)
      other.setEntry(i, other.getEntry(i) - 1);
  }

  @Override
  public double[] accessData() {
    double[] result = new double[size];
    for (Integer i : indexes)
      result[i] = 1;
    return result;
  }

  protected SVector copyAsSVector() {
    return new SVector(this);
  }

  @Override
  public RealVector add(RealVector other) {
    return other.copy().addToSelf(this);
  }

  @Override
  public RealVector addToSelf(RealVector other) {
    notSupported();
    return null;
  }

  @Override
  public boolean checkValues() {
    return true;
  }

  @Override
  public BVector copy() {
    BVector result = newInstance(size);
    result.indexes.addAll(indexes);
    return result;
  }

  @Override
  public double dotProduct(RealVector other) {
    double result = 0;
    for (Integer i : indexes)
      result += other.getEntry(i);
    return result;
  }

  @Override
  public int getDimension() {
    return size;
  }

  @Override
  public double getEntry(int i) {
    return indexes.contains(i) ? 1 : 0;
  }

  @Override
  public RealVector mapMultiply(double d) {
    return new BConstantVector(this, d);
  }

  @Override
  public RealVector mapMultiplyToSelf(double d) {
    notSupported();
    return null;
  }

  @Override
  public BVector newInstance(int size) {
    return new BVector(size);
  }

  @Override
  public void set(double d) {
    notSupported();
  }

  @Override
  public void setEntry(int i, double d) {
    notSupported();
  }

  @Override
  public void setSubVector(int i, RealVector other) {
    if (!(other instanceof BVector))
      notSupported();
    BVector bother = (BVector) other;
    for (Integer j : new HashSet<Integer>(indexes))
      if (j >= i && j < i + bother.size)
        indexes.remove(j);
    mergeSubVector(i, bother);
  }

  public void mergeSubVector(int i, BVector other) {
    assert i + other.size <= size;
    for (Integer j : other.indexes) {
      assert j + i < size;
      indexes.add(j + i);
    }
  }

  @Override
  public RealVector subtract(RealVector other) {
    return copyAsSVector().subtractToSelf(other);
  }

  @Override
  public RealVector subtractToSelf(RealVector other) {
    notSupported();
    return null;
  }

  @Override
  public void clear() {
    indexes.clear();
  }

  public void set(int... indexes) {
    clear();
    for (int i : indexes)
      this.indexes.add(i);
  }

  @Override
  public void setOn(int i) {
    assert i < size;
    indexes.add(i);
  }

  @Override
  public void setOn(int[] other) {
    for (int j : other)
      indexes.add(j);
  }

  public int[] toIndexesArray() {
    int[] result = new int[indexes.size()];
    int j = 0;
    for (Integer i : indexes) {
      result[j] = i;
      j++;
    }
    return result;
  }

  @Override
  public String toString() {
    return Arrays.toString(toIndexesArray());
  }

  @Override
  public int nonZeroElements() {
    return indexes.size();
  }

  @Override
  public void forEach(ElementIterator elementIterator) {
    for (int index : indexes)
      elementIterator.element(index, 1.0);
  }

  @Override
  public Iterator<Integer> iterator() {
    return indexes.iterator();
  }

  @Override
  public RealVector getSubVector(int index, int n) {
    notImplemented();
    return null;
  }

  public static BVector toBinary(double[] ds) {
    int[] is = new int[ds.length];
    for (int i = 0; i < is.length; i++)
      is[i] = (int) ds[i];
    return toBinary(is);
  }

  public static BVector toBinary(byte[] is) {
    BVector bobs = new BVector(is.length * Byte.SIZE);
    for (int i = 0; i < is.length; i++)
      for (int bi = 0; bi < Byte.SIZE; bi++) {
        int mask = 1 << bi;
        if ((is[i] & mask) != 0)
          bobs.setOn(i * Byte.SIZE + bi);
      }
    return bobs;
  }

  public static BVector toBinary(int[] is) {
    BVector bobs = new BVector(is.length * Integer.SIZE);
    for (int i = 0; i < is.length; i++)
      for (int bi = 0; bi < Integer.SIZE; bi++) {
        int mask = 1 << bi;
        if ((is[i] & mask) != 0)
          bobs.setOn(i * Integer.SIZE + bi);
      }
    return bobs;
  }

  @Override
  public int[] activeIndexes() {
    return Utils.asIntArray(indexes);
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
