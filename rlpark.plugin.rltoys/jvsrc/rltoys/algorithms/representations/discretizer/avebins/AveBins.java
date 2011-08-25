package rltoys.algorithms.representations.discretizer.avebins;

import rltoys.algorithms.representations.discretizer.Discretizer;
import rltoys.math.normalization.MeanVar;

public class AveBins implements Discretizer {
  private final MeanVar[] averages;

  public AveBins(MeanVar prototype, int nbBins) {
    averages = new MeanVar[nbBins];
    for (int i = 0; i < averages.length; i++)
      averages[i] = prototype.newInstance();
  }

  @Override
  public int discretize(double input) {
    int begin = 0;
    int end = averages.length - 1;
    while (begin != end) {
      int position = (begin + end) / 2;
      MeanVar meanVar = averages[position];
      meanVar.update(input);
      if (input <= meanVar.mean())
        end = position;
      else
        begin = position + 1;
    }
    return begin;
  }

  @Override
  public int resolution() {
    return averages.length;
  }
}
