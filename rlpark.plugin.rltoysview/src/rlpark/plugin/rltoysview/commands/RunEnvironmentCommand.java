package rlpark.plugin.rltoysview.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import zephyr.ZephyrCore;

public abstract class RunEnvironmentCommand extends AbstractHandler implements CommandRunnableFactory {
  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {
    ZephyrCore.start(new AgentRobotRunnable(this));
    return null;
  }
}
