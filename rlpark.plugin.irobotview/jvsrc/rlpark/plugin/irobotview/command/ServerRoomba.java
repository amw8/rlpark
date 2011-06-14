package rlpark.plugin.irobotview.command;

import rlpark.plugin.irobot.server.IRobotServer;
import rlpark.plugin.irobot.server.RoombaServer;


public class ServerRoomba extends StartServerCommand {
  @Override
  protected IRobotServer newServer(int port, String serialPortPath) {
    return new RoombaServer(port, serialPortPath);
  }
}
