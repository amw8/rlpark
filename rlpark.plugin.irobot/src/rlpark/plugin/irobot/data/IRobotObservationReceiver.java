package rlpark.plugin.irobot.data;

import rlpark.plugin.robot.sync.ObservationReceiver;
import rltoys.environments.envio.observations.Legend;
import rltoys.math.ranges.Range;

public interface IRobotObservationReceiver extends ObservationReceiver {
  void sendMessage(byte[] bytes);

  Legend legend();

  Range[] ranges();
}
