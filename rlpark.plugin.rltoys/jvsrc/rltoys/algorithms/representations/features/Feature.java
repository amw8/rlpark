package rltoys.algorithms.representations.features;

import java.util.List;

import rltoys.math.representations.Function;

public interface Feature extends Function {
  void update();

  List<Function> dependencies();
}
