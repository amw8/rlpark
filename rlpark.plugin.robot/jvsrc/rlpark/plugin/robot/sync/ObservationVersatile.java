package rlpark.plugin.robot.sync;

import rlpark.plugin.robot.disco.datatype.LiteByteBuffer;

public class ObservationVersatile {
  private final LiteByteBuffer buffer;
  private final double[] doubleValues;

  public ObservationVersatile(LiteByteBuffer buffer, ScalarInterpreter interpreter) {
    this.buffer = buffer;
    doubleValues = new double[interpreter.size()];
    interpreter.interpret(buffer, doubleValues);
  }

  public LiteByteBuffer buffer() {
    return buffer;
  }

  public byte[] rawData() {
    return buffer.array();
  }

  public double[] doubleValues() {
    return doubleValues;
  }
}
