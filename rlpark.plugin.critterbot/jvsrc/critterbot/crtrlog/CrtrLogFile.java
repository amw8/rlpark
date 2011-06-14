package critterbot.crtrlog;

import java.util.ArrayList;
import java.util.List;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.TStep;
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
  private TStep step = null;

  public CrtrLogFile(String filepath) {
    logfile = LogFile.load(filepath);
    this.filepath = filepath;
  }

  public TStep currentStep() {
    return step;
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

  public TStep step() {
    logfile.step();
    if (step == null)
      step = new TStep(0, (double[]) null, null, logfile.currentLine());
    else
      step = new TStep(step, Action.ActionUndef, logfile.currentLine());
    return step;
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

  public long nbSteps() {
    return step.time + 1;
  }

  @Override
  public CritterbotAction lastAction() {
    return (CritterbotAction) (step != null ? step.a_t : null);
  }

  @Override
  public double[] lastReceivedObs() {
    return step != null ? step.o_tp1 : null;
  }
}
