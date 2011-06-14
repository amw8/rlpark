package rlpark.plugin.irobot.examples;

import rlpark.plugin.irobot.data.CreateAction;
import rlpark.plugin.irobot.robots.CreateAgent;
import rlpark.plugin.irobot.robots.CreateRobot;
import rltoys.environments.envio.observations.TStep;

public class ConstantAgent implements CreateAgent {
  private final CreateAction action;

  public ConstantAgent() {
    this(null);
  }

  public ConstantAgent(CreateAction action) {
    this.action = action;
  }

  @Override
  public CreateAction getAtp1(TStep step) {
    if (action != null)
      return action;
    return new CreateAction(20, 20);
  }

  @Override
  public String toString() {
    return action.toString();
  }

  public static void main(String[] args) {
    CreateRobot environment = new CreateRobot();
    environment.run(new ConstantAgent());
  }
}
