package rltoys.algorithms.representations.ltu.networks;

import java.util.Random;

import rltoys.algorithms.representations.ltu.units.LTU;
import rltoys.math.vector.BinaryVector;
import rltoys.math.vector.implementations.BVector;

public class RandomNetworks {
  public static void fullyConnect(Random random, RandomNetwork randomNetwork, LTU prototype) {
    int outputSize = randomNetwork.outputSize;
    int inputSize = randomNetwork.inputSize;
    for (int i = 0; i < outputSize; i++) {
      byte[] weights = new byte[inputSize];
      int[] inputs = new int[inputSize];
      for (int j = 0; j < inputSize; j++) {
        inputs[j] = j;
        weights[j] = (byte) (random.nextBoolean() ? +1 : -1);
      }
      randomNetwork.addLTU(prototype.newLTU(i, inputs, weights));
    }
  }

  public static void connect(Random random, RandomNetwork randomNetwork, LTU prototype, int nbUnitInputs) {
    int outputSize = randomNetwork.outputSize;
    int inputSize = randomNetwork.inputSize;
    for (int i = 0; i < outputSize; i++) {
      byte[] weights = new byte[nbUnitInputs];
      int[] inputs = new int[nbUnitInputs];
      boolean[] choosen = new boolean[inputSize];
      for (int j = 0; j < nbUnitInputs; j++) {
        inputs[j] = chooseInput(random, choosen);
        weights[j] = (byte) (random.nextBoolean() ? +1 : -1);
      }
      randomNetwork.addLTU(prototype.newLTU(i, inputs, weights));
    }
  }

  private static int chooseInput(Random random, boolean[] choosen) {
    int result = random.nextInt(choosen.length);
    while (choosen[result])
      result = (result + 1) % choosen.length;
    return result;
  }

  static public BinaryVector addBiasUnit(BVector projected) {
    BVector vector = BVector.toBVector(projected.size + 1, projected.activeIndexes());
    vector.setOn(vector.size - 1);
    return vector;
  }
}
