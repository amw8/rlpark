package rltoys.math.vector;

import static rltoys.utils.Utils.notImplemented;
import static rltoys.utils.Utils.notSupported;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import rltoys.utils.NotImplemented;

public class BConstantVector implements SparseVector {
  private static final long serialVersionUID = -5499180612508381935L;
  public final BinaryVector bvector;
  public double constant;

  public BConstantVector(BinaryVector bvector, double constant) {
    this.bvector = bvector;
    this.constant = constant;
  }

  @Override
  public double[] accessData() {
    double[] result = new double[bvector.getDimension()];
    for (Integer i : bvector)
      result[i] = constant;
    return result;
  }

  @Override
  public void addSelfTo(double[] data) {
    for (int i : bvector.activeIndexes())
      data[i] += constant;
  }

  @Override
  public double dotProduct(double[] data) {
    return bvector.dotProduct(data) * constant;
  }

  @Override
  public double dotProduct(RealVector other) {
    return bvector.dotProduct(other) * constant;
  }

  @Override
  public int getDimension() {
    return bvector.getDimension();
  }

  @Override
  public double getEntry(int i) {
    return bvector.getEntry(i) * constant;
  }

  @Override
  public SVector newInstance(int size) {
    return new SVector(size);
  }

  @Override
  public int nonZeroElements() {
    return bvector.nonZeroElements();
  }

  @Override
  public String toString() {
    return bvector.toString() + "x" + constant;
  }

  @Override
  public RealVector mapMultiplyToSelf(double d) {
    constant *= d;
    return this;
  }

  @Override
  public void forEach(ElementIterator elementIterator) {
    for (int index : bvector)
      elementIterator.element(index, constant);
  }

  @Override
  public void subtractSelfTo(double[] data) {
    for (Integer i : bvector)
      data[i] -= constant;
  }

  @Override
  public void subtractSelfTo(SparseVector other) {
    for (Integer i : bvector)
      other.setEntry(i, other.getEntry(i) - constant);
  }

  @Override
  public RealVector add(RealVector other) {
    if (other instanceof BConstantVector)
      return new SVector(other).add(this);
    return other.add(this);
  }

  @Override
  public RealVector addToSelf(RealVector other) {
    notSupported();
    return null;
  }

  @Override
  public boolean checkValues() {
    notSupported();
    return false;
  }

  @Override
  public RealVector copy() {
    notSupported();
    return null;
  }

  @Override
  public RealVector mapMultiply(double d) {
    return new BConstantVector(bvector, d * constant);
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
    notSupported();
  }

  @Override
  public RealVector subtract(RealVector other) {
    return other.mapMultiply(-1).add(this);
  }

  @Override
  public RealVector subtractToSelf(RealVector other) {
    notSupported();
    return null;
  }

  public Collection<Integer> indexes() {
    List<Integer> result = new ArrayList<Integer>(nonZeroElements());
    for (Integer i : bvector)
      result.add(i);
    return result;
  }

  @Override
  public RealVector getSubVector(int index, int n) {
    notImplemented();
    return null;
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
