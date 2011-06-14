package critterbot.examples;

import java.awt.Color;

import rltoys.environments.envio.observations.TStep;
import critterbot.CritterbotAgent;
import critterbot.actions.WheelSpaceAction;
import critterbot.environment.CritterbotDrops;
import critterbot.environment.CritterbotEnvironment;
import critterbot.environment.CritterbotRobot;

public class ColoredAgent implements CritterbotAgent {
  final private WheelSpaceAction action = new WheelSpaceAction(10.0, 10.0, 0.0);
  final Color[] colors = new Color[CritterbotDrops.NbLeds];
  private final CritterbotEnvironment environment;
  static int ledOffset = 0;
  static double currentCount = 0.0;
  static int time = 0;

  public ColoredAgent(CritterbotEnvironment environment) {
    this.environment = environment;
  }

  @Override
  public WheelSpaceAction getAtp1(TStep step) {
    environment.setLed(computeLeds());
    return action;
  }

  private Color[] computeLeds() {
    return computeLeds(colors);
  }

  static public Color[] computeLeds(Color[] colors) {
    currentCount += 0.01;
    time++;
    if (time % 8 == 0)
      ledOffset += 1;
    Color[] colorsSuite = { Color.BLUE, Color.RED, Color.GREEN };
    for (int i = 0; i < colors.length; i++) {
      int ledIndex = (i + ledOffset) % colors.length;
      colors[ledIndex] = computeColor(colorsSuite, currentCount + i);
    }
    return colors;
  }

  static private Color computeColor(Color[] colorsSuite, double countColor) {
    double floor = Math.floor(countColor);
    double ceil = Math.ceil(countColor);
    double ratio = countColor - floor;
    Color floorColor = colorsSuite[(int) floor % colorsSuite.length];
    Color ceilColor = colorsSuite[(int) ceil % colorsSuite.length];
    return new Color((int) (floorColor.getRed() * (1 - ratio) + ceilColor.getRed() * ratio),
                     (int) (floorColor.getGreen() * (1 - ratio) + ceilColor.getGreen() * ratio),
                     (int) (floorColor.getBlue() * (1 - ratio) + ceilColor.getBlue() * ratio));
  }

  public static void main(String[] args) {
    CritterbotEnvironment environment = new CritterbotRobot();
    environment.run(new ColoredAgent(environment));
  }
}
