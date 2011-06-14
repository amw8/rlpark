package rltoys.algorithms.representations.policy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rltoys.algorithms.representations.acting.ConstantPolicy;
import rltoys.algorithms.representations.actions.Action;

@SuppressWarnings("serial")
public class ConstantPolicyTest {
  @Test
  public void testConstantDistribution() {
    Action a = new Action() {
    };
    Action b = new Action() {
    };
    Action c = new Action() {
    };
    double pa = 0.3, pb = 0.6, pc = 0.1;
    Map<Action, Double> distribution = new LinkedHashMap<Action, Double>();
    distribution.put(a, pa);
    distribution.put(b, pb);
    distribution.put(c, pc);
    ConstantPolicy policy = new ConstantPolicy(new Random(0), distribution);
    int nbSample = 1000;
    double na = 0, nb = 0, nc = 0;
    for (int i = 0; i < nbSample; i++) {
      Action action = policy.decide(null);
      if (action == a)
        na++;
      else if (action == b)
        nb++;
      else if (action == c)
        nc++;
    }
    Assert.assertEquals(pa, na / nbSample, 0.1);
    Assert.assertEquals(pb, nb / nbSample, 0.1);
    Assert.assertEquals(pc, nc / nbSample, 0.1);
  }
}
