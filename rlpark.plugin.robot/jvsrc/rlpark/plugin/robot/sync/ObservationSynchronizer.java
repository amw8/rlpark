package rlpark.plugin.robot.sync;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class ObservationSynchronizer {
  static private int BufferSize = 100;
  private final ObservationReceiver receiver;
  private final LinkedList<ObservationVersatile> lastObsBuffer = new LinkedList<ObservationVersatile>();
  private boolean terminated = false;
  private final Runnable observationReader = new Runnable() {
    @Override
    public void run() {
      observationReaderMainLoop();
    }
  };
  private boolean persistent = false;
  private final Semaphore firstInitialization = new Semaphore(0);

  public ObservationSynchronizer(ObservationReceiver receiver, boolean persistent) {
    assert receiver != null;
    this.receiver = receiver;
    this.persistent = persistent;
    start();
    try {
      if (firstInitialization.tryAcquire(5, TimeUnit.SECONDS))
        firstInitialization.release();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  protected void observationReaderMainLoop() {
    while (!terminated) {
      receiver.initialize();
      firstInitialization.release();
      while (!receiver.isClosed() && !terminated) {
        ObservationVersatile obs = receiver.waitForData();
        setLastObs(obs);
      }
      if (!persistent) {
        terminate();
        break;
      }
    }
  }

  private void start() {
    Thread thread = new Thread(observationReader);
    thread.setName("ObservationReader");
    thread.setDaemon(true);
    thread.start();
  }

  synchronized private void setLastObs(ObservationVersatile obs) {
    lastObsBuffer.add(obs);
    if (lastObsBuffer.size() > BufferSize)
      lastObsBuffer.poll();
    notifyAll();
  }

  synchronized public ObservationVersatile[] waitNewObs() {
    if (lastObsBuffer.size() > 0)
      return useLastObs();
    try {
      wait();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return useLastObs();
  }

  synchronized public ObservationVersatile[] newObsNow() {
    return useLastObs();
  }

  synchronized private ObservationVersatile[] useLastObs() {
    if (lastObsBuffer.size() == 0)
      return null;
    ObservationVersatile[] result = new ObservationVersatile[lastObsBuffer.size()];
    lastObsBuffer.toArray(result);
    lastObsBuffer.clear();
    return result;
  }

  public ObservationReceiver receiver() {
    return receiver;
  }

  synchronized public void terminate() {
    terminated = true;
    notifyAll();
  }

  public boolean isTerminated() {
    return terminated;
  }
}
