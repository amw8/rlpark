package rltoys.experiments;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rltoys.experiments.parametersweep.SweepTest;
import rltoys.experiments.parametersweep.interfaces.ParametersTest;
import rltoys.experiments.scheduling.tests.SchedulerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ParametersTest.class, SchedulerTest.class, SweepTest.class })
public class Tests {
}