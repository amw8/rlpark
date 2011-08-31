package critterbot.crtrlog;

import java.util.ArrayList;
import java.util.List;

import rltoys.environments.envio.observations.Legend;
import zephyr.plugin.core.api.logfiles.LogFile;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.api.synchronization.Timed;
import critterbot.CritterbotProblem;
import critterbot.actions.CritterbotAction;

public class CrtrLogFile implements CritterbotProblem, Timed {
  public final String filepath;
  @Monitor(emptyLabel = true)
  private final LogFile logfile;
  private double[] current = null;

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

  public double[] step() {
    logfile.step();
    current = logfile.currentLine();
    return current;
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
}
