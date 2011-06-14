from rlpark.plugin.irobot.data import CreateAction;
from rlpark.plugin.irobot.data import IRobotDrops;
from rlpark.plugin.irobot.robots import CreateRobot;
from zephyr.plugin.core.api.synchronization import Clock
import zepy

if __name__ == '__main__':
    robot = CreateRobot("/dev/cu.ElementSerial-ElementSe")
    clock = Clock()
    zepy.advertise(robot, clock)
    bumpRightObsIndex = robot.legend().indexOf(IRobotDrops.BumpRight)
    bumpLeftObsIndex = robot.legend().indexOf(IRobotDrops.BumpLeft)
    robot.safeMode()
    while clock.tick():
      obs = robot.waitNewObs()
      wheelLeft = 150 if obs[bumpRightObsIndex] == 0 else -150
      wheelRight = 150  if obs[bumpLeftObsIndex] == 0 else -70
      robot.sendAction(CreateAction(wheelLeft, wheelRight))
      
