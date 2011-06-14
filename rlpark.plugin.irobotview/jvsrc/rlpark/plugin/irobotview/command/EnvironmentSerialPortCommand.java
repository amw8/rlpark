package rlpark.plugin.irobotview.command;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;

import rlpark.plugin.irobot.data.CreateAction;
import rlpark.plugin.irobot.examples.ConstantAgent;
import rlpark.plugin.irobot.robots.IRobotEnvironment;
import rlpark.plugin.irobotview.dialog.SelectSerialPortDialog;
import rlpark.plugin.rltoysview.commands.RunEnvironmentCommand;
import rlpark.plugin.robot.RobotEnvironment;
import rltoys.environments.envio.Agent;

public abstract class EnvironmentSerialPortCommand extends RunEnvironmentCommand {
  private String serialPortPath = null;

  @Override
  public Agent createAgent() {
    return new ConstantAgent(CreateAction.DoNothing);
  }

  @Override
  public RobotEnvironment createEnvironment() {
    return createIRobotEnvironment(serialPortPath);
  }

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    SelectSerialPortDialog dialog = new SelectSerialPortDialog(Display.getCurrent().getActiveShell());
    dialog.open();
    Object[] results = dialog.getResult();
    serialPortPath = results != null ? (String) results[0] : null;
    if (serialPortPath == null)
      return null;
    return super.execute(event);
  }

  abstract protected IRobotEnvironment createIRobotEnvironment(String serialPortPath);
}
