package rltoys.experiments.scheduling.network;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.interfaces.JobQueue;
import rltoys.experiments.scheduling.interfaces.Scheduler;
import rltoys.experiments.scheduling.network.internal.LocalQueue;
import rltoys.experiments.scheduling.network.internal.Messages;
import rltoys.experiments.scheduling.schedulers.LocalScheduler;
import zephyr.plugin.core.api.signals.Listener;

public class ServerScheduler implements Scheduler {
  static final public int DefaultPort = 5000;
  static public boolean clientVerbose = true;
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
  private final Set<SocketClient> clients = new HashSet<SocketClient>();

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
    printAboutClient(String.format("%d client(s) for %d remaining jobs", clients.size(), localQueue.nbRemainingJobs()));
  }

  static private void printAboutClient(String message) {
    if (!clientVerbose)
      return;
    System.out.println(message);
  }

  @Override
  public List<Runnable> runAll() {
    start();
    List<Runnable> result = LocalQueue.waitAllDone(localQueue);
    if (localScheduler != null) {
      Throwable exceptionOccured = localScheduler.exceptionOccured();
      if (exceptionOccured != null)
        throw new RuntimeException(exceptionOccured);
    }
    return result;
  }

  public void start() {
    if (!serverThread.isAlive())
      serverThread.start();
    if (localScheduler != null)
      localScheduler.start();
    for (SocketClient clientScheduler : clients)
      clientScheduler.wakeUp();
  }

  @Override
  public void add(Runnable runnable, Listener<JobDoneEvent> listener) {
    if (!(runnable instanceof Serializable))
      throw new RuntimeException("Job needs to be serializable");
    localQueue.add(runnable, listener);
  }

  public void removeClient(SocketClient socketClient) {
    clients.remove(socketClient);
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
