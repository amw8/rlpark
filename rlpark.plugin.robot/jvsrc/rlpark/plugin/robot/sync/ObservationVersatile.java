package rlpark.plugin.robot.sync;

import rlpark.plugin.robot.disco.datatype.LightByteBuffer;

public class ObservationVersatile {
  private final LightByteBuffer buffer;
  private final double[] doubleValues;

  public ObservationVersatile(LightByteBuffer buffer, ScalarInterpreter interpreter) {
    this.buffer = buffer;
    doubleValues = new double[interpreter.size()];
    interpreter.interpret(buffer, doubleValues);
  }

  public LightByteBuffer buffer() {
    return buffer;
  }

  public byte[] rawData() {
    return buffer.array();
  }

  public double[] doubleValues() {
    return doubleValues;
  }
}
