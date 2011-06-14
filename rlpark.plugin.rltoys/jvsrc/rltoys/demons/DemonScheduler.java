package rltoys.demons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import rltoys.algorithms.representations.actions.Action;
import rltoys.math.MovingAverage;
import rltoys.math.vector.RealVector;
import rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Chrono;

public class DemonScheduler implements Serializable {
  private static final long serialVersionUID = 6003588160245867945L;

  protected class DemonUpdater implements Runnable {
    private static final long serialVersionUID = 4610329716833966956L;
    private final int offset;

    DemonUpdater(int offset) {
      this.offset = offset;
    }

    @Override
    public void run() {
      int currentPosition = offset;
      while (currentPosition < demons.size()) {
        demons.get(currentPosition).update(x_t, a_t, x_tp1);
        currentPosition += nbThread;
      }
    }
  }

  transient private ExecutorService executor = null;
  transient private DemonUpdater[] updaters;
  transient private Future<?>[] futurs;
  transient private Chrono chrono;
  @Monitor
  protected final List<Demon> demons = new ArrayList<Demon>();
  protected RealVector x_tp1;
  protected RealVector x_t;
  protected Action a_t;
  protected final int nbThread;
  @Monitor
  private final MovingAverage updateTimeAverage = new MovingAverage(100);

  public DemonScheduler() {
    this(getDefaultNbThreads(), new ArrayList<Demon>());
  }

  public DemonScheduler(int nbThread, Demon... demons) {
    this(nbThread, Utils.asList(demons));
  }

  public DemonScheduler(List<Demon> demons) {
    this(getDefaultNbThreads(), demons);
  }

  private static int getDefaultNbThreads() {
    return Runtime.getRuntime().availableProcessors();
  }

  public DemonScheduler(int nbThread) {
    this(nbThread, new ArrayList<Demon>());
  }

  public DemonScheduler(int nbThread, List<Demon> demons) {
    this.nbThread = nbThread;
    if (demons != null)
      this.demons.addAll(demons);
  }

  private void initialize() {
    updaters = new DemonUpdater[nbThread];
    for (int i = 0; i < updaters.length; i++)
      updaters[i] = new DemonUpdater(i);
    futurs = new Future<?>[nbThread];
    executor = Executors.newFixedThreadPool(nbThread);
    chrono = new Chrono();
  }

  public void update(RealVector x_t, Action a_t, RealVector x_tp1) {
    if (executor == null)
      initialize();
    chrono.start();
    this.x_t = x_t;
    this.a_t = a_t;
    this.x_tp1 = x_tp1;
    for (int i = 0; i < updaters.length; i++)
      futurs[i] = executor.submit(updaters[i]);
    try {
      for (Future<?> futur : futurs)
        futur.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    updateTimeAverage.update(chrono.getCurrentMillis());
  }

  public void add(Demon demon) {
    demons.add(demon);
  }

  public void remove(Demon demon) {
    demons.remove(demon);
  }

  public int nbDemons() {
    return demons.size();
  }

  public double updateTimeAverage() {
    return updateTimeAverage.average();
  }

  public List<Demon> demons() {
    return demons;
  }
}
