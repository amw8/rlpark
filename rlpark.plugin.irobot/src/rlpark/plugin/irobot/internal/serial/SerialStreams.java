package rlpark.plugin.irobot.internal.serial;

import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class SerialStreams {
  private final InputStream input;
  private final OutputStream output;

  protected SerialStreams(SerialPort serialPort) throws IOException {
    input = serialPort.getInputStream();
    output = serialPort.getOutputStream();
  }

  synchronized public void write(int c) throws IOException {
    output.write(c);
  }

  synchronized int available() throws IOException {
    return input.available();
  }

  synchronized public int read() throws IOException {
    return input.read();
  }

  synchronized public void close() {
    try {
      input.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      output.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
