package rlpark.plugin.robot.sync;


public class ObservationVersatile {
  private final double[] doubleValues;
  private final byte[] byteValues;

  public ObservationVersatile(byte[] byteValues, double[] doubleValues) {
    this.byteValues = byteValues;
    this.doubleValues = doubleValues;
  }

  public byte[] rawData() {
    return byteValues;
  }

  public double[] doubleValues() {
    return doubleValues;
  }
}
