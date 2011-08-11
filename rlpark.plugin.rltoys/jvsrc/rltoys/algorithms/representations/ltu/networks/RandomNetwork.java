package rltoys.algorithms.representations.ltu.networks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
  protected final List<LTU>[] connectedLTUs;
  protected int nbConnection = 0;
  protected int nbActive = 0;
  protected final double[] inputVector;
  private final boolean[] updatedLTUs;

  @SuppressWarnings("unchecked")
  public RandomNetwork(int inputSize, int outputSize) {
    this.outputSize = outputSize;
    this.inputSize = inputSize;
    connectedLTUs = new List[inputSize];
    ltus = new LTU[outputSize];
    output = new BVector(outputSize);
    inputVector = new double[inputSize];
    updatedLTUs = new boolean[outputSize];
    Arrays.fill(updatedLTUs, false);
  }

  public void addLTU(LTU ltu) {
    removeLTU(ltus[ltu.index()]);
    ltus[ltu.index()] = ltu;
    int[] ltuInputs = ltu.inputs();
    for (int input : ltuInputs) {
      if (connectedLTUs[input] == null)
        connectedLTUs[input] = new ArrayList<LTU>();
      connectedLTUs[input].add(ltu);
    }
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

  protected void updateActiveLTUs(BinaryVector obs) {
    for (int activeInput : obs.activeIndexes()) {
      List<LTU> connected = connectedLTUs[activeInput];
      if (connected == null)
        continue;
      for (LTU ltu : connected) {
        final int index = ltu.index();
        if (updatedLTUs[index])
          continue;
        if (ltu.update(inputVector))
          output.setOn(index);
        updatedLTUs[index] = true;
      }
    }
  }

  protected void prepareProjection(BinaryVector obs) {
    for (int activeIndex : obs.activeIndexes())
      inputVector[activeIndex] = 1;
    output.clear();
  }

  protected void postProjection(BinaryVector obs) {
    for (int activeIndex : obs.activeIndexes())
      inputVector[activeIndex] = 0;
    Arrays.fill(updatedLTUs, false);
  }

  public BVector project(BinaryVector obs) {
    prepareProjection(obs);
    updateActiveLTUs(obs);
    postProjection(obs);
    nbActive = output.nonZeroElements();
    return output.copy();
  }

  public LTU ltu(int i) {
    return ltus[i];
  }

  public List<LTU> parents(int index) {
    return connectedLTUs[index] != null ? connectedLTUs[index] : new ArrayList<LTU>();
  }
}
