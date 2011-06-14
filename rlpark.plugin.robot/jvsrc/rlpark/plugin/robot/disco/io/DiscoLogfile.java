package rlpark.plugin.robot.disco.io;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;


public class DiscoLogfile implements Iterator<DiscoPacket> {
  private ObjectInputStream objin;
  private DiscoPacket nextPacket;

  public DiscoLogfile(String name) throws IOException {
    FileInputStream input = new FileInputStream(name);
    objin = new ObjectInputStream(new BufferedInputStream(input));
    nextPacket = readPacket();
  }

  private DiscoPacket readPacket() {
    if (objin == null)
      return null;
    try {
      return (DiscoPacket) objin.readObject();
    } catch (EOFException e) {
      close();
    } catch (Exception e) {
      e.printStackTrace();
      close();
    }
    return null;
  }

  @Override
  public boolean hasNext() {
    return nextPacket != null;
  }

  public void close() {
    try {
      objin.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    objin = null;
  }

  @Override
  public void remove() {
  }

  @Override
  public DiscoPacket next() {
    DiscoPacket result = nextPacket;
    nextPacket = readPacket();
    return result;
  }
}
