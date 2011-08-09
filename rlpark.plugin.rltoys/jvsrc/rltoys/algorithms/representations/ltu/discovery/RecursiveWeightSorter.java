package rltoys.algorithms.representations.ltu.discovery;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import rltoys.algorithms.learning.predictions.LinearLearner;
import rltoys.algorithms.representations.ltu.networks.RandomNetwork;
import rltoys.algorithms.representations.ltu.units.LTU;
import rltoys.math.vector.implementations.PVector;
import rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class RecursiveWeightSorter extends WeightSorter {
  @IgnoreMonitor
  final protected RandomNetwork network;
  private final int nbMaxParents;
  @Monitor(level = 4)
  private final PVector recursiveSum;
  private final double discount;

  public RecursiveWeightSorter(RandomNetwork network, LinearLearner[] learners, int nbMaxParents) {
    super(learners);
    assert network.inputSize > network.outputSize;
    this.network = network;
    this.nbMaxParents = nbMaxParents;
    this.recursiveSum = new PVector(sums.size);
    this.discount = Utils.timeStepsToDiscount(nbMaxParents);
  }

  @Override
  protected Comparator<Integer> createComparator() {
    return new PVectorBasedComparator(recursiveSum) {
      private final int maxSort = network.outputSize;

      @Override
      public int compare(Integer o1, Integer o2) {
        if (o1 >= maxSort && o2 < maxSort)
          return 1;
        if (o2 >= maxSort && o1 < maxSort)
          return -1;
        return super.compare(o1, o2);
      }
    };
  }

  @Override
  protected void updateUnitEvaluation() {
    super.updateUnitEvaluation();
    recursiveSum.set(0);
    for (int i = 0; i < network.outputSize; i++)
      recursiveSum.data[i] = computeRecursiveWeights(new HashSet<LTU>(), network.ltu(i), 1.0);
  }

  private double computeRecursiveWeights(Set<LTU> counted, LTU ltu, double currentDiscount) {
    boolean added = counted.add(ltu);
    if (!added)
      return 0;
    double result = sums.data[ltu.index()] * currentDiscount;
    if (counted.size() >= nbMaxParents)
      return result;
    double parentDiscount = currentDiscount * discount;
    for (LTU parentLTU : network.parents(ltu.index()))
      result += computeRecursiveWeights(counted, parentLTU, parentDiscount);
    return result;
  }
}
