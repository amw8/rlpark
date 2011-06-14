package rlpark.plugin.robot.statemachine;


public interface StateNode<T> {
  void start();

  void step(T step);

  boolean isDone();
}
