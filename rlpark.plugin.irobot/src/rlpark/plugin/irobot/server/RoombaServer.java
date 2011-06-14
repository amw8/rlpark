package rlpark.plugin.irobot.server;

import rlpark.plugin.irobot.internal.descriptors.RoombaSerialDescriptor;

public class RoombaServer extends IRobotServer {
  public RoombaServer(int port, String serialPortPath) {
    super(port, serialPortPath, new RoombaSerialDescriptor());
  }
}
