package rltoys.environments.stategraph;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import rltoys.algorithms.representations.acting.ConstantPolicy;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;

@SuppressWarnings("serial")
public class RandomWalk extends FiniteStateGraph {
  static public final GraphState TL = new GraphState("TL", 0.0);
  static public final GraphState A = new GraphState("A", 0.0);
  static public final GraphState B = new GraphState("B", 0.0);
  static public final GraphState C = new GraphState("C", 0.0);
  static public final GraphState D = new GraphState("D", 0.0);
  static public final GraphState E = new GraphState("E", 0.0);
  static public final GraphState TR = new GraphState("TR", 1.0);
  static public final Action left = new Action() {
    @Override
    public String toString() {
      return "left";
    };
  };
  static public final Action right = new Action() {
    @Override
    public String toString() {
      return "right";
    };
  };

  static {
    A.connect(left, TL);
    A.connect(right, B);

    B.connect(left, A);
    B.connect(right, C);

    C.connect(left, B);
    C.connect(right, D);

    D.connect(left, C);
    D.connect(right, E);

    E.connect(left, D);
    E.connect(right, TR);
  }

  public RandomWalk(Random random) {
    this(newPolicy(random, 0.5));
  }


  public RandomWalk(Policy policy) {
    super(policy, new GraphState[] { TL, A, B, C, D, E, TR });
    setInitialState(C);
  }

  @Override
  public double[] expectedDiscountedSolution() {
    return new double[] { 0.056, 0.140, 0.258, 0.431, 0.644 };
  }

  public static ConstantPolicy newPolicy(Random random, double leftProbability) {
    Map<Action, Double> distribution = new LinkedHashMap<Action, Double>();
    distribution.put(left, leftProbability);
    distribution.put(right, 1 - leftProbability);
    return new ConstantPolicy(random, distribution);
  }

  @Override
  public Action[] actions() {
    return new Action[] { left, right };
  }
}
