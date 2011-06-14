package critterbot.agents;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;

import rltoys.environments.envio.observations.TStep;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.fileloggers.FileLogger;
import critterbot.CritterbotAgent;
import critterbot.CritterbotObservation;
import critterbot.actions.CritterbotAction;
import critterbot.environment.CritterbotEnvironment;
import critterbot.environment.CritterbotRobot;
import critterbot.examples.RandomAgent;

public class DaylongAgent implements CritterbotAgent {
  public static interface DaylongSlaveAgent {
    void setInControl(boolean inControl);
  }

  private final double chargingVoltage;
  @Monitor(emptyLabel = true)
  private final CritterbotEnvironment environment;
  private final DockingAgent dockingAgent;
  private CritterbotObservation obs;
  private long timeVoltageAbove = -1;
  private long timeDocked = -1;
  @Monitor
  private final CritterbotAgent agent;
  private final FileLogger logFile;
  @Monitor
  private boolean daylongInControl;

  public DaylongAgent(String filepath, CritterbotEnvironment environment, CritterbotAgent agent, double chargingVoltage)
      throws IOException {
    this.environment = environment;
    this.chargingVoltage = chargingVoltage;
    dockingAgent = new DockingAgent(environment);
    this.agent = agent;
    logFile = new FileLogger(filepath, true, false);
    logFile.add(this);
    System.out.println("Critterbot will charge at " + chargingVoltage);
  }

  private void updateCharging() {
    double voltageMax = Math.max(obs.bat[0], Math.max(obs.bat[1], obs.bat[2]));
    if (timeVoltageAbove < 0 || voltageMax >= chargingVoltage || isDocked())
      timeVoltageAbove = obs.time;
    if (!isDocked())
      timeDocked = obs.time;
  }

  private boolean isDocked() {
    return obs.busVoltage > 170;
  }

  private boolean needsCharging() {
    return obs.time - timeVoltageAbove > 10000;
  }

  private Color pickupColor() {
    if (needsCharging())
      return Color.BLUE;
    if (isDockedAndWait())
      return Color.WHITE;
    if (isDocked())
      return Color.GREEN;
    return Color.BLACK;
  }

  public void setLeds(Color[] colors) {
    if (obs == null)
      return;
    Arrays.fill(colors, pickupColor());
    colors[0] = Color.RED;
    colors[colors.length / 2] = Color.MAGENTA;
  }

  @Override
  public CritterbotAction getAtp1(TStep step) {
    obs = environment.getCritterbotObservation(step);
    updateCharging();
    daylongInControl = needsCharging() || isDockedAndWait();
    if (agent instanceof DaylongSlaveAgent)
      ((DaylongSlaveAgent) agent).setInControl(!daylongInControl);
    CritterbotAction action = daylongInControl ? dockingAgent.getAtp1(step) : agent.getAtp1(step);
    logFile.update(step.time);
    return action;
  }

  private boolean isDockedAndWait() {
    return isDocked() && obs.time - timeDocked < 10000;
  }

  public static void main(String[] args) throws IOException {
    CritterbotEnvironment environment = new CritterbotRobot();
    RandomAgent funAgent = new RandomAgent(environment);
    DaylongAgent daylongAgent = new DaylongAgent("/tmp/daylong.crtrlog", environment, funAgent, 160);
    environment.run(daylongAgent);
  }
}
