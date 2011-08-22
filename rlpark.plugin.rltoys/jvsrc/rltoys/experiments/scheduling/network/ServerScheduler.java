package rltoys.experiments.scheduling.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.interfaces.JobQueue;
import rltoys.experiments.scheduling.interfaces.Scheduler;
import rltoys.experiments.scheduling.network.internal.LocalQueue;
import rltoys.experiments.scheduling.network.internal.Messages;
import rltoys.experiments.scheduling.schedulers.LocalScheduler;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Chrono;

public class ServerScheduler implements Scheduler {
  static final public double StatPeriod = 3600 * 2;

  class JobStatListener implements Listener<JobDoneEvent> {
    private final Chrono chrono = new Chrono();
    private double lastChronoValue = 0.0;

    @Override
    public void listen(JobDoneEvent eventInfo) {
      if (chrono.getCurrentChrono() - lastChronoValue < StatPeriod)
        return;
      lastChronoValue = chrono.getCurrentChrono();
      double nbJobPerSecond = localQueue.nbJobsDone() / lastChronoValue;
      System.out.printf("%f jobs/minutes. ", nbJobPerSecond * 60);
      System.out.println();
    }
  }

  static final public int DefaultPort = 5000;
  static public boolean serverVerbose = true;
  private final Runnable acceptClientsRunnable = new Runnable() {
    @Override
    public void run() {
      Messages.println("Listening on port " + serverSocket.getLocalPort() + "...");
      while (!serverSocket.isClosed()) {
        try {
          Socket clientSocket = serverSocket.accept();
          SocketClient socketClient = new SocketClient(ServerScheduler.this, clientSocket);
          addClient(socketClient);
          socketClient.start();
        } catch (IOException e) {
        }
      }
    }
  };
  protected final LocalQueue localQueue = new LocalQueue();
  final ServerSocket serverSocket;
  private final LocalScheduler localScheduler;
  private final Thread serverThread = new Thread(acceptClientsRunnable, "AcceptThread");
  private final Set<SocketClient> clients = Collections.synchronizedSet(new HashSet<SocketClient>());

  public ServerScheduler() throws IOException {
    this(DefaultPort, LocalScheduler.getDefaultNbThreads());
  }

  public ServerScheduler(int port, int nbLocalThread) throws IOException {
    serverSocket = new ServerSocket(port);
    serverThread.setDaemon(true);
    localScheduler = nbLocalThread > 0 ? new LocalScheduler(nbLocalThread, localQueue) : null;
  }

  protected void addClient(SocketClient clientScheduler) {
    clients.add(clientScheduler);
    printClientStats();
  }

  private void printClientStats() {
    Messages.println(String.format("%d client(s)", clients.size()));
  }

  @Override
  public void runAll() {
    JobStatListener listener = new JobStatListener();
    localQueue.onJobDone().connect(listener);
    start();
    LocalQueue.waitAllDone(localQueue);
    if (localScheduler != null) {
      Throwable exceptionOccured = localScheduler.exceptionOccured();
      if (exceptionOccured != null)
        throw new RuntimeException(exceptionOccured);
    }
    localQueue.onJobDone().disconnect(listener);
  }

  public void start() {
    if (!serverThread.isAlive())
      serverThread.start();
    if (localScheduler != null)
      localScheduler.start();
    for (SocketClient clientScheduler : new ArrayList<SocketClient>(clients))
      clientScheduler.wakeUp();
  }

  public void removeClient(SocketClient socketClient) {
    clients.remove(socketClient);
    for (Runnable pendingJob : socketClient.pendingJobs())
      localQueue.requestCancel(pendingJob);
    printClientStats();
  }

  public void dispose() {
    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (localScheduler != null)
      localScheduler.dispose();
  }

  public boolean isLocalSchedulingEnabled() {
    return localScheduler != null;
  }

  @Override
  public JobQueue queue() {
    return localQueue;
  }
}
