package rltoys.algorithms.representations.ltu.discovery;

import java.util.LinkedList;
import java.util.Random;

import rltoys.algorithms.representations.ltu.networks.RandomNetwork;
import rltoys.algorithms.representations.ltu.units.LTU;
import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class RepresentationDiscovery {
  @IgnoreMonitor
  private final RandomNetwork network;
  private final WeightSorter sorter;
  private final LinkedList<Integer> protectedUnits = new LinkedList<Integer>();
  private final int nbProtectedUnits;
  private final int nbMaxInputForUnit;
  @IgnoreMonitor
  private final LTU prototype;
  private final Random random;
  private int worstUnit;
  public RepresentationDiscovery(Random random, RandomNetwork network, WeightSorter sorter, LTU prototype,
      int nbProtectedUnit, int nbMaxInputForUnit) {
    this.random = random;
    this.network = network;
    this.sorter = sorter;
    this.nbMaxInputForUnit = nbMaxInputForUnit;
    this.prototype = prototype;
    this.nbProtectedUnits = nbProtectedUnit;
    assert nbProtectedUnits > 0;
  }

  public void changeRepresentation(int nbUnitsToChange) {
    sorter.sort();
    for (int unitIndex = 0; unitIndex < nbUnitsToChange; unitIndex++) {
      worstUnit = findWorstUnit();
      assert worstUnit >= 0;
      LTU ltu = createNewUnit(worstUnit);
      network.addLTU(ltu);
      sorter.resetWeights(ltu.index());
      addIntoProtectedUnits(worstUnit);
    }
  }

  private void addIntoProtectedUnits(int worstUnit) {
    protectedUnits.push(worstUnit);
    if (protectedUnits.size() > nbProtectedUnits)
      protectedUnits.pollLast();
  }

  private LTU createNewUnit(int ltuIndex) {
    int nbUnitInput = random.nextInt(nbMaxInputForUnit - 1) + 1;
    int[] inputs = new int[nbUnitInput];
    byte[] weights = new byte[nbUnitInput];
    for (int i = 0; i < weights.length; i++) {
      inputs[i] = random.nextInt(network.inputSize);
      weights[i] = (byte) (random.nextBoolean() ? -1 : 1);
    }
    return prototype.newLTU(ltuIndex, inputs, weights);
  }

  protected int findWorstUnit() {
    int worstUnit = -1;
    do {
      worstUnit = sorter.nextWorst();
    } while (protectedUnits.contains(worstUnit));
    return worstUnit;
  }

  public void fillNetwork() {
    for (int i = 0; i < network.outputSize; i++) {
      LTU ltu = createNewUnit(i);
      network.addLTU(ltu);
    }
  }
}
