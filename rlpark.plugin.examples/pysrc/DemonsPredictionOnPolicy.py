import time
import zepy
from critterbot.environment import CritterbotSimulator 
from zephyr.plugin.core.api.synchronization import Chrono
from rltoys.algorithms.representations.acting import RandomPolicy
from critterbot.actions import XYThetaAction
from java.util import Random
from rltoys.algorithms.representations.tilescoding import TileCodersNoHashing
from rltoys.demons import DemonScheduler, RewardFunction, PredictionDemon
from rltoys.algorithms.learning.predictions.td import TDLambda

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
            demon = self.createOnPolicyPredictionDemon(rewardFunction)
            self.demons.add(demon)
        self.x_t = None

    def createRewardFunction(self):
        legend = self.environment.legend()
        return list(SensorRewardFunction(legend, label) for label in legend.getLabels())

    def createOnPolicyPredictionDemon(self, rewardFunction):
        gamma = .9
        alpha = .1 / self.representation.nbActive()
        nbFeatures = self.representation.vectorSize()
        lambda_= .3
        td = TDLambda(lambda_, gamma, alpha, nbFeatures)
        return PredictionDemon(rewardFunction, td)
        
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
