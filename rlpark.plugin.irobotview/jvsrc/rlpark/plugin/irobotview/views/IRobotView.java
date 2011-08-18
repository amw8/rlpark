package rlpark.plugin.irobotview.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IToolBarManager;

import rlpark.plugin.irobot.logfiles.IRobotLogFile;
import rlpark.plugin.irobotview.filehandlers.IRobotLogFileHandler;
import rlpark.plugin.robot.RobotProblem;
import rlpark.plugin.robot.Robots;
import rltoys.math.ranges.Range;
import rltoys.utils.Utils;
import zephyr.ZephyrCore;
import zephyr.plugin.core.actions.RestartAction;
import zephyr.plugin.core.actions.TerminateAction;
import zephyr.plugin.core.api.synchronization.Closeable;
import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.observations.EnvironmentView;
import zephyr.plugin.core.observations.SensorGroup;
import zephyr.plugin.core.observations.SensorTextGroup.TextClient;
import zephyr.plugin.core.views.Restartable;

public abstract class IRobotView extends EnvironmentView<RobotProblem> implements Closeable, Restartable {
  static abstract public class IRobotViewProvider extends ClassViewProvider {
    public IRobotViewProvider() {
      super(RobotProblem.class);
    }
  }

  protected class IntegerTextClient extends TextClient {
    private final String defaultString;
    private final int labelIndex;
    private final String suffix;

    public IntegerTextClient(String obsLabel, String textLabel) {
      this(obsLabel, textLabel, "0");
    }

    public IntegerTextClient(String obsLabel, String textLabel, String defaultString) {
      this(obsLabel, textLabel, "0", "");
    }

    @SuppressWarnings("synthetic-access")
    public IntegerTextClient(String obsLabel, String textLabel, String defaultString, String suffix) {
      super(textLabel);
      labelIndex = environment.legend().indexOf(obsLabel);
      this.defaultString = defaultString;
      this.suffix = suffix;
      assert labelIndex >= 0;
    }

    @Override
    public String currentText() {
      if (currentObservation == null)
        return defaultString + suffix;
      return String.valueOf((int) currentObservation[labelIndex]) + suffix;
    }
  }

  protected double[] currentObservation;
  private final TerminateAction terminateAction;
  private final RestartAction restartAction;

  public IRobotView() {
    terminateAction = new TerminateAction(this);
    terminateAction.setEnabled(false);
    restartAction = new RestartAction(this);
    restartAction.setEnabled(false);
  }

  @Override
  protected void setToolbar(IToolBarManager toolBarManager) {
    toolBarManager.add(restartAction);
    toolBarManager.add(terminateAction);
  }

  protected SensorGroup createSensorGroup(String title, String prefix) {
    return new SensorGroup(title, startsWith(prefix));
  }

  abstract protected Range[] ranges();

  private int[] startsWith(String prefix) {
    List<Integer> result = new ArrayList<Integer>();
    for (Map.Entry<String, Integer> entry : environment.legend().legend().entrySet()) {
      String label = entry.getKey();
      if (label.startsWith(prefix))
        result.add(entry.getValue());
    }
    Collections.sort(result);
    return Utils.asIntArray(result);
  }

  @Override
  public boolean synchronize() {
    currentObservation = Robots.toDoubles(environment.lastReceivedRawObs());
    synchronize(currentObservation);
    return true;
  }

  @Override
  protected void set(RobotProblem current) {
    super.set(current);
    restartAction.setEnabled(current instanceof IRobotLogFile);
    terminateAction.setEnabled(true);
    setViewTitle();
  }

  private void setViewTitle() {
    if (environment == null)
      setViewName("Observation", "");
    IRobotLogFile logFile = environment instanceof IRobotLogFile ? (IRobotLogFile) environment : null;
    String viewTitle = logFile == null ? environment.getClass().getSimpleName() :
        new File(logFile.filepath()).getName();
    String tooltip = logFile == null ? "" : logFile.filepath();
    setViewName(viewTitle, tooltip);
  }

  @Override
  protected void unset() {
    super.unset();
    restartAction.setEnabled(false);
    terminateAction.setEnabled(false);
  }

  @Override
  public void restart() {
    if (!(environment instanceof IRobotLogFile))
      return;
    final String filepath = ((IRobotLogFile) environment).filepath();
    close();
    ZephyrCore.start(new Runnable() {
      @Override
      public void run() {
        IRobotLogFileHandler.handle(filepath);
      }
    });
  }

  @Override
  public void close() {
    instance.unset();
  }
}