package rltoys.math.vector;

import java.util.List;

import rltoys.math.representations.Function;

public abstract class CachedGenericVector<T extends PVector> {
  private T cached = null;
  private T values;

  public CachedGenericVector() {
    super();
  }

  abstract protected T newInstance(int size);

  private T cached(int size) {
    assert cached == null || cached.size == size;
    if (cached == null)
      cached = newInstance(size);
    return cached;
  }

  public T setSize(int size) {
    return cached(size);
  }

  public void set(List<? extends Function> functions) {
    PVectors.set(cached(functions.size()), functions);
    values = cached;
  }

  public T values() {
    return values;
  }

  public void reset() {
    values = null;
  }

  public void set(RealVector values) {
    if (values == null) {
      this.values = null;
      return;
    }
    cached(values.getDimension()).setSubVector(0, values);
    this.values = cached;
  }

  @Override
  public String toString() {
    if (values == null)
      return "[null]";
    return String.valueOf(values);
  }

  public int size() {
    return cached.size;
  }
}
