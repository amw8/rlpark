package rlpark.plugin.irobot.examples;

import rlpark.plugin.irobot.data.RoombaLeds;
import rlpark.plugin.irobot.robots.RoombaRobot;

public class RoombaClient {
  private int cleanColor;
  private final RoombaRobot environment = new RoombaRobot();
  private final RoombaLeds leds = new RoombaLeds();

  private void run() {
    environment.fullMode();
    while (!environment.isClosed()) {
      environment.waitNewObs();
      updateRobotLeds();
      environment.sendAction(60, -60);
    }
  }

  private void updateRobotLeds() {
    leds.intensity = 255;
    leds.cleanColor = cleanColor;
    cleanColor = (cleanColor + 5) % 256;
    leds.dirt = !leds.dirt;
    environment.sendLeds(leds);
  }

  public static void main(String[] args) {
    new RoombaClient().run();
  }
}
