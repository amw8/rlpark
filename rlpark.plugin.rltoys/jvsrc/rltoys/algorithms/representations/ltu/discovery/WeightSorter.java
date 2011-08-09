package rltoys.algorithms.representations.ltu.discovery;

import java.util.Arrays;
import java.util.Comparator;

import rltoys.algorithms.learning.predictions.LinearLearner;
import rltoys.math.vector.implementations.PVector;
import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class WeightSorter {
  public static class PVectorBasedComparator implements Comparator<Integer> {
    final private double[] data;

    public PVectorBasedComparator(PVector reference) {
      data = reference.data;
    }

    @Override
    public int compare(Integer o1, Integer o2) {
      return Double.compare(data[o1], data[o2]);
    }
  };

  @IgnoreMonitor
  private final LinearLearner[] learners;
  @Monitor(level = 4)
  protected final PVector sums;
  @Monitor(level = 4)
  private final Integer[] order;
  private Comparator<Integer> comparator;
  private int worst;
  private final int startSorting;
  private final int endSorting;

  public WeightSorter(LinearLearner[] learners) {
    this(learners, 0, -1);
  }

  public WeightSorter(LinearLearner[] learners, int startSorting, int endSorting) {
    this.learners = learners;
    sums = new PVector(learners[0].weights().size);
    this.startSorting = startSorting;
    this.endSorting = endSorting > 0 ? endSorting : sums.getDimension();
    order = new Integer[this.endSorting - this.startSorting];
    for (int i = this.startSorting; i < this.endSorting; i++)
      order[i - this.startSorting] = i;
  }

  protected Comparator<Integer> createComparator() {
    return new PVectorBasedComparator(sums);
  }

  public void sort() {
    if (comparator == null)
      comparator = createComparator();
    worst = 0;
    updateUnitEvaluation();
    Arrays.sort(order, comparator);
  }

  protected void updateUnitEvaluation() {
    sums.set(0);
    for (LinearLearner learner : learners) {
      PVector weight = learner.weights();
      for (int i = startSorting; i < endSorting; i++)
        sums.data[i] += Math.abs(weight.data[i]);
    }
  }

  public boolean hasNext() {
    return worst < order.length;
  }

  public int nextWorst() {
    int result = order[worst];
    worst++;
    return result;
  }

  public void resetWeights(int index) {
    for (LinearLearner learner : learners)
      learner.resetWeight(index);
  }
}
