package rlpark.plugin.robot;

import rlpark.plugin.robot.sync.ObservationVersatile;

public class Robots {

  public static double[] toDoubles(ObservationVersatile[] o_tp1) {
    return last(o_tp1).doubleValues();
  }

  public static ObservationVersatile last(ObservationVersatile[] o_tp1) {
    return o_tp1[o_tp1.length - 1];
  }

}
