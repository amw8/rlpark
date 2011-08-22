package rltoys.experiments.scheduling.network.internal;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.interfaces.JobQueue;
import zephyr.plugin.core.api.signals.Signal;
import zephyr.plugin.core.api.synchronization.Chrono;

public class NetworkJobQueue implements JobQueue {
  private static final double MessagePeriod = 10;
  public Signal<JobQueue> onJobReceived = new Signal<JobQueue>();
  private final SyncSocket syncSocket;
  private final Map<Runnable, Integer> jobToId = new HashMap<Runnable, Integer>();
  private final NetworkClassLoader classLoader;
  private final Chrono chrono = new Chrono();
  private final Signal<JobDoneEvent> onJobDone = new Signal<JobDoneEvent>();
  private int nbJobsSinceLastMessage = 0;
  private boolean denyNewJobRequest = false;

  public NetworkJobQueue(String serverHostName, int port) throws UnknownHostException, IOException {
    syncSocket = new SyncSocket(new Socket(serverHostName, port));
    classLoader = NetworkClassLoader.newClassLoader(syncSocket);
  }

  @Override
  synchronized public Runnable request() {
    if (denyNewJobRequest)
      return null;
    Runnable job = null;
    MessageJob messageJobTodo = syncSocket.jobTransaction(classLoader, jobToId.isEmpty());
    if (messageJobTodo == null)
      return null;
    job = messageJobTodo.job();
    if (job != null)
      jobToId.put(job, messageJobTodo.id());
    if (job != null)
      onJobReceived.fire(this);
    return job;
  }

  @Override
  synchronized public void done(Runnable todo, Runnable done) {
    Integer jobId = jobToId.remove(todo);
    MessageJob messageJobTodo = new MessageJob(jobId, done);
    syncSocket.write(messageJobTodo);
    nbJobsSinceLastMessage += 1;
    if (chrono.getCurrentChrono() > MessagePeriod) {
      Messages.println(nbJobsSinceLastMessage + " done in " + chrono.toString());
      chrono.start();
      nbJobsSinceLastMessage = 0;
    }
    onJobDone.fire(new JobDoneEvent(todo, done));
  }

  public void close() {
    syncSocket.close();
  }

  public boolean canAnswerJobRequest() {
    return !syncSocket.isClosed() && !denyNewJobRequest;
  }

  @Override
  public Signal<JobDoneEvent> onJobDone() {
    return onJobDone;
  }

  public void denyNewJobRequest() {
    denyNewJobRequest = true;
  }
}
