package rlpark.plugin.robot.disco.drops;

import java.nio.ByteBuffer;


public class DropEndBit extends DropData {
  public DropEndBit(String label) {
    super(label, true);
  }

  @Override
  public DropData clone(String label, int index) {
    return new DropEndBit(label);
  }

  @Override
  public void putData(ByteBuffer buffer) {
  }

  @Override
  public int size() {
    return ByteSize;
  }

}
