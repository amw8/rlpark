package rlpark.plugin.robot;

import rlpark.plugin.robot.sync.ObservationReceiver;
import rlpark.plugin.robot.sync.ObservationSynchronizer;
import rlpark.plugin.robot.sync.ObservationVersatile;
import rltoys.environments.envio.Agent;
import rltoys.environments.envio.observations.Legend;
import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.Closeable;

public abstract class RobotEnvironment implements Closeable, Labeled {
  protected final ObservationSynchronizer obsSync;
  protected ObservationVersatile[] lastReceivedObsBuffer;

  public RobotEnvironment(ObservationReceiver receiver, boolean persistent) {
    prepareEnvironment();
    obsSync = new ObservationSynchronizer(receiver, persistent);
  }

  protected void prepareEnvironment() {
  }

  public ObservationSynchronizer synchronizer() {
    return obsSync;
  }

  protected ObservationReceiver receiver() {
    return obsSync.receiver();
  }

  public double[] newObsNow() {
    ObservationVersatile newObs = toOneObs(newRawObsNow());
    return newObs != null ? newObs.doubleValues() : null;
  }

  public ObservationVersatile[] newRawObsNow() {
    ObservationVersatile[] newObs = obsSync.newObsNow();
    if (newObs != null)
      lastReceivedObsBuffer = newObs;
    return newObs;
  }

  protected ObservationVersatile toOneObs(ObservationVersatile[] obs) {
    return obs != null ? obs[obs.length - 1] : null;
  }

  public double[] waitNewObs() {
    ObservationVersatile rawobs = toOneObs(waitNewRawObs());
    return rawobs != null ? rawobs.doubleValues() : null;
  }

  public ObservationVersatile[] waitNewRawObs() {
    lastReceivedObsBuffer = obsSync.waitNewObs();
    if (lastReceivedObsBuffer == null)
      close();
    return lastReceivedObsBuffer;
  }

  @Override
  public void close() {
    obsSync.terminate();
  }

  @Override
  public String label() {
    return getClass().getSimpleName();
  }

  public double[] lastReceivedObs() {
    ObservationVersatile lastReceivedObs = toOneObs(lastReceivedObsBuffer);
    if (lastReceivedObs == null)
      return null;
    return lastReceivedObs.doubleValues();
  }

  public ObservationVersatile[] lastReceivedRawObs() {
    return lastReceivedObsBuffer;
  }

  public int observationPacketSize() {
    return receiver().packetSize();
  }

  public boolean isClosed() {
    return obsSync.isTerminated();
  }

  /**
   * Do not use this method, use your own main loop instead using sendAction(),
   * setLed() waitNewObs() and lastReceivedObs()
   * 
   * @see rlpark.plugin.robot.RobotEnvironment#run(zephyr.plugin.core.api.synchronization
   *      .Clock, rltoys.environments.envio.Agent)
   */
  @Deprecated
  public abstract void run(Clock clock, Agent agent);

  abstract public Legend legend();
}
