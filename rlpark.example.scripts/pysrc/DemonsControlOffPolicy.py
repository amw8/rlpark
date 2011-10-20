import time
import zepy
from critterbot.environment import CritterbotSimulator 
from zephyr.plugin.core.api.synchronization import Chrono
from rltoys.algorithms.representations.acting import RandomPolicy
from critterbot.actions import XYThetaAction
from java.util import Random
from rltoys.algorithms.representations.tilescoding import TileCodersNoHashing
from rltoys.demons import DemonScheduler, ControlOffPolicyDemon, RewardFunction
from rltoys.algorithms.learning.control.gq import GQ, ExpectedGQ
from rltoys.algorithms.representations.actions import TabularAction
from rltoys.algorithms.learning.control.acting import Greedy

class SensorRewardFunction(RewardFunction):
    def __init__(self, legend, label):
        self.label = "reward" + label
        self.index = legend.indexOf(label)
        self.rewardValue = 0
        
    def update(self, o_tp1):
        self.rewardValue = o_tp1[self.index]
        
    def reward(self):
        return self.rewardValue

class DemonExperiment(object):
    Latency = 100 #s
    
    def __init__(self):
        self.environment = CritterbotSimulator()
        self.latencyTimer = Chrono()
        self.rewards = self.createRewardFunction()
        self.actions = XYThetaAction.sevenActions()
        self.behaviourPolicy = RandomPolicy(Random(0), self.actions)
        self.representation = TileCodersNoHashing(self.environment.legend().nbLabels(), -2000, 2000)
        self.representation.includeActiveFeature()
        self.demons = DemonScheduler()
        for rewardFunction in self.rewards:
            self.demons.add(self.createOffPolicyControlDemon(rewardFunction))
        self.x_t = None

    def createRewardFunction(self):
        legend = self.environment.legend()
        return [ SensorRewardFunction(legend, 'MotorCurrent0'),
                 SensorRewardFunction(legend, 'MotorCurrent1'),
                 SensorRewardFunction(legend, 'MotorCurrent2') ]

    def createOffPolicyControlDemon(self, rewardFunction):
        toStateAction = TabularAction(self.actions, self.representation.vectorSize())
        nbFeatures = toStateAction.actionStateFeatureSize()
        lambda_ = 0.1
        beta = .1
        alpha_v = .1 / self.representation.nbActive()
        alpha_w = .1 / self.representation.nbActive()
        gq = GQ(alpha_v, alpha_w, beta , lambda_, nbFeatures)
        targetPolicy = Greedy(gq, self.actions, toStateAction)
        controlGQ = ExpectedGQ(gq, self.actions, toStateAction, targetPolicy, self.behaviourPolicy)
        return ControlOffPolicyDemon(rewardFunction, controlGQ)
        
    def learn(self, a_t, o_tp1):
        for rewardFunction in self.rewards:
            rewardFunction.update(o_tp1)
        x_tp1 = self.representation.project(o_tp1)
        self.demons.update(self.x_t, a_t, x_tp1)
        self.x_t = x_tp1
        
    def run(self):
        a_t = None
        while not self.environment.isClosed():
            self.latencyTimer.start()
            o_tp1 = self.environment.waitNewObs()
            self.learn(a_t, o_tp1)
            a_tp1 = self.behaviourPolicy.decide(None)
            self.environment.sendAction(a_tp1)
            a_t = a_tp1
            waitingTime = self.Latency - self.latencyTimer.getCurrentMillis()
            if waitingTime > 0:
                time.sleep(waitingTime / 1000.0)
                
    def zephyrize(self):
        clock = self.environment.clock()
        zepy.advertise(self.environment, clock)
        zepy.advertise(self.demons, clock)
        for rewardFunction in self.rewards:
            zepy.monattr(rewardFunction, 'rewardValue', clock = clock, label = rewardFunction.label)
                

if __name__ == '__main__':
    experiment = DemonExperiment()
    experiment.zephyrize()
    experiment.run()
