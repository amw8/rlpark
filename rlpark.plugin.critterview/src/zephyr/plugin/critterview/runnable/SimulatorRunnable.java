package zephyr.plugin.critterview.runnable;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.synchronization.Clock;
import critterbot.environment.CritterbotSimulator;
import critterbot.environment.CritterbotSimulator.SimulatorCommand;

public class SimulatorRunnable implements Runnable {
  @Override
  public void run() {
    SimulatorCommand simulatorCommand = CritterbotSimulator.startSimulator();
    CritterbotSimulator simulator = new CritterbotSimulator(simulatorCommand);
    Clock clock = new Clock("Simulator");
    Zephyr.advertise(clock, simulator);
    while (!simulator.isClosed() && clock.tick())
      simulator.waitNewObs();
    simulator.close();
  }
}