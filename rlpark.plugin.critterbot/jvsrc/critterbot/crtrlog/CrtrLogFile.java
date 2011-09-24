package critterbot.crtrlog;

import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.robot.RobotLog;
import rlpark.plugin.robot.Robots;
import rlpark.plugin.robot.sync.ObservationVersatile;
import rltoys.environments.envio.observations.Legend;
import zephyr.plugin.core.api.logfiles.LogFile;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.Timed;
import critterbot.CritterbotProblem;
import critterbot.actions.CritterbotAction;

public class CrtrLogFile implements CritterbotProblem, RobotLog, Timed {
  public final String filepath;
  @Monitor(emptyLabel = true)
  private final LogFile logfile;
  private double[] nextObervation = null;

  public CrtrLogFile(String filepath) {
    logfile = LogFile.load(filepath);
    this.filepath = filepath;
  }

  @Override
  public Clock clock() {
    return logfile.clock();
  }

  @Override
  public Legend legend() {
    List<String> labels = new ArrayList<String>();
    for (String label : logfile.labels())
      labels.add(label);
    return new Legend(labels);
  }

  @Override
  public ObservationVersatile nextStep() {
    logfile.step();
    nextObervation = logfile.currentLine();
    return new ObservationVersatile(logfile.clock.timeStep(), Robots.doubleArrayToByteArray(nextObervation),
                                    nextObervation);
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
    return nextObervation;
  }

  @Override
  public int observationPacketSize() {
    return logfile.labels().length * 4;
  }
}
