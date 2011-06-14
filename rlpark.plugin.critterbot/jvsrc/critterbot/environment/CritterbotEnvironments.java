package critterbot.environment;

import java.util.List;

import rltoys.environments.envio.observations.ObsFilter;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import critterbot.CritterbotProblem;
import critterbot.actions.CritterbotAction;

public class CritterbotEnvironments {
  public static void addObservationsLogged(final CritterbotProblem problem, DataMonitor loggedManager) {
    ObsFilter filter = CritterbotDrops.newDefaultFilter(problem.legend());
    List<String> labelsToLog = filter.legend().getLabels();
    for (String label : labelsToLog) {
      final int obsIndex = problem.legend().indexOf(label);
      loggedManager.add(label, 0, new Monitored() {
        @Override
        public double monitoredValue() {
          double[] o_t = problem.lastReceivedObs();
          if (o_t == null)
            return -1;
          return o_t[obsIndex];
        }
      });
    }
  }

  public static void addActionsLogged(final CritterbotEnvironment environment, DataMonitor loggedManager) {
    for (int i = 0; i < 3; i++) {
      String label = String.format("a[%d]", i);
      final int actionIndex = i;
      loggedManager.add(label, 0, new Monitored() {
        @Override
        public double monitoredValue() {
          CritterbotAction a_t = environment.lastAction();
          if (a_t == null || a_t.actions == null)
            return -1;
          return a_t.actions[actionIndex];
        }
      });
    }
    loggedManager.add("ActionMode", 0, new Monitored() {
      @Override
      public double monitoredValue() {
        CritterbotAction a_t = environment.lastAction();
        if (a_t == null)
          return -1;
        return a_t.motorMode.ordinal();
      }
    });
  }
}
