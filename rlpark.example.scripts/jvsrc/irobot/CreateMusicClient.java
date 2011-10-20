package irobot;

import rlpark.plugin.irobot.data.IRobotSongs;
import rlpark.plugin.irobot.robots.CreateRobot;

public class CreateMusicClient {

  public static void main(String[] args) {
    CreateRobot environment = new CreateRobot("/dev/cu.ElementSerial-ElementSe");
    environment.waitNewObs();
    environment.sendLeds(0, 255, true, true);
    environment.playSong(IRobotSongs.StarTrek);
  }
}
