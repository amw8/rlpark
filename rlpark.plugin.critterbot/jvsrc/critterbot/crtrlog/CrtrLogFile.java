package critterbot.crtrlog;

import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.robot.RobotLog;
import rlpark.plugin.robot.Robots;
import rlpark.plugin.robot.sync.ObservationVersatile;
import rltoys.environments.envio.observations.Legend;
import zephyr.plugin.core.api.logfiles.LogFile;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import critterbot.CritterbotProblem;
import critterbot.actions.CritterbotAction;

public class CrtrLogFile implements CritterbotProblem, RobotLog {
  public final String filepath;
  @Monitor(emptyLabel = true)
  private final LogFile logfile;
  private ObservationVersatile currentObservation;
  private final Legend legend;
  private final int timeIndex;

  public CrtrLogFile(String filepath) {
    logfile = LogFile.load(filepath);
    this.filepath = filepath;
    timeIndex = findTimeIndex();
    legend = createLegend();
  }

  private int findTimeIndex() {
    String[] labels = logfile.labels();
    for (int i = 0; i < labels.length; i++)
      if (labels[i].equals("LocalTime"))
        return i;
    return -1;
  }

  private Legend createLegend() {
    List<String> legendLabels = new ArrayList<String>();
    String[] labels = logfile.labels();
    for (int i = 0; i < labels.length; i++) {
      if (i == timeIndex)
        continue;
      legendLabels.add(labels[i]);
    }
    return new Legend(legendLabels);
  }

  @Override
  public Legend legend() {
    return legend;
  }

  @Override
  public ObservationVersatile nextStep() {
    logfile.step();
    double[] obs = logfile.currentLine();
    long localTime = 0;
    if (timeIndex >= 0) {
      localTime = (long) obs[timeIndex];
      obs = removeLocalTimeValue(obs);
    }
    currentObservation = new ObservationVersatile(localTime, Robots.doubleArrayToByteArray(obs), obs);
    return currentObservation;
  }

  private double[] removeLocalTimeValue(double[] obs) {
    double[] result = new double[obs.length - 1];
    System.arraycopy(obs, 0, result, 0, timeIndex);
    System.arraycopy(obs, timeIndex + 1, result, timeIndex, obs.length - timeIndex - 1);
    return result;
  }

  public double[] step() {
    return nextStep().doubleValues();
  }

  @Override
  public boolean hasNextStep() {
    return !logfile.eof();
  }

  @Override
  public void close() {
    logfile.close();
  }

  public String filepath() {
    return logfile.filepath;
  }

  @Override
  public String label() {
    return logfile.label();
  }

  public static CrtrLogFile load(String filepath) {
    return new CrtrLogFile(filepath);
  }

  @Override
  public CritterbotAction lastAction() {
    return null;
  }

  @Override
  public double[] lastReceivedObs() {
    return currentObservation.doubleValues();
  }

  @Override
  public int observationPacketSize() {
    return logfile.labels().length * 4;
  }
}
