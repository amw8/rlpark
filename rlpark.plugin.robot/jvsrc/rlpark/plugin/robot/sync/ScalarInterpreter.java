package rlpark.plugin.robot.sync;

import java.nio.ByteBuffer;

public interface ScalarInterpreter {
  void interpret(ByteBuffer buffer, double[] values);

  int size();
}
