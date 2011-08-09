package rltoys.math.vector;

import static rltoys.utils.Utils.notImplemented;

import java.util.Arrays;


public class BVector extends AbstractVector implements BinaryVector {
  private static final long serialVersionUID = 5688026326299722364L;
  public final int size;
  private int[] activeIndexes;
  private int nbActive = 0;

  public BVector(int size) {
    this(size, 0);
  }

  public BVector(int size, int allocated) {
    this.size = size;
    activeIndexes = new int[allocated];
  }

  public BVector(int size, int[] indexes) {
    this(size, indexes.length);
    for (int i = 0; i < indexes.length; i++)
      setOn(indexes[i]);
  }

  public BVector(BVector v) {
    this(v.size, v.nbActive);
    System.arraycopy(v.activeIndexes, 0, activeIndexes, 0, v.nbActive);
    nbActive = v.nbActive;
  }

  @Override
  public void addSelfTo(double[] data) {
    for (int i : activeIndexes())
      data[i] += 1;
  }

  @Override
  public double dotProduct(double[] data) {
    double result = 0.0;
    for (int i : activeIndexes())
      result += data[i];
    return result;
  }

  @Override
  public void subtractSelfTo(double[] data) {
    for (int i : activeIndexes())
      data[i] -= 1;
  }

  @Override
  public void subtractSelfTo(SparseRealVector other) {
    for (int i : activeIndexes())
      other.setEntry(i, other.getEntry(i) - 1);
  }

  @Override
  public double[] accessData() {
    double[] result = new double[size];
    for (int i : activeIndexes())
      result[i] = 1;
    return result;
  }

  @Override
  public SVector copyAsMutable() {
    return new SVector(this);
  }

  @Override
  public boolean checkValues() {
    return true;
  }

  @Override
  public BVector copy() {
    return new BVector(this);
  }

  @Override
  public double dotProduct(RealVector other) {
    double result = 0;
    for (int i : activeIndexes())
      result += other.getEntry(i);
    return result;
  }

  @Override
  public int getDimension() {
    return size;
  }

  @Override
  public double getEntry(int i) {
    return search(i) >= 0 ? 1 : 0;
  }

  private int search(int i) {
    return Arrays.binarySearch(activeIndexes, 0, nbActive, i);
  }

  @Override
  public ModifiableVector mapMultiply(double d) {
    SVector result = copyAsMutable();
    result.mapMultiplyToSelf(d);
    return result;
  }

  @Override
  public ModifiableVector newInstance(int size) {
    SVector result = copyAsMutable();
    result.clear();
    return result;
  }

  @Override
  public void clear() {
    nbActive = 0;
  }

  @Override
  public void setOn(int index) {
    assert index < size;
    int searchResult = (canAppend(index) ? -nbActive - 1 : search(index));
    if (searchResult >= 0)
      return;
    int insertion = -searchResult - 1;
    nbActive++;
    int[] newActiveIndex = activeIndexes;
    if (nbActive >= activeIndexes.length) {
      int newLength = activeIndexes.length > 0 ? activeIndexes.length * 2 : 1;
      newActiveIndex = new int[newLength];
      System.arraycopy(activeIndexes, 0, newActiveIndex, 0, insertion);
    }
    System.arraycopy(activeIndexes, insertion, newActiveIndex, insertion + 1, nbActive - insertion - 1);
    newActiveIndex[insertion] = index;
    activeIndexes = newActiveIndex;
  }

  private boolean canAppend(int index) {
    return nbActive == 0 || index > activeIndexes[nbActive - 1];
  }

  @Override
  public String toString() {
    return Arrays.toString(activeIndexes());
  }

  @Override
  public int nonZeroElements() {
    return nbActive;
  }

  @Override
  public void forEach(ElementIterator elementIterator) {
    for (int index : activeIndexes())
      elementIterator.element(index, 1.0);
  }

  @Override
  public ModifiableVector getSubVector(int index, int n) {
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
  final public int[] activeIndexes() {
    if (activeIndexes.length > nbActive)
      activeIndexes = Arrays.copyOf(activeIndexes, nbActive);
    return activeIndexes;
  }

  public void mergeSubVector(int start, BinaryVector other) {
    if (!canAppend(start))
      throw new RuntimeException("cannot append when other indexes are less than already active indexes");
    int[] otherIndexes = other.activeIndexes();
    allocate(nbActive + otherIndexes.length);
    for (int otherIndex : otherIndexes) {
      activeIndexes[nbActive] = otherIndex + start;
      nbActive++;
    }
  }

  public void allocate(int allocation) {
    if (allocation <= activeIndexes.length)
      return;
    activeIndexes = Arrays.copyOf(activeIndexes, allocation);
  }
}
