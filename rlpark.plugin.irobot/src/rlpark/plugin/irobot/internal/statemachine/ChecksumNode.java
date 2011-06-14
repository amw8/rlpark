package rlpark.plugin.irobot.internal.statemachine;

import java.util.List;


public class ChecksumNode implements SerialLinkNode {
  private final List<SerialLinkNode> checksumeds;
  private int checksum = 0;

  public ChecksumNode(List<SerialLinkNode> serialLinkNodes) {
    checksumeds = serialLinkNodes;
  }

  @Override
  public void start() {
  }

  @Override
  public void step(Byte b) {
    checksum = 0x00ff & b;
  }

  @Override
  public boolean isDone() {
    return true;
  }

  public boolean isPacketValid() {
    int sum = checksum;
    for (SerialLinkNode checksumed : checksumeds)
      sum += checksumed.sum();
    if (sum % 256 != 0)
      System.err.println(String.format("Warning: checksum error (%d->%d)", sum, sum % 256));
    return sum % 256 == 0;
  }

  public void reset() {
    checksum = (byte) 1;
  }

  public static int sum(byte[] data) {
    int result = 0;
    for (byte b : data)
      result += b & 0x00ff;
    return result;
  }

  @Override
  public int sum() {
    return checksum;
  }
}
