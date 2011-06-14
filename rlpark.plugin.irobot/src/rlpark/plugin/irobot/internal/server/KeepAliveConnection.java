package rlpark.plugin.irobot.internal.server;

import rlpark.plugin.irobot.internal.descriptors.IRobotSerialDescriptor;

public class KeepAliveConnection {
  private final IRobotDiscoServer iRobotServer;
  private final SerialLinkWatchDog watchdog;
  private final byte[] messageOnNoClient;

  public KeepAliveConnection(IRobotDiscoServer iRobotServer) {
    this.iRobotServer = iRobotServer;
    IRobotSerialDescriptor descriptor = iRobotServer.connection().descriptor();
    messageOnNoClient = descriptor.messageOnNoClient();
    watchdog = new SerialLinkWatchDog(iRobotServer.connection(), descriptor);
  }

  protected void sendNoClientMessage() {
    iRobotServer.connection().sendMessage(messageOnNoClient);
  }

  public SerialLinkWatchDog watchdog() {
    return watchdog;
  }
}
