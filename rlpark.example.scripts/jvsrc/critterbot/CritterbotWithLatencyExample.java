package critterbot;

import rltoys.environments.envio.observations.Legend;
import zephyr.plugin.core.api.synchronization.Chrono;
import critterbot.actions.CritterbotAction;
import critterbot.actions.VoltageSpaceAction;
import critterbot.actions.XYThetaAction;
import critterbot.environment.CritterbotDrops;
import critterbot.environment.CritterbotEnvironment;
import critterbot.environment.CritterbotSimulator;

public class CritterbotWithLatencyExample {
  final static public long Latency = 100;

  public static void main(String[] args) {
    CritterbotEnvironment environment = new CritterbotSimulator();
    Legend legend = environment.legend();
    Chrono chrono = new Chrono();
    while (!environment.isClosed()) {
      chrono.start();
      double[] obs = environment.waitNewObs();
      CritterbotAction action;
      if (obs[legend.indexOf(CritterbotDrops.IRDistance + "0")] > 128)
        action = new XYThetaAction(20, -20, 20);
      else
        action = new VoltageSpaceAction(20, -20, 20);
      environment.sendAction(action);
      long remainingTime = Latency - chrono.getCurrentMillis();
      if (remainingTime > 0)
        try {
          Thread.sleep(remainingTime);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
    }
  }
}
