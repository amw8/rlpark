package rlpark.plugin.robot.sync;


public class ObservationVersatile {
  private final long timestamp;
  private final double[] doubleValues;
  private final byte[] byteValues;

  public ObservationVersatile(long timestamp, byte[] byteValues, double[] doubleValues) {
    this.timestamp = timestamp;
    this.byteValues = byteValues;
    this.doubleValues = doubleValues;
  }

  public byte[] rawData() {
    return byteValues;
  }

  public double[] doubleValues() {
    return doubleValues;
  }

  public long time() {
    return timestamp;
  }
}
