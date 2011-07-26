package rlpark.plugin.robot.disco.io;

import java.io.Serializable;
import java.nio.ByteOrder;

import rlpark.plugin.robot.disco.datatype.LightByteBuffer;

public class DiscoPacket implements Serializable {
  private static final long serialVersionUID = -4603810414251911563L;

  public enum Direction {
    Send,
    Recv
  }

  public final String order;
  public final byte[] buffer;
  public final String name;
  public final long time;
  public final Direction direction;
  private transient LightByteBuffer byteBuffer = null;

  DiscoPacket(Direction direction, String name, LightByteBuffer buffer) {
    this(direction, name, buffer.order(), buffer.array());
    this.byteBuffer = buffer;
  }

  DiscoPacket(Direction direction, String name, ByteOrder order, byte[] byteArray) {
    this.name = name;
    this.direction = direction;
    this.order = order.toString();
    this.buffer = byteArray;
    time = System.currentTimeMillis();
  }

  public ByteOrder order() {
    return "BIG_ENDIAN".equals(order) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
  }

  public LightByteBuffer byteBuffer() {
    if (byteBuffer != null)
      return byteBuffer;
    byteBuffer = new LightByteBuffer(buffer, order());
    return byteBuffer;
  }
}
