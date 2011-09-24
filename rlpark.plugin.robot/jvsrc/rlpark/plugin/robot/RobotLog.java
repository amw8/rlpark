package rlpark.plugin.robot;

import rlpark.plugin.robot.sync.ObservationVersatile;

public interface RobotLog extends RobotProblem {
  boolean hasNextStep();

  ObservationVersatile nextStep();

}
