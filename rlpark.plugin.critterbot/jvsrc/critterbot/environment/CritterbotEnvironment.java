package critterbot.environment;

import java.awt.Color;

import rlpark.plugin.robot.RobotEnvironment;
import rlpark.plugin.robot.sync.ObservationReceiver;
import rltoys.environments.envio.Agent;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.ObsFilter;
import rltoys.environments.envio.observations.TStep;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.synchronization.Clock;
import critterbot.CritterbotAgent;
import critterbot.CritterbotObservation;
import critterbot.CritterbotProblem;
import critterbot.actions.CritterbotAction;
import critterbot.environment.CritterbotDrops.LedMode;

public class CritterbotEnvironment extends RobotEnvironment implements CritterbotProblem, MonitorContainer {
  protected CritterbotAction agentAction;
  private LedMode ledMode = LedMode.BUSY;
  private final Color[] ledValues = new Color[CritterbotDrops.NbLeds];
  protected final CritterbotConnection critterbotConnection;

  protected CritterbotEnvironment(ObservationReceiver receiver) {
    super(receiver, false);
    critterbotConnection = (CritterbotConnection) receiver();
  }

  @Override
  public Legend legend() {
    return critterbotConnection.legend();
  }

  /**
   * Do not use this method, use your own main loop instead using sendAction(),
   * setLed() waitNewObs() and lastReceivedObs()
   * 
   * @see rlpark.plugin.robot.RobotEnvironment#run(zephyr.plugin.core.api.synchronization
   *      .Clock, rltoys.environments.envio.Agent)
   */
  @Deprecated
  public void run(Agent agent) {
    run(new Clock("CritterbotEnvironment"), agent);
  }

  /**
   * Do not use this method, use your own main loop instead using sendAction(),
   * setLed() waitNewObs() and lastReceivedObs()
   * 
   * @see rlpark.plugin.robot.RobotEnvironment#run(zephyr.plugin.core.api.synchronization
   *      .Clock, rltoys.environments.envio.Agent)
   */
  @Deprecated
  @Override
  public void run(Clock clock, Agent agent) {
    CritterbotAgent critterbotAgent = (CritterbotAgent) agent;
    double[] obsArray = waitNewObs();
    TStep currentStep = new TStep(critterbotConnection.lastObservationDropTime(), (double[]) null, null, obsArray);
    while (!isClosed() && !clock.isTerminated()) {
      clock.tick();
      CritterbotAction action = critterbotAgent.getAtp1(currentStep);
      sendAction(action);
      if (action == null)
        break;
      obsArray = waitNewObs();
      TStep lastStep = currentStep;
      long time = critterbotConnection.lastObservationDropTime();
      currentStep = new TStep(time, lastStep, action, obsArray);
    }
  }

  public void sendAction(CritterbotAction action) {
    agentAction = action;
    if (action != null)
      critterbotConnection.sendActionDrop(action, ledMode, ledValues);
    ledMode = LedMode.CLEAR;
  }

  public void setLed(Color[] colors) {
    setLedMode(LedMode.CUSTOM);
    System.arraycopy(colors, 0, ledValues, 0, ledValues.length);
  }

  public void setLedMode(LedMode ledMode) {
    this.ledMode = ledMode;
  }

  public CritterbotObservation getCritterbotObservation(TStep step) {
    return new CritterbotObservation(legend(), step.time, step.o_tp1);
  }

  public ObsFilter getDefaultFilter() {
    return CritterbotDrops.newDefaultFilter(legend());
  }

  @Override
  public void addToMonitor(DataMonitor monitor) {
    CritterbotEnvironments.addObservationsLogged(this, monitor);
    CritterbotEnvironments.addActionsLogged(this, monitor);
  }

  @Override
  public CritterbotAction lastAction() {
    return agentAction;
  }
}
