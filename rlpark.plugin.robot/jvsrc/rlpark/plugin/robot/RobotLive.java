package rlpark.plugin.robot;


import rlpark.plugin.robot.sync.ObservationVersatile;
import rlpark.plugin.robot.sync.ObservationVersatileArray;
import zephyr.plugin.core.api.synchronization.Closeable;

public interface RobotLive extends Closeable, RobotProblem {
  ObservationVersatileArray waitNewRawObs();

  ObservationVersatile lastReceivedRawObs();
}
