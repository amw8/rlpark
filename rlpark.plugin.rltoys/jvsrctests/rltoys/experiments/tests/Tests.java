package rltoys.experiments.tests;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rltoys.experiments.parametersweep.ParametersTest;
import rltoys.experiments.parametersweep.SweepTest;
import rltoys.experiments.reinforcementlearning.OffPolicyContinuousEvaluationSweepTest;
import rltoys.experiments.reinforcementlearning.OffPolicyPerEpisodeBasedEvaluationSweepTest;
import rltoys.experiments.reinforcementlearning.OnPolicySweepTest;
import rltoys.experiments.scheduling.JobPoolTest;
import rltoys.experiments.scheduling.SchedulerTest;
import rltoys.experiments.scheduling.UnreliableNetworkClientTest;
import rltoys.experiments.scheduling.UnreliableNetworkClientWithPoolTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ParametersTest.class, SchedulerTest.class, JobPoolTest.class, UnreliableNetworkClientTest.class,
    UnreliableNetworkClientWithPoolTest.class, SweepTest.class, OnPolicySweepTest.class,
    OffPolicyContinuousEvaluationSweepTest.class, OffPolicyPerEpisodeBasedEvaluationSweepTest.class })
public class Tests {
}