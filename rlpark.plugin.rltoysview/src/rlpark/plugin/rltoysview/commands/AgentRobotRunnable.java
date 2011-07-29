package rlpark.plugin.rltoysview.commands;

import rlpark.plugin.robot.RobotEnvironment;
import rltoys.environments.envio.Agent;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.synchronization.Clock;

public class AgentRobotRunnable implements Runnable {
  private final CommandRunnableFactory environmentCommand;

  public AgentRobotRunnable(CommandRunnableFactory environmentCommand) {
    this.environmentCommand = environmentCommand;
  }

  @Override
  public void run() {
    RobotEnvironment environment = environmentCommand.createEnvironment();
    Agent agent = environmentCommand.createAgent();
    String clockName = String
        .format("%s[%s]", environment.getClass().getSimpleName(), agent.getClass().getSimpleName());
    Clock clock = new Clock(clockName);
    if (environment.isClosed())
      return;
    Zephyr.advertise(clock, environment);
    while (clock.tick() && !environment.isClosed())
      environment.sendAction(agent.getAtp1(environment.waitNewObs()));
    environment.close();
  }
}