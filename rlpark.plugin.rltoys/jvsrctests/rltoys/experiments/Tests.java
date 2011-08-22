package rltoys.experiments;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rltoys.experiments.parametersweep.SweepTest;
import rltoys.experiments.parametersweep.interfaces.ParametersTest;
import rltoys.experiments.scheduling.tests.JobPoolTest;
import rltoys.experiments.scheduling.tests.SchedulerNetworkUnreliableTest;
import rltoys.experiments.scheduling.tests.SchedulerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ParametersTest.class, SchedulerTest.class, JobPoolTest.class,
    SchedulerNetworkUnreliableTest.class, SweepTest.class })
public class Tests {
}