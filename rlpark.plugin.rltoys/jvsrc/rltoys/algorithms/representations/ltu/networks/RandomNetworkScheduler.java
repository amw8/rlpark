package rltoys.algorithms.representations.ltu.networks;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import rltoys.algorithms.representations.ltu.internal.LTUArray;
import rltoys.algorithms.representations.ltu.internal.LTUUpdated;
import rltoys.algorithms.representations.ltu.units.LTU;
import rltoys.math.vector.BinaryVector;
import rltoys.utils.Scheduling;

public class RandomNetworkScheduler implements Serializable {
  private static final long serialVersionUID = -2515509378000478726L;

  protected class LTUSumUpdater implements Runnable {
    private final int offset;
    private final LTUArray[] connectedLTUs;
    private final LTUUpdated updatedLTUs;
    private final double[] denseInputVector;
    private final boolean[] updated;

    LTUSumUpdater(RandomNetwork randomNetwork, int offset) {
      this.offset = offset;
      connectedLTUs = randomNetwork.connectedLTUs;
      updatedLTUs = randomNetwork.updatedLTUs;
      updated = updatedLTUs.updated;
      denseInputVector = randomNetwork.denseInputVector;
    }

    @Override
    public void run() {
      int currentPosition = offset;
      int[] activeIndexes = obs.activeIndexes();
      final int runTime = time;
      while (currentPosition < activeIndexes.length) {
        int activeInput = activeIndexes[currentPosition];
        LTU[] connected = connectedLTUs[activeInput].array();
        updateConnectedLTU(runTime, connected);
        currentPosition += nbThread;
      }
    }

    private void updateConnectedLTU(int time, LTU[] connected) {
      for (LTU ltu : connected) {
        final int index = ltu.index();
        if (updated[index])
          continue;
        updatedLTUs.updateLTUSum(time, index, ltu, denseInputVector);
      }
    }
  }

  protected class LTUActivationUpdater implements Runnable {
    private final int offset;
    private final LTU[] ltus;

    LTUActivationUpdater(RandomNetwork randomNetwork, int offset) {
      this.offset = offset;
      ltus = randomNetwork.ltus;
    }

    @Override
    public void run() {
      int currentPosition = offset;
      while (currentPosition < ltus.length) {
        final LTU ltu = ltus[currentPosition];
        if (ltu != null && ltu.updateActivation())
          setOutputOn(currentPosition);
        currentPosition += nbThread;
      }
    }
  }

  transient private ExecutorService executor = null;
  transient private LTUSumUpdater[] sumUpdaters;
  transient private LTUActivationUpdater[] activationUpdaters;
  transient private Future<?>[] futurs;
  protected final int nbThread;
  BinaryVector obs;
  BinaryVector output;
  int time;

  public RandomNetworkScheduler() {
    this(Scheduling.getDefaultNbThreads());
  }

  public RandomNetworkScheduler(int nbThread) {
    this.nbThread = nbThread;
  }

  private void initialize(RandomNetwork randomNetwork) {
    sumUpdaters = new LTUSumUpdater[nbThread];
    activationUpdaters = new LTUActivationUpdater[nbThread];
    for (int i = 0; i < nbThread; i++) {
      sumUpdaters[i] = new LTUSumUpdater(randomNetwork, i);
      activationUpdaters[i] = new LTUActivationUpdater(randomNetwork, i);
    }
    futurs = new Future<?>[nbThread];
    executor = Scheduling.newFixedThreadPool("randomnetwork", nbThread);
  }

  public void update(RandomNetwork randomNetwork, BinaryVector obs) {
    if (executor == null)
      initialize(randomNetwork);
    this.obs = obs;
    this.output = randomNetwork.output;
    this.time = randomNetwork.time;
    for (int i = 0; i < sumUpdaters.length; i++)
      futurs[i] = executor.submit(sumUpdaters[i]);
    waitWorkingThread();
    for (int i = 0; i < sumUpdaters.length; i++)
      futurs[i] = executor.submit(activationUpdaters[i]);
    waitWorkingThread();
  }

  private void waitWorkingThread() {
    try {
      for (Future<?> futur : futurs)
        futur.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    }
  }

  synchronized final void setOutputOn(int index) {
    output.setOn(index);
  }
}
