package rltoys.math.vector;

import static rltoys.utils.Utils.notImplemented;
import static rltoys.utils.Utils.notSupported;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import rltoys.utils.NotImplemented;


public class BUncheckedVector implements BinaryVector {
  private static final long serialVersionUID = -6172060217685648302L;
  public final int size;
  public final int[] indexes;
  private int addedElements;

  public BUncheckedVector(int nbActive, int size) {
    indexes = new int[nbActive];
    this.size = size;
  }

  public BUncheckedVector(int size, int[] indexes) {
    this(indexes.length, size);
    set(indexes);
  }

  public BUncheckedVector(int size, Collection<Integer> indexes) {
    this(indexes.size(), size);
    int indexInArray = 0;
    for (Integer activeIndex : indexes) {
      this.indexes[indexInArray] = activeIndex;
      indexInArray++;
    }
  }

  public BUncheckedVector(BUncheckedVector v) {
    this(v.nonZeroElements(), v.size);
    set(v.indexes);
  }

  @Override
  public void addSelfTo(double[] data) {
    for (int i : indexes)
      data[i] += 1;
  }

  @Override
  public double dotProduct(double[] data) {
    double result = 0.0;
    for (int i : indexes)
      result += data[i];
    return result;
  }

  @Override
  public void subtractSelfTo(double[] data) {
    for (int i : indexes)
      data[i] -= 1;
  }

  @Override
  public void subtractSelfTo(SparseVector other) {
    for (int i : indexes)
      other.setEntry(i, other.getEntry(i) - 1);
  }

  @Override
  public double[] accessData() {
    double[] result = new double[size];
    for (int i : indexes)
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
  public BUncheckedVector copy() {
    BUncheckedVector result = newInstance(size);
    result.set(indexes);
    return result;
  }

  @Override
  public double dotProduct(RealVector other) {
    double result = 0;
    for (int i : indexes)
      result += other.getEntry(i);
    return result;
  }

  @Override
  public int getDimension() {
    return size;
  }

  @Override
  public double getEntry(int i) {
    for (int indexe : indexes)
      if (indexe == i)
        return 1.0;
    return 0.0;
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
  public BUncheckedVector newInstance(int size) {
    return new BUncheckedVector(nonZeroElements(), size);
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
    mergeSubVector(i, (BUncheckedVector) other);
  }

  public void mergeSubVector(int i, BUncheckedVector other) {
    assert i + other.size <= size;
    for (int oi : other.indexes) {
      indexes[addedElements] = oi + i;
      addedElements++;
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
    addedElements = 0;
  }

  public void set(int... indexes) {
    System.arraycopy(indexes, 0, this.indexes, 0, this.indexes.length);
    addedElements = this.indexes.length;
  }

  @Override
  public void setOn(int i) {
    indexes[addedElements] = i;
    addedElements++;
  }

  @Override
  public void setOn(int[] other) {
    for (int i : other) {
      indexes[addedElements] = i;
      addedElements++;
    }
  }

  public int[] toIndexesArray() {
    int[] result = new int[indexes.length];
    int j = 0;
    for (int i : indexes) {
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
    return addedElements;
  }

  @Override
  public void forEach(ElementIterator elementIterator) {
    for (int index : indexes)
      elementIterator.element(index, 1.0);
  }

  @Override
  public Iterator<Integer> iterator() {
    return new Iterator<Integer>() {
      int index;

      @Override
      public boolean hasNext() {
        return index < indexes.length;
      }

      @Override
      public Integer next() {
        Integer result = indexes[index];
        index++;
        return result;
      }

      @Override
      public void remove() {
        notSupported();
      }
    };
  }

  @Override
  public RealVector getSubVector(int index, int n) {
    notImplemented();
    return null;
  }

  @Override
  public int[] activeIndexes() {
    return indexes;
  }

  @Override
  public RealVector ebeMultiply(RealVector other) {
    return copyAsSVector().ebeMultiplyToSelf(other);
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
