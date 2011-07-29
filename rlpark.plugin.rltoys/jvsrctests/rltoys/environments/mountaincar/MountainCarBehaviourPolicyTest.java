package rltoys.environments.mountaincar;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.observations.TRStep;


public class MountainCarBehaviourPolicyTest {

  @Test
  public void testMountainCarBehaviourPolicyAgent() {
    Random random = new Random(0);
    MountainCar mcar = new MountainCar(random);
    RLAgent agent = new MountainCarBehaviourPolicy(mcar, random, 0.1);
    TRStep step;
    for (int i = 0; i < 10; i++) {
      step = mcar.initialize();
      do {
        step = mcar.step(agent.getAtp1(step));
        Assert.assertTrue(step.time < 200);
      } while (step.isEpisodeEnding());
    }
  }

}
