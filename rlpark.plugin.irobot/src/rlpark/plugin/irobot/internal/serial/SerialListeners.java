package rlpark.plugin.irobot.internal.serial;

import zephyr.plugin.core.api.signals.Listener;

public class SerialListeners {

  static interface SerialInputCondition {
    boolean isSatisfied(SerialPortToRobot serialPort);
  }

  static public class WakeUpThread implements Listener<SerialPortToRobot> {
    private final SerialInputCondition condition;
    private int nbDataAvailable = 0;

    public WakeUpThread(SerialInputCondition condition) {
      this.condition = condition;
    }

    @Override
    public void listen(SerialPortToRobot serialPort) {
      if (!condition.isSatisfied(serialPort))
        return;
      synchronized (serialPort) {
        nbDataAvailable = serialPort.available();
        serialPort.notify();
      }
    }

    public int nbDataAvailable() {
      return nbDataAvailable;
    }
  }

  static public class AlwaysWakeUpThread extends WakeUpThread {
    public AlwaysWakeUpThread() {
      super(new SerialInputCondition() {
        @Override
        public boolean isSatisfied(SerialPortToRobot serialPort) {
          return true;
        }
      });
    }
  }

  static public class ReadWhenArriveAndWakeUp extends AlwaysWakeUpThread {
    private String message;

    @Override
    public void listen(SerialPortToRobot serialPort) {
      message = serialPort.getAvailableAsString();
      super.listen(serialPort);
    }

    public String message() {
      return message;
    }
  }

  static public class PrintReceived implements Listener<SerialPortToRobot> {
    @Override
    public void listen(SerialPortToRobot serialPort) {
      String available = serialPort.getAvailableAsString();
      if (available.length() > 0)
        System.out.println("Recv: " + available);
    }
  }
}
