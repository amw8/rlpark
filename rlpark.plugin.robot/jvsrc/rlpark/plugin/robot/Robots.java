package rlpark.plugin.robot;

import rlpark.plugin.robot.disco.datatype.LiteByteBuffer;
import rlpark.plugin.robot.sync.ObservationVersatile;
import rlpark.plugin.robot.sync.ScalarInterpreter;
import rltoys.math.GrayCode;
import rltoys.math.vector.BVector;
import rltoys.math.vector.BinaryVector;

public class Robots {

  public static double[] toDoubles(ObservationVersatile[] obs) {
    ObservationVersatile last = last(obs);
    if (last == null)
      return null;
    return last.doubleValues();
  }

  public static ObservationVersatile last(ObservationVersatile[] obs) {
    if (obs == null || obs.length == 0)
      return null;
    return obs[obs.length - 1];
  }

  public static BinaryVector toRawBinary(ObservationVersatile[] obs) {
    ObservationVersatile last = last(obs);
    if (last == null)
      return null;
    return BVector.toBinary(last.rawData());
  }


  public static BinaryVector toGrayCodeBinary(ObservationVersatile[] obs) {
    ObservationVersatile last = last(obs);
    if (last == null)
      return null;
    return BVector.toBinary(GrayCode.toGrayCode(last.rawData()));
  }

  static public ObservationVersatile createObservation(LiteByteBuffer buffer, ScalarInterpreter interpreter) {
    double[] doubleValues = new double[interpreter.size()];
    interpreter.interpret(buffer, doubleValues);
    return new ObservationVersatile(buffer.array().clone(), doubleValues);
  }
}
