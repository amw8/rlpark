package rlpark.plugin.robot.disco.drops;


import rlpark.plugin.robot.disco.datatype.GrayCodeConverter;
import rlpark.plugin.robot.disco.datatype.LightByteBuffer;
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
  public int getInt(LightByteBuffer buffer) {
    return buffer.get(index) & 0xFF;
  }

  @Override
  public void convert(LightByteBuffer source, LightByteBuffer target) {
    value = getInt(source);
    value = GrayCode.byteToGrayCode((byte) value);
    putData(target);
  }

  @Override
  public void putData(LightByteBuffer buffer) {
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
  public double getDouble(LightByteBuffer buffer) {
    return getInt(buffer);
  }
}
