package rltoys.algorithms.representations.ltu.discovery;

import java.util.LinkedList;
import java.util.Random;

import rltoys.algorithms.representations.ltu.networks.RandomNetwork;
import rltoys.algorithms.representations.ltu.networks.RandomNetworks;
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
  @IgnoreMonitor
  private final LTU prototype;
  private final Random random;
  private int worstUnit;
  private final int nbInputForUnit;

  public RepresentationDiscovery(Random random, RandomNetwork network, WeightSorter sorter, LTU prototype,
      int nbProtectedUnit, int nbInputForUnit) {
    this.random = random;
    this.network = network;
    this.sorter = sorter;
    this.prototype = prototype;
    this.nbInputForUnit = nbInputForUnit;
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
    return RandomNetworks.newRandomUnit(random, prototype, ltuIndex, nbInputForUnit, network.inputSize);
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
