package rlpark.example.robots.irobot;


import rlpark.plugin.irobot.data.CreateAction;
import rlpark.plugin.irobot.data.IRobotDrops;
import rlpark.plugin.irobot.robots.CreateRobot;

public class CreateIntroSessionJava {
  public static void main(String[] args) {
    CreateRobot robot = new CreateRobot("/dev/cu.ElementSerial-ElementSe");
    int bumpRightObsIndex = robot.legend().indexOf(IRobotDrops.BumpRight);
    int bumpLeftObsIndex = robot.legend().indexOf(IRobotDrops.BumpLeft);
    robot.safeMode();
    while (!robot.isClosed()) {
      double obs[] = robot.waitNewObs();
      double wheelLeft = obs[bumpRightObsIndex] == 0 ? 150 : -150;
      double wheelRight = obs[bumpLeftObsIndex] == 0 ? 150 : -70;
      robot.sendAction(new CreateAction(wheelLeft, wheelRight));
    }
  }
}
