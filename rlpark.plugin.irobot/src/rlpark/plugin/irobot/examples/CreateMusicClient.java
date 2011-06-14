package rlpark.plugin.irobot.examples;

import rlpark.plugin.irobot.data.IRobotSongs;
import rlpark.plugin.irobot.robots.CreateRobot;
import rlpark.plugin.irobot.robots.IRobotEnvironment;

public class CreateMusicClient {
  public static void main(String[] args) {
    IRobotEnvironment environment = new CreateRobot();
    environment.waitNewObs();
    environment.sendLeds(0, 255, true, true);
    while (true) {
      environment.playSong(IRobotSongs.composeSadSong());
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
