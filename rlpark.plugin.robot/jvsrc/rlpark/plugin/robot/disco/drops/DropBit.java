package rlpark.plugin.robot.disco.drops;


import rlpark.plugin.robot.disco.datatype.LightByteBuffer;
import rlpark.plugin.robot.disco.datatype.Ranged;
import rlpark.plugin.robot.disco.datatype.ScalarReader;
import rltoys.math.ranges.Range;

public class DropBit extends DropData implements Ranged, ScalarReader {
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
  public int getInt(LightByteBuffer buffer) {
    byte b = buffer.get(index);
    return (b & mask) != 0 ? 1 : 0;
  }

  @Override
  public void putData(LightByteBuffer buffer) {
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
  public double getDouble(LightByteBuffer buffer) {
    return getInt(buffer);
  }
}
