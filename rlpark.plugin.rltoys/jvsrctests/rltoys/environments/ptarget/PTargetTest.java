package rltoys.environments.ptarget;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.Runner;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.observations.TRStep;

@SuppressWarnings("serial")
public class PTargetTest {
  private final Random random = new Random(0);
  public final static int nbTarget = 5;

  public RLAgent createAgent() {
    return new RLAgent() {
      @Override
      public Action getAtp1(TRStep step) {
        double[] action = new double[nbTarget];
        Arrays.fill(action, PTarget.Resolution * 0.5);
        if (step.o_t != null)
          for (int i = 0; i < action.length; i++)
            action[i] *= -Math.signum(step.o_t[i]);
        return new ActionArray(action);
      }
    };
  }

  @Test
  public void testPTarget() {
    PTarget problem = new PTarget(random, nbTarget);
    RLAgent agent = createAgent();
    checkAgent(problem, agent);
  }

  static public void checkAgent(PTarget problem, RLAgent agent) {
    final int nbEpisode = 100;
    final int maxNbTimeSteps = (int) (2.0 / PTarget.Resolution * nbEpisode * 1000);
    Runner runner = new Runner(problem, agent, nbEpisode, maxNbTimeSteps);
    runner.run();
  }
}
