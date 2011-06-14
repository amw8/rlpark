package rltoys.math.vector;

import java.io.Serializable;

public class CachedVector extends CachedGenericVector<PVector> implements Serializable {
  private static final long serialVersionUID = -7874485527771296825L;

  @Override
  protected PVector newInstance(int size) {
    return new PVector(size);
  }
}
