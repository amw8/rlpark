package rlpark.plugin.irobotview.command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;

import rlpark.plugin.irobotview.dialog.SelectSerialPortDialog;

public abstract class EnvironmentSerialPortCommand extends AbstractHandler {
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    SelectSerialPortDialog dialog = new SelectSerialPortDialog(Display.getCurrent().getActiveShell());
    dialog.open();
    Object[] results = dialog.getResult();
    String serialPortPath = results != null ? (String) results[0] : null;
    if (serialPortPath == null)
      return null;
    startRunnable(serialPortPath);
    return null;
  }

  abstract protected void startRunnable(String serialPortPath);
}
