package rlpark.plugin.irobot.data;

public class RoombaLeds {
  public boolean dirt = true;
  public boolean spot = false;
  public boolean dock = false;
  public int cleanColor = 0;
  public int intensity = 0;

  public RoombaLeds() {
  }

  public RoombaLeds(boolean dirt, boolean spot, boolean dock, int cleanColor, int intensity) {
    this.dirt = dirt;
    this.spot = spot;
    this.dock = dock;
    this.cleanColor = cleanColor;
    this.intensity = intensity;
  }
}
