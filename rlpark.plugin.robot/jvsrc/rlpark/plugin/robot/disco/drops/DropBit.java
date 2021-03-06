package rlpark.plugin.robot.disco.drops;


import rlpark.plugin.robot.disco.datatype.LiteByteBuffer;
import rlpark.plugin.robot.disco.datatype.ScalarReader;
import rltoys.math.ranges.Range;

public class DropBit extends DropData implements ScalarReader {
  private final byte bitIndex;
  private final byte mask;

  public DropBit(String label, int bitIndex) {
    this(label, bitIndex, -1);
  }

  public DropBit(String label, int bitIndex, int index) {
    super(label, false, index);
    this.bitIndex = (byte) bitIndex;
    mask = (byte) (0x01 << bitIndex);
  }

  @Override
  public DropData clone(String label, int index) {
    return new DropBit(label, bitIndex, index);
  }

  @Override
  public int getInt(LiteByteBuffer buffer) {
    byte b = buffer.get(index);
    return (b & mask) != 0 ? 1 : 0;
  }

  @Override
  public void putData(LiteByteBuffer buffer) {
  }

  @Override
  public int size() {
    return 0;
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
