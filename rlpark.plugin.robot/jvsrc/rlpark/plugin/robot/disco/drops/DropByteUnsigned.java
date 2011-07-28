package rlpark.plugin.robot.disco.drops;


import rlpark.plugin.robot.disco.datatype.GrayCodeConverter;
import rlpark.plugin.robot.disco.datatype.LiteByteBuffer;
import rlpark.plugin.robot.disco.datatype.Ranged;
import rlpark.plugin.robot.disco.datatype.ScalarReader;
import rltoys.math.GrayCode;
import rltoys.math.ranges.Range;

public class DropByteUnsigned extends DropData implements Ranged, ScalarReader, GrayCodeConverter {
  private int value;

  public DropByteUnsigned(String label) {
    this(label, -1);
  }

  public DropByteUnsigned(String label, int index) {
    super(label, false, index);
  }

  @Override
  public DropData clone(String label, int index) {
    return new DropByteUnsigned(label, index);
  }

  @Override
  public int getInt(LiteByteBuffer buffer) {
    return buffer.get(index) & 0xFF;
  }

  @Override
  public void convert(LiteByteBuffer source, LiteByteBuffer target) {
    value = getInt(source);
    value = GrayCode.byteToGrayCode((byte) value);
    putData(target);
  }

  @Override
  public void putData(LiteByteBuffer buffer) {
    buffer.put((byte) value);
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public Range range() {
    return new Range(0, 255);
  }

  @Override
  public double getDouble(LiteByteBuffer buffer) {
    return getInt(buffer);
  }
}
