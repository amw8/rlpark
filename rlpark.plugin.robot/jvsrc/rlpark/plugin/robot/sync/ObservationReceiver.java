package rlpark.plugin.robot.sync;

public interface ObservationReceiver {
  void initialize();

  int packetSize();

  ObservationVersatile waitForData();

  boolean isClosed();
}
