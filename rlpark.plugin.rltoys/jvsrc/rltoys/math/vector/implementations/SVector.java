package rltoys.math.vector.implementations;

import java.util.Arrays;
import java.util.Iterator;

import rltoys.math.vector.MutableVector;
import rltoys.math.vector.RealVector;
import rltoys.math.vector.SparseRealVector;
import rltoys.math.vector.VectorEntry;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class SVector extends AbstractVector implements SparseRealVector {
  private static class SVectorEntry implements VectorEntry {
    private final int[] indexes;
    private final double[] values;
    private int current;

    public SVectorEntry(int[] indexes, double[] values) {
      this.indexes = indexes;
      this.values = values;
    }

    @Override
    public int index() {
      return indexes[current];
    }

    @Override
    public double value() {
      return values[current];
    }

    public void update(int current) {
      this.current = current;
    }
  }

  private class SVectorIterator implements Iterator<VectorEntry> {
    private int current;
    private final SVectorEntry entry;
    private boolean removed;

    @SuppressWarnings("synthetic-access")
    protected SVectorIterator() {
      current = -1;
      nbActive = SVector.this.nbActive;
      entry = new SVectorEntry(indexes, values);
    }

    @Override
    public boolean hasNext() {
      return current < nbActive - 1;
    }

    @Override
    public VectorEntry next() {
      if (!removed) {
        current++;
        entry.update(current);
      } else
        removed = false;
      return entry;
    }

    @Override
    public void remove() {
      removeExistingEntry(current);
      removed = true;
    }
  }

  private static final long serialVersionUID = -3324707947990480491L;
  private int[] indexes;
  private double[] values;
  @Monitor
  int nbActive = 0;

  public SVector(int size) {
    this(size, 0);
  }

  public SVector(int size, int allocated) {
    super(size);
    indexes = new int[allocated];
    values = new double[allocated];
  }

  public SVector(SVector other) {
    super(other.size);
    indexes = other.indexes.clone();
    values = other.values.clone();
    nbActive = other.nbActive;
  }

  public SVector(BVector other, double value) {
    this(other.size, other.nonZeroElements());
    System.arraycopy(other.activeIndexes(), 0, indexes, 0, indexes.length);
    Arrays.fill(values, value);
    nbActive = indexes.length;
  }

  @Override
  public MutableVector copy() {
    return new SVector(this);
  }

  @Override
  public MutableVector newInstance(int size) {
    return new SVector(size);
  }

  @Override
  public MutableVector copyAsMutable() {
    return copy();
  }

  private int search(int i) {
    return Arrays.binarySearch(indexes, 0, nbActive, i);
  }

  @Override
  public void setEntry(int index, double value) {
    if (value == 0.0) {
      int searchResult = search(index);
      if (searchResult < 0)
        return;
      removeExistingEntry(searchResult);
      return;
    }
    int position = findElementPosition(index);
    values[position] = value;
  }

  void removeExistingEntry(int entryIndex) {
    System.arraycopy(indexes, entryIndex + 1, indexes, entryIndex, nbActive - entryIndex - 1);
    System.arraycopy(values, entryIndex + 1, values, entryIndex, nbActive - entryIndex - 1);
    nbActive--;
  }

  private int findElementPosition(int index) {
    assert index < size;
    int searchResult = search(index);
    if (searchResult >= 0)
      return searchResult;
    int insertion = -searchResult - 1;
    nbActive++;
    int[] newActiveIndex = indexes;
    double[] newActiveValues = values;
    if (nbActive >= indexes.length) {
      int newLength = indexes.length > 0 ? indexes.length * 2 : 1;
      newActiveIndex = new int[newLength];
      newActiveValues = new double[newLength];
      System.arraycopy(indexes, 0, newActiveIndex, 0, insertion);
      System.arraycopy(values, 0, newActiveValues, 0, insertion);
    }
    System.arraycopy(indexes, insertion, newActiveIndex, insertion + 1, nbActive - insertion - 1);
    System.arraycopy(values, insertion, newActiveValues, insertion + 1, nbActive - insertion - 1);
    newActiveIndex[insertion] = index;
    indexes = newActiveIndex;
    values = newActiveValues;
    return insertion;
  }

  @Override
  public MutableVector addToSelf(RealVector other) {
    for (VectorEntry entry : other) {
      final int index = entry.index();
      setEntry(index, getEntry(index) + entry.value());
    }
    return this;
  }

  @Override
  public MutableVector subtractToSelf(RealVector other) {
    for (VectorEntry entry : other) {
      final int index = entry.index();
      setEntry(index, getEntry(index) - entry.value());
    }
    return this;
  }

  @Override
  public MutableVector ebeMultiplyToSelf(RealVector other) {
    for (VectorEntry entry : other) {
      final int index = entry.index();
      setEntry(index, getEntry(index) * entry.value());
    }
    return this;
  }

  @Override
  public MutableVector mapMultiplyToSelf(double d) {
    for (int i = 0; i < values.length; i++)
      values[i] *= d;
    return this;
  }

  @Override
  public double getEntry(int i) {
    final int position = search(i);
    return position >= 0 ? values[position] : 0;
  }

  @Override
  public double dotProduct(RealVector other) {
    double sum = 0;
    for (int i = 0; i < nbActive; i++)
      sum += values[i] * other.getEntry(indexes[i]);
    return sum;
  }

  @Override
  public double dotProduct(double[] data) {
    double sum = 0;
    for (int i = 0; i < nbActive; i++)
      sum += values[i] * data[indexes[i]];
    return sum;
  }

  @Override
  public void addSelfTo(double[] data) {
    for (int i = 0; i < nbActive; i++)
      data[indexes[i]] += values[i];
  }

  @Override
  public void subtractSelfTo(double[] data) {
    for (int i = 0; i < nbActive; i++)
      data[indexes[i]] -= values[i];
  }

  @Override
  public int nonZeroElements() {
    return nbActive;
  }

  @Override
  public void clear() {
    nbActive = 0;
  }

  @Override
  public Iterator<VectorEntry> iterator() {
    return new SVectorIterator();
  }
}
