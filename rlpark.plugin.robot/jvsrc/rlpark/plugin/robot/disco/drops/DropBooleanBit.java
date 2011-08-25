package rlpark.plugin.robot.disco.drops;


import rlpark.plugin.robot.disco.datatype.LiteByteBuffer;
import rlpark.plugin.robot.disco.datatype.ScalarReader;
import rltoys.math.ranges.Range;

public class DropBooleanBit extends DropData implements ScalarReader {
  public DropBooleanBit(String label) {
    this(label, -1);
  }

  public DropBooleanBit(String label, int index) {
    super(label, false, index);
  }

  @Override
  public DropData clone(String label, int index) {
    return new DropBooleanBit(label, index);
  }

  @Override
  public int getInt(LiteByteBuffer buffer) {
    return buffer.get(index);
  }

  @Override
  public void putData(LiteByteBuffer buffer) {
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public Range range() {
    return new Range(0, 1);
  }

  @Override
  public double getDouble(LiteByteBuffer buffer) {
    return getInt(buffer);
  }
}
