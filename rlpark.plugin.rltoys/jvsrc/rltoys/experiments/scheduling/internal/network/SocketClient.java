package rltoys.experiments.scheduling.internal.network;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rltoys.experiments.scheduling.interfaces.JobQueue;
import rltoys.experiments.scheduling.internal.messages.Message;
import rltoys.experiments.scheduling.internal.messages.MessageClassData;
import rltoys.experiments.scheduling.internal.messages.MessageJob;
import rltoys.experiments.scheduling.internal.messages.MessageRequestClass;
import rltoys.experiments.scheduling.internal.messages.MessageRequestJob;
import rltoys.experiments.scheduling.internal.messages.MessageSendClientName;
import rltoys.experiments.scheduling.internal.messages.Messages.MessageType;
import zephyr.plugin.core.api.signals.Signal;

public class SocketClient {
  static private volatile int nbJobSendPerRequest;
  static public final Signal<String> onClassRequested = new Signal<String>();
  public final Signal<SocketClient> onClosed = new Signal<SocketClient>();
  private final Runnable clientRunnable = new Runnable() {
    @Override
    public void run() {
      clientReadMainLoop();
    }
  };
  private final SyncSocket clientSocket;
  private final Thread clientThread = new Thread(clientRunnable, "ClientThread");
  private final Map<Integer, Runnable> idtoJob = new HashMap<Integer, Runnable>();
  private boolean waitingForJob = false;
  private int id;
  private String clientName;
  private final JobQueue jobQueue;
  private int nbJobDone = 0;

  public SocketClient(JobQueue jobQueue, Socket clientSocket) {
    this.jobQueue = jobQueue;
    this.clientSocket = new SyncSocket(clientSocket);
    clientThread.setPriority(Thread.MAX_PRIORITY);
    clientThread.setDaemon(true);
  }

  public void start() {
    clientThread.start();
  }

  public boolean readName() {
    Message message = SyncSocket.readNextMessage(clientSocket);
    if (message == null || message.type() != MessageType.SendClientName) {
      System.err.println("error: client did not declare its name, it is rejected.");
      return false;
    }
    clientName = ((MessageSendClientName) message).clientName();
    return true;
  }

  @SuppressWarnings("incomplete-switch")
  protected void clientReadMainLoop() {
    while (!clientSocket.isClosed()) {
      Message message = SyncSocket.readNextMessage(clientSocket);
      if (message == null)
        break;
      switch (message.type()) {
      case RequestJob:
        sendJob(((MessageRequestJob) message).blocking());
        break;
      case Job:
        jobDone((MessageJob) message);
        break;
      case RequestClass:
        requestClassData(((MessageRequestClass) message).className());
        break;
      case SendClientName:
        System.err.println("error: a client is trying to change its name");
        break;
      }
    }
    close();
  }

  private void requestClassData(String className) {
    clientSocket.write(new MessageClassData(className));
    onClassRequested.fire(className);
  }

  synchronized private void jobDone(MessageJob message) {
    for (int i = 0; i < message.nbJobs(); i++) {
      Runnable todo = idtoJob.remove(message.jobIds()[i]);
      jobQueue.done(todo, message.jobs()[i]);
      nbJobDone++;
    }
  }

  synchronized private void sendJob(boolean blocking) {
    List<Runnable> jobs = new ArrayList<Runnable>();
    List<Integer> jobIds = new ArrayList<Integer>();
    for (int i = 0; i < nbJobSendPerRequest; i++) {
      Runnable todo = jobQueue.request();
      if (todo == null)
        break;
      int jobId = newId();
      idtoJob.put(jobId, todo);
      jobs.add(todo);
      jobIds.add(jobId);
    }
    MessageJob messageJob = new MessageJob(jobs, jobIds);
    waitingForJob = messageJob.nbJobs() == 0;
    if (messageJob.nbJobs() == 0 && blocking)
      return;
    clientSocket.write(messageJob);
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
    sendJob(true);
  }

  public void close() {
    clientSocket.close();
    onClosed.fire(this);
  }

  public String clientName() {
    return clientName;
  }

  public Collection<Runnable> pendingJobs() {
    return idtoJob.values();
  }

  public static void nbJobSendPerRequest(int nbJobSendPerRequest) {
    SocketClient.nbJobSendPerRequest = Math.min(Math.max(1, nbJobSendPerRequest), 20);
  }

  public int nbJobDone() {
    return nbJobDone;
  }
}
