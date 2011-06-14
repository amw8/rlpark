package rlpark.plugin.irobot.robots;

import rltoys.environments.envio.observations.Legend;
import zephyr.plugin.core.api.synchronization.Closeable;

public interface IRobotProblem extends Closeable {
  Legend legend();

  double[] lastReceivedObs();

  int observationPacketSize();
}
