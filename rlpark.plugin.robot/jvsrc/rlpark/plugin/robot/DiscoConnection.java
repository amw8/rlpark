package rlpark.plugin.robot;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.robot.disco.datagroup.DropScalarGroup;
import rlpark.plugin.robot.disco.drops.Drop;
import rlpark.plugin.robot.disco.io.DiscoPacket;
import rlpark.plugin.robot.disco.io.DiscoSocket;
import rlpark.plugin.robot.sync.ObservationReceiver;
import rlpark.plugin.robot.sync.ObservationVersatile;
import rltoys.environments.envio.observations.Legend;
import zephyr.plugin.core.api.signals.Listener;


public class DiscoConnection implements ObservationReceiver {
  protected final Drop sensorDrop;
  protected final DropScalarGroup sensorGroup;
  protected final String hostname;
  protected final int port;
  protected DiscoSocket socket;
  private final ByteOrder byteOrdering;
  private final List<Listener<DiscoPacket>> packetListeners = new ArrayList<Listener<DiscoPacket>>();

  public DiscoConnection(String hostname, int port, Drop sensorDrop) {
    this(hostname, port, sensorDrop, ByteOrder.BIG_ENDIAN);
  }

  public DiscoConnection(String hostname, int port, Drop sensorDrop, ByteOrder order) {
    this.hostname = hostname;
    this.port = port;
    this.sensorDrop = sensorDrop;
    sensorGroup = new DropScalarGroup(sensorDrop);
    byteOrdering = order;
  }

  @Override
  public void initialize() {
    try {
      socket = new DiscoSocket(hostname, port, byteOrdering);
      for (Listener<DiscoPacket> listener : packetListeners)
        socket.onPacket.connect(listener);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void addPacketListener(Listener<DiscoPacket> listener) {
    packetListeners.add(listener);
    if (socket != null)
      socket.onPacket.connect(listener);
  }

  @Override
  synchronized public ObservationVersatile waitForData() {
    if (isClosed())
      return null;
    DiscoPacket packet = null;
    try {
      packet = socket.recv();
    } catch (Throwable e) {
      e.printStackTrace();
      close();
    }
    return packet != null ? Robots.createObservation(System.currentTimeMillis(), packet.byteBuffer(), sensorGroup)
        : null;
  }

  @Override
  public boolean isClosed() {
    return socket == null || socket.isSocketClosed();
  }

  public DiscoSocket socket() {
    return socket;
  }

  synchronized public void close() {
    socket.close();
    notifyAll();
  }

  public Legend legend() {
    return sensorGroup.legend();
  }

  public Drop sensorDrop() {
    return sensorDrop;
  }

  @Override
  public int packetSize() {
    return sensorDrop.dataSize();
  }
}