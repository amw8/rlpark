package rlpark.plugin.irobot.internal.statemachine;

import java.util.Arrays;

public class DataNode implements SerialLinkNode {
  private int dataRead = 0;
  final byte[] data;

  public DataNode(int dataSize) {
    data = new byte[dataSize];
  }

  @Override
  public void start() {
    dataRead = 0;
  }

  @Override
  public void step(Byte b) {
    data[dataRead] = b;
    dataRead++;
  }

  @Override
  public boolean isDone() {
    return dataRead == data.length;
  }

  public void reset() {
    Arrays.fill(data, (byte) 0);
  }

  public int length() {
    return data.length;
  }

  @Override
  public int sum() {
    return ChecksumNode.sum(data);
  }
}
