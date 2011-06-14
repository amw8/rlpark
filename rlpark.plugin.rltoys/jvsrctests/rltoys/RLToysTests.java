package rltoys;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ rltoys.math.Tests.class, rltoys.environments.Tests.class,
    rltoys.algorithms.Tests.class, rltoys.experiments.Tests.class, rltoys.demons.Tests.class })
public class RLToysTests {
}