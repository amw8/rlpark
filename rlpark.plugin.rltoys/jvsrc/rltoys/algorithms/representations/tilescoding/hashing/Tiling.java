package rltoys.algorithms.representations.tilescoding.hashing;

import java.io.Serializable;

import rltoys.algorithms.representations.tilescoding.discretizer.Discretizer;
import rltoys.utils.Utils;

public class Tiling implements Serializable {
  private static final long serialVersionUID = 4526997497936063389L;
  public final int hashingIndex;
  private final Discretizer[] discretizers;
  private final int[] inputIndexes;
  private final int[] coordinates;

  public Tiling(int hashingIndex, Discretizer[] discretizers, int[] inputIndexes) {
    assert Utils.checkInstanciated(discretizers);
    this.hashingIndex = hashingIndex;
    this.discretizers = discretizers;
    this.inputIndexes = inputIndexes;
    coordinates = new int[inputIndexes.length];
  }

  public int[] tilesCoordinates(double[] inputs) {
    for (int i = 0; i < inputIndexes.length; i++)
      coordinates[i] = discretizers[i].discretize(inputs[inputIndexes[i]]);
    return coordinates;
  }

  protected Discretizer[] discretizers() {
    return discretizers;
  }

  public int[] inputIndexes() {
    return inputIndexes;
  }
}
