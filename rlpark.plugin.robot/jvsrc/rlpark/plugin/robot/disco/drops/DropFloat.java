package rlpark.plugin.robot.disco.drops;

import java.nio.ByteBuffer;

import rlpark.plugin.robot.disco.datatype.ScalarReader;
import rlpark.plugin.robot.disco.datatype.ScalarWriter;

public class DropFloat extends DropData implements ScalarReader, ScalarWriter {
  protected float value;

  public DropFloat(String label) {
    this(label, -1);
  }

  protected DropFloat(String label, int index) {
    super(label, false, index);
  }

  public DropFloat(String label, float value, int index) {
    super(label, true, index);
    this.value = value;
  }

  @Override
  public DropData clone(String label, int index) {
    if (readOnly)
      return new DropFloat(label, value, index);
    return new DropFloat(label, index);
  }

  @Override
  public int getInt(ByteBuffer buffer) {
    return (int) getDouble(buffer);
  }

  @Override
  public void putData(ByteBuffer buffer) {
    buffer.putFloat(value);
  }

  @Override
  public int size() {
    return FloatSize;
  }

  @Override
  public double getDouble(ByteBuffer buffer) {
    return buffer.getFloat(index);
  }

  @Override
  public void setDouble(double value) {
    this.value = (float) value;
  }
}
