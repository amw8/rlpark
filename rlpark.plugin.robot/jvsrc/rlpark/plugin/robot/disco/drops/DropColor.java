package rlpark.plugin.robot.disco.drops;

import java.awt.Color;
import java.nio.ByteBuffer;

public class DropColor extends DropData {

  private Color color = Color.YELLOW;

  public DropColor(String label) {
    this(label, -1);
  }

  public DropColor(String label, int index) {
    super(label, false, index);
  }

  public void set(Color color) {
    this.color = color == null ? Color.YELLOW : color;
  }

  public Color color(ByteBuffer buffer) {
    short red = (short) (0xFF & buffer.get(index));
    short green = (short) (0xFF & buffer.get(index + DropData.ByteSize));
    short blue = (short) (0xFF & buffer.get(index + DropData.ByteSize * 2));
    return new Color(red, green, blue);
  }

  @Override
  public DropData clone(String label, int index) {
    return new DropColor(label, index);
  }

  @Override
  public void putData(ByteBuffer buffer) {
    buffer.put((byte) color.getRed());
    buffer.put((byte) color.getGreen());
    buffer.put((byte) color.getBlue());
  }

  @Override
  public int size() {
    return ByteSize * 3;
  }
}
