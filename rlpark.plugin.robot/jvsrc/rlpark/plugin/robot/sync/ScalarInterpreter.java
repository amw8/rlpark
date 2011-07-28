package rlpark.plugin.robot.sync;

import rlpark.plugin.robot.disco.datatype.LiteByteBuffer;

public interface ScalarInterpreter {
  void interpret(LiteByteBuffer buffer, double[] values);

  int size();
}
