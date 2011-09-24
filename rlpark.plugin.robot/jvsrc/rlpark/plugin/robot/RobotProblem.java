package rlpark.plugin.robot;

import rltoys.environments.envio.observations.Legend;

public interface RobotProblem {
  Legend legend();

  int observationPacketSize();
}
