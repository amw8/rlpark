package rltoys.experiments.scheduling.tests;

import static rltoys.experiments.scheduling.tests.SchedulerTestsUtils.testServerScheduler;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.BeforeClass;
import org.junit.Test;

import rltoys.experiments.scheduling.internal.messages.ClassLoading;
import rltoys.experiments.scheduling.internal.messages.Messages;
import rltoys.experiments.scheduling.internal.queue.NetworkJobQueue;
import rltoys.experiments.scheduling.network.NetworkClient;
import rltoys.experiments.scheduling.network.ServerScheduler;
import rltoys.experiments.scheduling.schedulers.LocalScheduler;

public class SchedulerNetworkUnreliableTest {
  class UnreliableNetworkQueue extends NetworkJobQueue {
    public UnreliableNetworkQueue(String serverHostName, int port) throws UnknownHostException, IOException {
      super(serverHostName, port);
    }

    @Override
    public Runnable request() {
      super.request();
      close();
      return null;
    }


  }

  private static final String Localhost = "localhost";
  private static final int Port = 5000;
  public static final int Timeout = 1000000;

  @BeforeClass
  static public void junitMode() {
    ClassLoading.enableForceNetworkClassResolution();
    Messages.disableVerbose();
    // Messages.enableDebug();
  }

  @Test(timeout = Timeout)
  public void testServerSchedulerWithMultipleClients() throws IOException {
    ServerScheduler scheduler = new ServerScheduler(Port, 0);
    scheduler.start();
    startUnreliableClients();
    NetworkClient reliableClient = new NetworkClient(1, Localhost, Port);
    reliableClient.start();
    testServerScheduler(scheduler);
    reliableClient.dispose();
    scheduler.dispose();
  }

  private void startUnreliableClients() throws UnknownHostException, IOException {
    for (int i = 0; i < 5; i++) {
      UnreliableNetworkQueue queue = new UnreliableNetworkQueue(Localhost, Port);
      final LocalScheduler localScheduler = new LocalScheduler(queue);
      NetworkClient client = new NetworkClient(localScheduler);
      client.start();
    }
  }
}
