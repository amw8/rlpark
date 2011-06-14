package rlpark.plugin.irobot.internal.server;

import gnu.io.SerialPortEvent;

import java.io.IOException;

import rlpark.plugin.irobot.internal.descriptors.IRobotSerialDescriptor;
import rlpark.plugin.irobot.internal.irobot.IRobotSerialConnection;
import rlpark.plugin.irobot.internal.serial.SerialPortToRobot;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Chrono;

public class SerialLinkWatchDog implements Runnable, Listener<SerialPortToRobot> {
  static private final int CheckingTime = 5000; // (ms)
  private final IRobotSerialConnection connection;
  private final IRobotSerialDescriptor descriptor;
  private Thread thread;
  private final Chrono lastSerialEventTime = new Chrono();

  public SerialLinkWatchDog(IRobotSerialConnection connection, IRobotSerialDescriptor descriptor) {
    this.connection = connection;
    this.descriptor = descriptor;
  }

  @Override
  public void run() {
    while (!connection.isClosed()) {
      try {
        Thread.sleep(CheckingTime);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (lastSerialEventTime.getCurrentMillis() > CheckingTime) {
        System.err.println("Warning: serial link is soundless. Reinitialising communication...");
        try {
          descriptor.initializeRobotCommunication(connection.stateMachine().serialPort);
        } catch (IOException e) {
          e.printStackTrace();
          SerialPortToRobot.fatalError("Error while reinitialising the communication with the robot");
        }
      }
    }
  }

  public void start() {
    connection.stateMachine().serialPort.register(SerialPortEvent.DATA_AVAILABLE, this);
    thread = new Thread(this, "Serial Link Watch Dog");
    thread.setDaemon(true);
    thread.start();
  }

  @Override
  public void listen(SerialPortToRobot eventInfo) {
    lastSerialEventTime.start();
  }
}
