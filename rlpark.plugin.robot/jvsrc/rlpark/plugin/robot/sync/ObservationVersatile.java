package rlpark.plugin.robot.sync;

import java.nio.ByteBuffer;

public class ObservationVersatile {
  private final ByteBuffer buffer;
  private final double[] doubleValues;

  public ObservationVersatile(ByteBuffer buffer, ScalarInterpreter interpreter) {
    this.buffer = buffer;
    doubleValues = new double[interpreter.size()];
    interpreter.interpret(buffer, doubleValues);
  }

  public ByteBuffer buffer() {
    return buffer;
  }

  public byte[] rawData() {
    return buffer.array();
  }

  public double[] doubleValues() {
    return doubleValues;
  }
}
