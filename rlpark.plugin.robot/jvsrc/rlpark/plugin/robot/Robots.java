package rlpark.plugin.robot;

import java.util.Arrays;

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

  // ---
  public static byte[] toByta(long data) {

    return new byte[] { (byte) ((data >> 56) & 0xff), (byte) ((data >> 48) & 0xff), (byte) ((data >> 40) & 0xff),
        (byte) ((data >> 32) & 0xff), (byte) ((data >> 24) & 0xff), (byte) ((data >> 16) & 0xff),
        (byte) ((data >> 8) & 0xff), (byte) ((data >> 0) & 0xff), };
  }

  public static byte[] toByta(double data) {
    return toByta(Double.doubleToRawLongBits(data));
  }

  public static byte[] toByta(double[] data) {
    if (data == null)
      return null;
    // ----------
    byte[] byts = new byte[data.length * 8];
    for (int i = 0; i < data.length; i++)
      System.arraycopy(toByta(data[i]), 0, byts, i * 8, 8);
    return byts;
  }

  // --

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

  public static byte[] toByteArray(double[] current) {
    int srcLength = current.length;
    byte[] dst = new byte[srcLength << 2];

    int j = 0;
    for (int i = 0; i < srcLength; i++) {
      int x = (int) current[i];
      byte[] b = new byte[] { (byte) (x >>> 24), (byte) (x >>> 16), (byte) (x >>> 8), (byte) x };
      System.arraycopy(b, 0, dst, j, 4);
      j += 4;
    }
    return dst;
  }

  public static double[] byteArrayToDoubleArray(byte[] b) {
    double[] ret = new double[b.length / 4];

    int j = 0;
    for (int i = 0; i < ret.length; i++) {
      ret[i] = byteArrayToInt(Arrays.copyOfRange(b, j, j + 4));
      j += 4;
    }
    return ret;
  }

  public static final int byteArrayToInt(byte[] b) {
    return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8) + (b[3] & 0xFF);
  }

  public static void main(String[] args) {
    int noMatch = 0;
    int numTests = 100;
    int numDim = 54;

    System.out.println("Test converting int[] to byte[] and back. Testing " + numTests + " arrays of length " + numDim);
    for (int i = 0; i < numTests; i++) {
      double[] num = new double[numDim];
      for (int j = 0; j < numDim; j++)
        num[j] = (int) (Math.random() * 256);

      byte[] ba = toByteArray(num);
      double[] newNum = byteArrayToDoubleArray(ba);

      if (!Arrays.equals(num, newNum))
        noMatch++;

    }

    if (noMatch > 0)
      System.out.println(noMatch + " errors in Conversion");
    else
      System.out.println("No errors in test");

  }


}
