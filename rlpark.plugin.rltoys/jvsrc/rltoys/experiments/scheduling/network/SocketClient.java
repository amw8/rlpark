package rltoys.experiments.scheduling.network;

import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import rltoys.experiments.scheduling.network.internal.Message;
import rltoys.experiments.scheduling.network.internal.MessageClassData;
import rltoys.experiments.scheduling.network.internal.MessageJob;
import rltoys.experiments.scheduling.network.internal.MessageRequestClass;
import rltoys.experiments.scheduling.network.internal.MessageRequestJob;
import rltoys.experiments.scheduling.network.internal.Messages;
import rltoys.experiments.scheduling.network.internal.SyncSocket;

public class SocketClient {
  private final Runnable clientRunnable = new Runnable() {
    @Override
    public void run() {
      clientReadMainLoop();
    }
  };
  private final ServerScheduler serverScheduler;
  private final SyncSocket clientSocket;
  private final Thread clientThread = new Thread(clientRunnable, "ClientThread");
  private final Map<Integer, Runnable> idtoJob = new HashMap<Integer, Runnable>();
  private boolean waitingForJob = false;
  private int id;

  public SocketClient(ServerScheduler serverScheduler, Socket clientSocket) {
    this.serverScheduler = serverScheduler;
    this.clientSocket = new SyncSocket(clientSocket);
    clientThread.setPriority(Thread.MAX_PRIORITY);
    clientThread.setDaemon(true);
  }

  public void start() {
    clientThread.start();
  }

  @SuppressWarnings("incomplete-switch")
  protected void clientReadMainLoop() {
    while (!clientSocket.isClosed()) {
      Message message = Messages.readNextMessage(clientSocket);
      if (message == null)
        break;
      switch (message.type()) {
      case RequestJob:
        requestJob(((MessageRequestJob) message).blocking());
        break;
      case Job:
        jobDone((MessageJob) message);
        break;
      case RequestClass:
        requestClassData(((MessageRequestClass) message).className());
      }
    }
    close();
  }

  private void requestClassData(String className) {
    clientSocket.write(new MessageClassData(className));
  }

  synchronized private void jobDone(MessageJob message) {
    Runnable todo = idtoJob.remove(message.id());
    serverScheduler.localQueue.done(todo, message.job());
  }

  synchronized private void requestJob(boolean blocking) {
    waitingForJob = waitingForJob || blocking;
    Runnable todo = serverScheduler.localQueue.request();
    if (todo == null && blocking)
      return;
    sendJob(todo);
  }

  synchronized private void sendJob(Runnable todo) {
    if (todo == null) {
      clientSocket.write(MessageJob.nullJob());
      return;
    }
    int jobId = newId();
    idtoJob.put(jobId, todo);
    clientSocket.write(new MessageJob(jobId, todo));
    waitingForJob = false;
  }

  private int newId() {
    id++;
    if (id < 0)
      id = 0;
    return id;
  }

  synchronized public void wakeUp() {
    if (!waitingForJob)
      return;
    Runnable todo = serverScheduler.localQueue.request();
    if (todo == null)
      return;
    sendJob(todo);
  }

  private void close() {
    clientSocket.close();
    serverScheduler.removeClient(this);
  }

  public Collection<Runnable> pendingJobs() {
    return idtoJob.values();
  }
}
