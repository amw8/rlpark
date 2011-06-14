package rlpark.plugin.irobot.examples;

import rlpark.plugin.irobot.data.CreateAction;
import rlpark.plugin.irobot.robots.CreateAgent;
import rlpark.plugin.irobot.robots.CreateRobot;
import rltoys.environments.envio.observations.TStep;

public class CreateClient implements CreateAgent {
  private int action = 0;

  public CreateClient() {
  }

  @Override
  public CreateAction getAtp1(TStep step) {
    action = (action + 1) % 500;
    action *= 0;
    // return new CreateAction(CreateAction.Forward);
    return new CreateAction(-action, action);
  }

  public static void main(String[] args) {
    CreateRobot environment = new CreateRobot();
    environment.run(new CreateClient());
  }
}
