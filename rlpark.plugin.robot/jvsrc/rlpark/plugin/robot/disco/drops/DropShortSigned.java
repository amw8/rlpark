package rlpark.plugin.robot.disco.drops;

import java.nio.ByteBuffer;

import rlpark.plugin.robot.disco.datatype.GrayCodeConverter;
import rlpark.plugin.robot.disco.datatype.Ranged;
import rlpark.plugin.robot.disco.datatype.ScalarReader;
import rltoys.math.GrayCode;
import rltoys.math.ranges.Range;

public class DropShortSigned extends DropData implements Ranged, ScalarReader, GrayCodeConverter {
  private short value;

  public DropShortSigned(String label) {
    this(label, -1);
  }

  public DropShortSigned(String label, int index) {
    super(label, false, index);
  }

  @Override
  public DropData clone(String label, int index) {
    return new DropShortSigned(label, index);
  }

  @Override
  public int getInt(ByteBuffer buffer) {
    return buffer.getShort(index);
  }

  @Override
  public void convert(ByteBuffer source, ByteBuffer target) {
    value = (short) getInt(source);
    value = GrayCode.shortToGrayCode(value);
    putData(target);
  }

  @Override
  public void putData(ByteBuffer buffer) {
    buffer.putShort(value);
  }

  @Override
  public int size() {
    return 2;
  }

  @Override
  public Range range() {
    return new Range(-32768, 32767);
  }

  @Override
  public double getDouble(ByteBuffer buffer) {
    return getInt(buffer);
  }
}
