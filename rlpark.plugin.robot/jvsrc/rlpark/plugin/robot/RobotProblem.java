package rlpark.plugin.robot;

import rlpark.plugin.robot.sync.ObservationVersatile;
import rltoys.environments.envio.observations.Legend;
import zephyr.plugin.core.api.synchronization.Closeable;

public interface RobotProblem extends Closeable {
  Legend legend();

  int observationPacketSize();

  ObservationVersatile[] waitNewRawObs();

  ObservationVersatile[] lastReceivedRawObs();
}
