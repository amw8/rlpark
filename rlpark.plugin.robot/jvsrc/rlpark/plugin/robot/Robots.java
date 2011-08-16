package rlpark.plugin.robot;

import rlpark.plugin.robot.disco.datatype.LiteByteBuffer;
import rlpark.plugin.robot.sync.ObservationVersatile;
import rlpark.plugin.robot.sync.ScalarInterpreter;
import rltoys.math.GrayCode;
import rltoys.math.vector.BinaryVector;
import rltoys.math.vector.implementations.BVector;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

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

  static public ObservationVersatile createObservation(long time, LiteByteBuffer buffer, ScalarInterpreter interpreter) {
    double[] doubleValues = new double[interpreter.size()];
    interpreter.interpret(buffer, doubleValues);
    return new ObservationVersatile(time, buffer.array().clone(), doubleValues);
  }

  static public void addToMonitor(DataMonitor monitor, final RobotProblem problem) {
    for (String label : problem.legend().getLabels()) {
      final int obsIndex = problem.legend().indexOf(label);
      monitor.add(label, 0, new Monitored() {
        @Override
        public double monitoredValue() {
          double[] obs = toDoubles(problem.lastReceivedRawObs());
          if (obs == null)
            return -1;
          return obs[obsIndex];
        }
      });
    }
  }
}
