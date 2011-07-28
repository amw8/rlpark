package rlpark.plugin.robot.disco.datagroup;

import java.awt.Color;

import rlpark.plugin.robot.disco.datatype.LiteByteBuffer;
import rlpark.plugin.robot.disco.drops.Drop;
import rlpark.plugin.robot.disco.drops.DropColor;
import rlpark.plugin.robot.disco.drops.DropData;

public class DropColorGroup extends DataObjectGroup<Color> {

  public DropColorGroup(Drop drop) {
    super(drop);
  }

  @Override
  protected boolean isDataSelected(DropData data) {
    return data instanceof DropColor;
  }

  @Override
  protected void setValue(DropData dropData, Color value) {
    ((DropColor) dropData).set(value);
  }

  @Override
  protected Color getValue(LiteByteBuffer byteBuffer, DropData dropData) {
    return ((DropColor) dropData).color(byteBuffer);
  }
}