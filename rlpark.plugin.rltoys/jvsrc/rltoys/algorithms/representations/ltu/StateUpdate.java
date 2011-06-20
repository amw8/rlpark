package rltoys.algorithms.representations.ltu;

import rltoys.algorithms.representations.ltu.networks.RandomNetwork;
import rltoys.math.vector.BVector;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class StateUpdate {
  private final RandomNetwork network;
  private final int nbObsInput;
  private BVector networkOutput;
  @Monitor
  private final BVector networkInput;

  public StateUpdate(RandomNetwork network, int nbObsInput) {
    this.network = network;
    this.nbObsInput = nbObsInput;
    networkInput = new BVector(network.inputSize);
  }

  public BVector updateState(BVector o_tp1) {
    if (o_tp1 == null) {
      networkOutput = null;
      return null;
    }
    networkInput.clear();
    if (networkOutput != null)
      networkInput.mergeSubVector(0, networkOutput);
    networkInput.mergeSubVector(network.outputSize, o_tp1);
    networkOutput = network.project(networkInput);
    BVector s_tp1 = new BVector(stateSize());
    s_tp1.mergeSubVector(0, networkOutput);
    s_tp1.mergeSubVector(network.outputSize, o_tp1);
    s_tp1.setOn(s_tp1.size - 1);
    return s_tp1;
  }

  public int stateSize() {
    return network.outputSize + nbObsInput + 1;
  }
}
