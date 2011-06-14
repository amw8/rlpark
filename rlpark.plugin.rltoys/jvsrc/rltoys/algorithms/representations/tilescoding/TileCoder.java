package rltoys.algorithms.representations.tilescoding;

import java.io.Serializable;

import rltoys.algorithms.representations.tilescoding.hashing.Hashing;
import rltoys.algorithms.representations.tilescoding.hashing.Tiling;


public class TileCoder implements Serializable {
  private static final long serialVersionUID = 2546756615574797245L;
  private final Tiling[] tilings;
  private final int[] activeTiles;
  public final int resolution;

  public TileCoder(Tiling[] tilings, int resolution) {
    this.tilings = tilings;
    this.resolution = resolution;
    activeTiles = new int[tilings.length];
  }

  public int[] updateActiveTiles(Hashing hashing, double[] inputs) {
    for (int i = 0; i < tilings.length; i++) {
      Tiling tiling = tilings[i];
      int coordinates[] = tiling.tilesCoordinates(inputs);
      activeTiles[i] = hashing.hash(tiling, coordinates);
    }
    return activeTiles;
  }

  public int nbTilings() {
    return tilings.length;
  }

  public int[] inputIndexes() {
    return tilings[0].inputIndexes();
  }

  public Tiling[] tilings() {
    return tilings;
  }
}
