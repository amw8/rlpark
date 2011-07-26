package rlpark.plugin.robot.sync;

import rlpark.plugin.robot.disco.datatype.LightByteBuffer;

public interface ScalarInterpreter {
  void interpret(LightByteBuffer buffer, double[] values);

  int size();
}
