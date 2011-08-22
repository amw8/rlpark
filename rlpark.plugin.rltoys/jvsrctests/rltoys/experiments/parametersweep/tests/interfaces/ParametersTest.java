package rltoys.experiments.parametersweep.tests.interfaces;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rltoys.experiments.parametersweep.parameters.Parameters;


public class ParametersTest {
  @Test
  public void testParametersEquals01() {
    FrozenParameters p01 = createParameter01();
    FrozenParameters p01bis = createParameter01Bis();
    Assert.assertTrue(p01.equals(p01bis));
    Assert.assertEquals(p01.hashCode(), p01bis.hashCode());
    Set<FrozenParameters> set = new HashSet<FrozenParameters>();
    set.add(p01);
    set.add(p01bis);
    Assert.assertEquals(1, set.size());
    Assert.assertTrue(set.contains(createParameter01()));
    Assert.assertFalse(set.contains(createParameter02()));
    FrozenParameters p02 = createParameter02();
    Assert.assertFalse(p01.equals(p02));
    Assert.assertTrue(p01.hashCode() != p02.hashCode());
    set.add(p02);
    Assert.assertEquals(2, set.size());
  }

  private FrozenParameters toParameters(Object... objects) {
    Parameters parameters = new Parameters();
    for (int i = 0; i < objects.length / 2; i++)
      parameters.put((String) objects[i * 2], (double) (Integer) objects[i * 2 + 1]);
    return parameters.froze();
  }

  private FrozenParameters createParameter02() {
    return toParameters("Hello", 0, "Bye", 3);
  }

  private FrozenParameters createParameter01() {
    return toParameters("Hello", 0, "Bye", 2);
  }

  private FrozenParameters createParameter01Bis() {
    return toParameters("Bye", 2, "Hello", 0);
  }
}
