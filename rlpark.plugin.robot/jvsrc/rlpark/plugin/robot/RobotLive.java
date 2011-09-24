package rlpark.plugin.robot;

import rlpark.plugin.robot.sync.ObservationVersatile;
import zephyr.plugin.core.api.synchronization.Closeable;

public interface RobotLive extends Closeable, RobotProblem {
  ObservationVersatile[] waitNewRawObs();

  ObservationVersatile[] lastReceivedRawObs();
}
