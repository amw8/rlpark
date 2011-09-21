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
  private double[] current = null;
  private ObservationVersatile nextObservation;
  private ObservationVersatile currentObservation;

  public CrtrLogFile(String filepath) {
    logfile = LogFile.load(filepath);
    step();
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

  public double[] step() {
    currentObservation = nextObservation;
    logfile.step();
    current = logfile.currentLine();
    nextObservation = new ObservationVersatile(logfile.clock.timeStep(), Robots.doubleArrayToByteArray(current),
                                               current);
    return current;
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
    return current;
  }

  @Override
  public int observationPacketSize() {
    return logfile.labels().length * 4;
  }

  @Override
  public ObservationVersatile[] waitNewRawObs() {
    step();
    return lastReceivedRawObs();
  }

  @Override
  public ObservationVersatile[] lastReceivedRawObs() {
    if (currentObservation == null)
      return null;
    return new ObservationVersatile[] { currentObservation };
  }

  @Override
  public ObservationVersatile getNewRawObs() {
    return Robots.last(waitNewRawObs());
  }
}
