package rlpark.plugin.irobot.examples;

import java.util.Random;

import rlpark.plugin.irobot.robots.CreateRobot;
import rlpark.plugin.irobot.robots.IRobotEnvironment;

public class CreateLedRandom {
  public static void main(String[] args) {
    IRobotEnvironment environment = new CreateRobot();
    environment.waitNewObs();
    Random random = new Random(1);
    while (true) {
      environment.sendLeds(0, random.nextBoolean() ? 0 : 255, random.nextBoolean(), random.nextBoolean());
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
