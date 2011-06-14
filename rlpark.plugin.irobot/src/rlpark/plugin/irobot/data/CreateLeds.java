package rlpark.plugin.irobot.data;

import java.io.Serializable;

public class CreateLeds implements Serializable {
  private static final long serialVersionUID = -5241921974188253595L;
  public boolean advance = true;
  public boolean play = false;
  public int powerColor = 0;
  public int powerIntensity = 0;

  public CreateLeds() {
  }

  public CreateLeds(int powerColor, int powerIntensity, boolean play, boolean advance) {
    this.advance = advance;
    this.play = play;
    this.powerColor = powerColor;
    this.powerIntensity = powerIntensity;
  }
}
