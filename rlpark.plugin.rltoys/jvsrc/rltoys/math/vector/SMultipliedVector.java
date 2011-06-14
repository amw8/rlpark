package rltoys.math.vector;

import static rltoys.utils.Utils.notImplemented;
import static rltoys.utils.Utils.notSupported;

import java.util.Map;

import rltoys.utils.NotImplemented;


public class SMultipliedVector implements SparseVector {
  private static final long serialVersionUID = -3828595063276696964L;
  private final SVector svector;
  private final double multiplier;

  public SMultipliedVector(SVector svector, double multiplier) {
    this.svector = svector;
    this.multiplier = multiplier;
  }

  @Override
  public void addSelfTo(double[] data) {
    for (Map.Entry<Integer, Double> entry : svector.values.entrySet())
      data[entry.getKey()] += entry.getValue() * multiplier;
  }

  @Override
  public double dotProduct(double[] data) {
    notSupported();
    return 0;
  }

  @Override
  public void forEach(ElementIterator elementIterator) {
    for (Map.Entry<Integer, Double> entry : svector.values.entrySet())
      elementIterator.element(entry.getKey(), entry.getValue() * multiplier);
  }

  @Override
  public int nonZeroElements() {
    notSupported();
    return 0;
  }

  @Override
  public void subtractSelfTo(double[] data) {
    for (Map.Entry<Integer, Double> entry : svector.values.entrySet())
      data[entry.getKey()] -= entry.getValue() * multiplier;
  }

  @Override
  public void subtractSelfTo(SparseVector other) {
    for (Map.Entry<Integer, Double> entry : svector.values.entrySet()) {
      int i = entry.getKey();
      other.setEntry(i, other.getEntry(i) - entry.getValue() * multiplier);
    }
  }

  @Override
  public double[] accessData() {
    notSupported();
    return null;
  }

  @Override
  public RealVector add(RealVector other) {
    notSupported();
    return null;
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
  public SVector copy() {
    SVector result = svector.copy();
    result.mapMultiplyToSelf(multiplier);
    return result;
  }

  @Override
  public double dotProduct(RealVector other) {
    notSupported();
    return 0;
  }

  @Override
  public int getDimension() {
    return svector.getDimension();
  }

  @Override
  public double getEntry(int i) {
    return svector.getEntry(i) * multiplier;
  }

  @Override
  public RealVector mapMultiply(double d) {
    notSupported();
    return null;
  }

  @Override
  public RealVector mapMultiplyToSelf(double d) {
    notSupported();
    return null;
  }

  @Override
  public RealVector newInstance(int size) {
    notSupported();
    return null;
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
    SVector result = copy();
    result.subtractToSelf(other);
    return result;
  }

  @Override
  public RealVector subtractToSelf(RealVector other) {
    return subtract(other);
  }

  @Override
  public RealVector getSubVector(int index, int n) {
    notImplemented();
    return null;
  }

  @Override
  public String toString() {
    return copy().toString();
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
  public RealVector ebeMultiplyToSelf(RealVector phi_t) {
    throw new NotImplemented();
  }
}
