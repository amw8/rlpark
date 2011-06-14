package rlpark.plugin.robot.disco.drops;

import java.nio.ByteBuffer;

public class DropTime extends DropData {
  static final public long startingTime = System.currentTimeMillis();
  static final public String TIMELABEL = "Time";
  private long time;

  public DropTime() {
    this(-1);
  }

  public DropTime(int index) {
    super(TIMELABEL, false, index);
  }

  public void set() {
    set(System.currentTimeMillis() - startingTime);
  }

  public void set(long time) {
    this.time = time;
  }

  @Override
  public int size() {
    return 2 * IntSize;
  }

  @Override
  public void putData(ByteBuffer buffer) {
    buffer.putInt((int) time);
  }

  public long getData(ByteBuffer buffer, int index) {
    // long longValue = buffer.getInt(index);
    // time = 0xFFFFFFFFL & longValue;
    return System.currentTimeMillis();
  }

  @Override
  public DropData clone(String label, int index) {
    return new DropTime(index);
  }

  @Override
  public String toString() {
    return String.valueOf(time()) + "s";
  }

  public long time() {
    return time;
  }
}
