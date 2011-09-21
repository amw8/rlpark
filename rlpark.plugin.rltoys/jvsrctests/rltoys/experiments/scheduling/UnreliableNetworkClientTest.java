package rltoys.experiments.scheduling;

import static rltoys.experiments.scheduling.SchedulerTestsUtils.testServerScheduler;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;

import rltoys.experiments.scheduling.internal.messages.ClassLoading;
import rltoys.experiments.scheduling.internal.queue.NetworkJobQueue;
import rltoys.experiments.scheduling.network.NetworkClient;
import rltoys.experiments.scheduling.network.ServerScheduler;
import rltoys.experiments.scheduling.schedulers.LocalScheduler;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.signals.Signal;

public class UnreliableNetworkClientTest {
  static int nbUnreliableQueue = 0;

  static class UnreliableNetworkQueue extends NetworkJobQueue {
    private final Random random = new Random(nbUnreliableQueue);
    private boolean terminated = false;
    public final Signal<NetworkJobQueue> onClose = new Signal<NetworkJobQueue>();

    public UnreliableNetworkQueue(String serverHostName, int port) throws UnknownHostException, IOException {
      super(serverHostName, port);
      nbUnreliableQueue++;
    }

    @Override
    synchronized public Runnable request() {
      if (terminated)
        return null;
      Runnable runnable = super.request();
      if (runnable != null && random.nextFloat() < .2) {
        terminated = true;
        onClose.fire(this);
        return null;
      }
      return runnable;
    }
  }

  @BeforeClass
  static public void junitMode() {
    ClassLoading.enableForceNetworkClassResolution();
    // Messages.disableVerbose();
    // Messages.enableDebug();
  }

  @Test(timeout = SchedulerTestsUtils.Timeout)
  public void testServerSchedulerWithMultipleClients() throws IOException {
    ServerScheduler scheduler = new ServerScheduler(SchedulerTestsUtils.Port, 0);
    scheduler.start();
    startUnreliableClients(5);
    testServerScheduler(scheduler, 1000);
    scheduler.dispose();
  }

  public static void startUnreliableClients(int nbClients) throws UnknownHostException, IOException {
    for (int i = 0; i < nbClients; i++) {
      UnreliableNetworkQueue queue = new UnreliableNetworkQueue(SchedulerTestsUtils.Localhost, SchedulerTestsUtils.Port);
      LocalScheduler localScheduler = new LocalScheduler(queue);
      final NetworkClient client = new NetworkClient(localScheduler);
      queue.onClose.connect(new Listener<NetworkJobQueue>() {
        @Override
        public void listen(NetworkJobQueue eventInfo) {
          try {
            startUnreliableClients(1);
            client.dispose();
          } catch (Exception e) {
          }
        }
      });
      client.start();
    }
  }
}
