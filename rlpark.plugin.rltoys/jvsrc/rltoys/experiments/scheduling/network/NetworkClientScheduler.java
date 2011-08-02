package rltoys.experiments.scheduling.network;

import java.io.IOException;
import java.net.UnknownHostException;

import rltoys.experiments.scheduling.interfaces.JobQueue;
import rltoys.experiments.scheduling.network.internal.NetworkJobQueue;
import rltoys.experiments.scheduling.schedulers.LocalScheduler;
import zephyr.plugin.core.api.signals.Listener;

public class NetworkClientScheduler {
  private final LocalScheduler localScheduler;
  private final NetworkJobQueue clientScheduler;

  public NetworkClientScheduler(String serverHost, int port) throws UnknownHostException, IOException {
    this(new LocalScheduler(createJobQueue(serverHost, port)));
  }

  public NetworkClientScheduler(int nbThread, String serverHost, int port) throws UnknownHostException, IOException {
    this(new LocalScheduler(nbThread, createJobQueue(serverHost, port)));
  }

  public NetworkClientScheduler(final LocalScheduler localScheduler) {
    this.localScheduler = localScheduler;
    clientScheduler = (NetworkJobQueue) localScheduler.queue();
    clientScheduler.onJobReceived.connect(new Listener<JobQueue>() {
      @Override
      public void listen(JobQueue eventInfo) {
        localScheduler.start();
      }
    });
  }

  private static NetworkJobQueue createJobQueue(String serverHost, int port) throws UnknownHostException, IOException {
    return new NetworkJobQueue(serverHost, port);
  }

  public void start() {
    localScheduler.start();
  }

  public void run() {
    while (!clientScheduler.isClosed()) {
      localScheduler.runAll();
    }
  }

  public void dispose() {
    localScheduler.dispose();
    clientScheduler.close();
  }

  public static void main(String[] args) throws UnknownHostException, IOException {
    if (args.length != 1) {
      System.err.println("Usage: java -jar <jarfile.jar> <hostname:port>");
      System.exit(1);
    }
    int portSeparator = args[0].lastIndexOf(":");
    String hostname = portSeparator >= 0 ? args[0].substring(0, portSeparator) : args[0];
    int port = portSeparator >= 0 ? Integer.parseInt(args[0].substring(portSeparator)) : ServerScheduler.DefaultPort;
    NetworkClientScheduler scheduler = new NetworkClientScheduler(hostname, port);
    scheduler.run();
    scheduler.dispose();
  }
}
