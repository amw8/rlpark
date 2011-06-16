package rlpark.plugin.robot;

import rlpark.plugin.robot.sync.ObservationVersatile;

public class Robots {

  public static double[] toDoubles(ObservationVersatile[] o_tp1) {
    ObservationVersatile last = last(o_tp1);
    if (last == null)
      return null;
    return last.doubleValues();
  }

  public static ObservationVersatile last(ObservationVersatile[] o_tp1) {
    if (o_tp1 == null || o_tp1.length == 0)
      return null;
    return o_tp1[o_tp1.length - 1];
  }

}
