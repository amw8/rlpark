from rlpark.plugin.irobot.data import CreateAction;
from rlpark.plugin.irobot.data import IRobotDrops;
from rlpark.plugin.irobot.robots import RoombaRobot;
import zepy

if __name__ == '__main__':
    robot = RoombaRobot()
    zepy.advertise(robot)
    bumpRightObsIndex = robot.legend().indexOf(IRobotDrops.BumpRight)
    bumpLeftObsIndex = robot.legend().indexOf(IRobotDrops.BumpLeft)
    robot.safeMode()
    while not robot.isClosed():
      obs = robot.waitNewObs()
      wheelLeft = 100 if obs[bumpRightObsIndex] == 0 else -100
      wheelRight = 100  if obs[bumpLeftObsIndex] == 0 else -70
      robot.sendAction(CreateAction(wheelLeft, wheelRight))
