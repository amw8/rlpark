package rlpark.plugin.robot.disco.drops;


import rlpark.plugin.robot.disco.datatype.LightByteBuffer;
import rlpark.plugin.robot.disco.datatype.ScalarReader;
import rlpark.plugin.robot.disco.datatype.ScalarWriter;

public class DropInteger extends DropData implements ScalarReader, ScalarWriter {
  protected int value;

  public DropInteger(String label) {
    this(label, false, -1);
  }

  public DropInteger(String label, int value) {
    this(label, value, -1);
  }

  public DropInteger(String label, int value, int index) {
    this(label, true, index);
    this.value = value;
  }

  protected DropInteger(String label, boolean readOnly, int index) {
    super(label, readOnly, index);
  }

  @Override
  public void setDouble(double value) {
    assert !readOnly;
    this.value = (int) value;
  }

  @Override
  public DropData clone(String label, int index) {
    if (readOnly)
      return new DropInteger(label, value, index);
    return new DropInteger(label, false, index);
  }

  @Override
  public int getInt(LightByteBuffer buffer) {
    return buffer.getInt(index);
  }

  @Override
  public double getDouble(LightByteBuffer buffer) {
    return getInt(buffer);
  }

  @Override
  public void putData(LightByteBuffer buffer) {
    buffer.putInt(value);
  }

  @Override
  public int size() {
    return IntSize;
  }
}
