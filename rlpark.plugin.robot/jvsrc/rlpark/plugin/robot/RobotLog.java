package rlpark.plugin.robot;

import rlpark.plugin.robot.sync.ObservationVersatileArray;

public interface RobotLog extends RobotProblem {
  boolean hasNextStep();

  ObservationVersatileArray nextStep();
}
