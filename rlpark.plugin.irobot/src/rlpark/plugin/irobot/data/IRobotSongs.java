package rlpark.plugin.irobot.data;

import java.util.Random;

public class IRobotSongs {
  static private final Random random = new Random(0);

  static public final int[] ViveLeVent = new int[] {
      66, 20, 66, 20, 66, 34,
      66, 20, 66, 20, 66, 34,
      66, 20, 69, 20, 62, 22, 64, 14,
      66, 70 };
  static public final int[] CloseEncounter = new int[] {
      74, 40, 76, 40, 72, 40, 60, 40, 67, 50 };
  static public final int[] Beethov5 = new int[] {
      55, 16, 55, 16, 55, 16, 51, 48, 128, 16, 53, 16, 53, 16, 53, 16, 50, 48
  };
  static public final int[] Beethov9 = new int[] {
      83, 16, 83, 16, 84, 16, 86, 16, 86, 16,
      84, 16, 83, 16, 81, 16, 79, 16, 79, 16,
      81, 16, 83, 16, 83, 32, 81, 8, 81, 16
  };
  static public final int[] DarthVador = new int[] {
      43, 48, 43, 48, 43, 48,
      39, 32, 46, 16, 43, 48,
      39, 32, 46, 16, 43, 32,
  };
  static public final int[] Piaf = new int[] {
      74, 100, 76, 16, 74, 16, 74, 80, 128, 8, 74, 100, 78, 16, 76, 16, 74, 16, 72, 16, 71, 16, 71, 32
  };
  static public final int[] StarTrek = new int[] {
      62, 32, 67, 16, 72, 48,
      128, 2, 71, 22, 67, 22, 64, 22,
      69, 22, 74, 64
  };
  static public final int[][] Repertoire = {
      ViveLeVent, CloseEncounter, Beethov5, Beethov9, DarthVador, StarTrek, Piaf
  };

  public static int[] composeHappySong() {
    int songLength = random.nextInt(5) + 7;
    int[] song = new int[songLength * 2];
    for (int i = 0; i < songLength; i++) {
      int noteIndex = i * 2;
      song[noteIndex] = random.nextInt(10) + 96;
      song[noteIndex + 1] = 2 + random.nextInt(8);
    }
    return song;
  }

  public static int[] composeSadSong() {
    int songLength = random.nextInt(2) + 3;
    int[] song = new int[songLength * 2];
    for (int i = 0; i < songLength - 1; i++) {
      int noteIndex = i * 2;
      song[noteIndex] = random.nextInt(5) + 31;
      song[noteIndex + 1] = 20 + random.nextInt(20);
    }
    song[song.length - 2] = 31;
    song[song.length - 1] = 20;
    return song;
  }
}
