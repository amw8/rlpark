package rlpark.plugin.robot.disco.datagroup;

import java.nio.ByteBuffer;

import rlpark.plugin.robot.disco.datatype.ScalarReader;
import rlpark.plugin.robot.disco.datatype.ScalarWriter;
import rlpark.plugin.robot.disco.drops.Drop;
import rlpark.plugin.robot.disco.drops.DropData;
import rlpark.plugin.robot.sync.ScalarInterpreter;

public class DropScalarGroup extends DataGroup implements ScalarInterpreter {
  public DropScalarGroup(Drop drop) {
    this("", drop);
  }

  public DropScalarGroup(String prefix, Drop drop) {
    super(prefix, drop);
  }

  @Override
  protected boolean isDataSelected(DropData data) {
    return data instanceof ScalarReader;
  }

  public void set(int[] values) {
    double[] doubleValues = new double[values.length];
    for (int i = 0; i < doubleValues.length; i++)
      doubleValues[i] = values[i];
    set(doubleValues);
  }

  public void set(double... values) {
    assert values.length == dropDatas.length;
    for (int i = 0; i < values.length; i++)
      ((ScalarWriter) dropDatas[i]).setDouble(values[i]);
  }

  @Override
  public void interpret(ByteBuffer buffer, double[] values) {
    assert values.length == dropDatas.length;
    for (int i = 0; i < values.length; i++)
      values[i] = ((ScalarReader) dropDatas[i]).getDouble(buffer);
  }

  public void interpret(ByteBuffer buffer, int[] values) {
    assert values.length == dropDatas.length;
    for (int i = 0; i < values.length; i++)
      values[i] = ((ScalarReader) dropDatas[i]).getInt(buffer);
  }
}