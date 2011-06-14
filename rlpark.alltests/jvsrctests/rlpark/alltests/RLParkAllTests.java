package rlpark.alltests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rlpark.plugin.robot.RobotTests;
import rltoys.RLToysTests;
import critterbot.CritterbotTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({ RLToysTests.class, RobotTests.class, CritterbotTests.class, PackageCycleTest.class })
public class RLParkAllTests {
}