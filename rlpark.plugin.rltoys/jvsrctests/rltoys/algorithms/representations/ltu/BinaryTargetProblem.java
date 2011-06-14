package rltoys.algorithms.representations.ltu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rltoys.algorithms.learning.predictions.LearningAlgorithm;
import rltoys.math.vector.BUncheckedVector;
import rltoys.math.vector.BinaryVector;

public class BinaryTargetProblem {
  final public int inputSize;
  private final double[] weights;
  private final Random random;

  public BinaryTargetProblem(Random random, int nbRelevantInput, int inputSize) {
    this.inputSize = inputSize;
    this.random = random;
    weights = chooseWeights(random, nbRelevantInput);
  }

  private double[] chooseWeights(Random random, int nbRelevantInput) {
    double[] weights = new double[inputSize];
    for (int i = 0; i < weights.length; i++)
      weights[i] = i < nbRelevantInput ? random.nextDouble() : 0.0;
    return weights;
  }

  public double evaluateLearning(int nbTimeStep, LearningAlgorithm learner) {
    double squaredErrorSum = 0.0;
    for (int t = 0; t < nbTimeStep; t++) {
      BUncheckedVector input = createInput();
      double target = computeTarget(input);
      double error = target - learner.predict(input);
      squaredErrorSum += error * error;
      learner.learn(input, target);
    }
    return squaredErrorSum / nbTimeStep;
  }

  private double computeTarget(BinaryVector input) {
    double result = 0;
    for (int activeIndex : input)
      result += weights[activeIndex];
    return result;
  }

  protected BUncheckedVector createInput() {
    List<Integer> input = new ArrayList<Integer>();
    for (int i = 0; i < inputSize; i++)
      if (random.nextBoolean())
        input.add(i);
    return new BUncheckedVector(inputSize, input);
  }
}
