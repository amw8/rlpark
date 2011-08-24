package rltoys.algorithms.representations.ltu.networks;

import java.io.Serializable;

import rltoys.algorithms.representations.ltu.internal.LTUArray;
import rltoys.algorithms.representations.ltu.internal.LTUUpdated;
import rltoys.algorithms.representations.ltu.units.LTU;
import rltoys.math.vector.BinaryVector;
import rltoys.math.vector.implementations.BVector;
import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class RandomNetwork implements Serializable {
  private static final long serialVersionUID = 8140259178658376161L;
  final public int outputSize;
  final public int inputSize;
  @Monitor(level = 4)
  protected final BVector output;
  @Monitor(level = 4)
  protected final LTU[] ltus;
  @IgnoreMonitor
  protected final LTUArray[] connectedLTUs;
  protected int nbConnection = 0;
  protected int nbActive = 0;
  @IgnoreMonitor
  protected final double[] denseInputVector;
  final LTUUpdated updatedLTUs;
  private final RandomNetworkScheduler scheduler;
  @SuppressWarnings("unused")
  @Monitor
  private int nbUnitUpdated = 0;

  public RandomNetwork(int inputSize, int outputSize) {
    this(new RandomNetworkScheduler(), inputSize, outputSize);
  }

  public RandomNetwork(RandomNetworkScheduler scheduler, int inputSize, int outputSize) {
    this.outputSize = outputSize;
    this.inputSize = inputSize;
    this.scheduler = scheduler;
    connectedLTUs = new LTUArray[inputSize];
    for (int i = 0; i < connectedLTUs.length; i++)
      connectedLTUs[i] = new LTUArray();
    ltus = new LTU[outputSize];
    output = new BVector(outputSize);
    denseInputVector = new double[inputSize];
    updatedLTUs = new LTUUpdated(outputSize);
  }

  public void addLTU(LTU ltu) {
    removeLTU(ltus[ltu.index()]);
    ltus[ltu.index()] = ltu;
    int[] ltuInputs = ltu.inputs();
    for (int input : ltuInputs)
      connectedLTUs[input].add(ltu);
    addLTUStat(ltu);
  }

  private void addLTUStat(LTU ltu) {
    nbConnection += ltu.inputs().length;
  }

  private void removeLTUStat(LTU ltu) {
    nbConnection -= ltu.inputs().length;
  }

  public void removeLTU(LTU ltu) {
    if (ltu == null)
      return;
    assert ltus[ltu.index()] != null;
    removeLTUStat(ltu);
    ltus[ltu.index()] = null;
    for (int input : ltu.inputs())
      if (connectedLTUs[input] != null)
        connectedLTUs[input].remove(ltu);
  }

  protected void prepareProjection(BinaryVector obs) {
    for (int activeIndex : obs.activeIndexes())
      denseInputVector[activeIndex] = 1;
    output.clear();
    updatedLTUs.clean();
  }

  protected void postProjection(BinaryVector obs) {
    for (int activeIndex : obs.activeIndexes())
      denseInputVector[activeIndex] = 0;
  }

  public BVector project(BinaryVector obs) {
    prepareProjection(obs);
    scheduler.update(this, obs);
    nbUnitUpdated = updatedLTUs.nbUnitUpdated();
    postProjection(obs);
    nbActive = output.nonZeroElements();
    return output.copy();
  }

  public LTU ltu(int i) {
    return ltus[i];
  }

  public LTU[] parents(int index) {
    return connectedLTUs[index] != null ? connectedLTUs[index].array() : new LTU[] {};
  }

  public LTU[] ltus() {
    return ltus;
  }
}
