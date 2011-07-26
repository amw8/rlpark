package rlpark.plugin.robot.disco.drops;


import rlpark.plugin.robot.disco.datatype.GrayCodeConverter;
import rlpark.plugin.robot.disco.datatype.LightByteBuffer;
import rlpark.plugin.robot.disco.datatype.Ranged;
import rlpark.plugin.robot.disco.datatype.ScalarReader;
import rltoys.math.GrayCode;
import rltoys.math.ranges.Range;

public class DropShortUnsigned extends DropData implements Ranged, ScalarReader, GrayCodeConverter {
  private int value;

  public DropShortUnsigned(String label) {
    this(label, -1);
  }

  public DropShortUnsigned(String label, int index) {
    super(label, false, index);
  }

  @Override
  public DropData clone(String label, int index) {
    return new DropShortUnsigned(label, index);
  }

  @Override
  public int getInt(LightByteBuffer buffer) {
    return buffer.getShort(index) & 0xffff;
  }

  @Override
  public void convert(LightByteBuffer source, LightByteBuffer target) {
    value = getInt(source);
    value = GrayCode.shortToGrayCode((short) value);
    putData(target);
  }

  @Override
  public void putData(LightByteBuffer buffer) {
    buffer.putShort((short) value);
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @Override
  public int size() {
    return 2;
  }

  @Override
  public Range range() {
    return new Range(0, 65535);
  }

  @Override
  public double getDouble(LightByteBuffer buffer) {
    return getInt(buffer);
  }
}
