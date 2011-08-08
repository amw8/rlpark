package rlpark.plugin.irobot.logfiles;

import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.robot.RobotProblem;
import rlpark.plugin.robot.sync.ObservationVersatile;
import rltoys.environments.envio.observations.Legend;
import zephyr.plugin.core.api.logfiles.LogFile;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.Timed;

public class IRobotLogFile implements RobotProblem, Timed {
  public static final String Extension = "irobotlog";
  private ObservationVersatile[] lastReceived = null;

  @Monitor(emptyLabel = true)
  private final LogFile logfile;

  public IRobotLogFile(String filepath) {
    logfile = LogFile.load(filepath);
  }

  public double[] lastReceivedObs() {
    return logfile.currentLine();
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

  public void step() {
    if (hasNextStep()) {
      if (lastReceived == null)
        lastReceived = new ObservationVersatile[1];
      logfile.step();
      lastReceived[0] = new ObservationVersatile(null, logfile.currentLine());
    } else {
      lastReceived = null;
    }
  }

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
  public int observationPacketSize() {
    return 0;
  }

  @Override
  public ObservationVersatile[] waitNewRawObs() {
    step();
    return lastReceived;
  }

  @Override
  public ObservationVersatile[] lastReceivedRawObs() {
    return lastReceived;
  }
}
