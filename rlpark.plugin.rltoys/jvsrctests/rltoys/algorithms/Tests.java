package rltoys.algorithms;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rltoys.algorithms.learning.control.acting.SoftMaxTest;
import rltoys.algorithms.learning.control.actorcritic.ActorCriticMountainCarTest;
import rltoys.algorithms.learning.control.actorcritic.ActorCriticOnPolicyOnPendulumTest;
import rltoys.algorithms.learning.control.actorcritic.ActorCriticOnPolicyOnStateTest;
import rltoys.algorithms.learning.control.gq.GQOnPolicyTest;
import rltoys.algorithms.learning.control.gq.GQTest;
import rltoys.algorithms.learning.control.qlearning.QLearningTest;
import rltoys.algorithms.learning.control.sarsa.SarsaTest;
import rltoys.algorithms.learning.control.sarsa.TracesTest;
import rltoys.algorithms.learning.predictions.supervised.AdalineTest;
import rltoys.algorithms.learning.predictions.supervised.IDBDTest;
import rltoys.algorithms.learning.predictions.supervised.K1Test;
import rltoys.algorithms.learning.predictions.td.GTDLambdaBetaTest;
import rltoys.algorithms.learning.predictions.td.GTDLambdaTest;
import rltoys.algorithms.learning.predictions.td.TDTest;
import rltoys.algorithms.representations.ObsHistoryTest;
import rltoys.algorithms.representations.actions.TabularActionTest;
import rltoys.algorithms.representations.agentstates.FeatureNetworkTest;
import rltoys.algorithms.representations.features.FeatureTest;
import rltoys.algorithms.representations.ltu.RandomNetworkTest;
import rltoys.algorithms.representations.policy.ConstantPolicyTest;
import rltoys.algorithms.representations.states.LinearCombinationTest;
import rltoys.algorithms.representations.states.StepDelayTest;
import rltoys.algorithms.representations.tilescoding.TileCodersHashingTest;
import rltoys.algorithms.representations.tilescoding.TileCodersNoHashingTest;
import rltoys.algorithms.representations.tilescoding.hashing.MurmurHash2Test;

@RunWith(Suite.class)
@Suite.SuiteClasses({ FeatureNetworkTest.class, StepDelayTest.class,
    ConstantPolicyTest.class, LinearCombinationTest.class, ObsHistoryTest.class, FeatureTest.class,
    TabularActionTest.class, SoftMaxTest.class, TileCodersNoHashingTest.class,
    TileCodersHashingTest.class, MurmurHash2Test.class, AdalineTest.class, IDBDTest.class, K1Test.class, TDTest.class,
    GTDLambdaBetaTest.class, GTDLambdaTest.class, GQTest.class, GQOnPolicyTest.class, SarsaTest.class,
    QLearningTest.class, TracesTest.class, ActorCriticOnPolicyOnStateTest.class,
    ActorCriticOnPolicyOnPendulumTest.class, ActorCriticMountainCarTest.class, RandomNetworkTest.class })
public class Tests {
}