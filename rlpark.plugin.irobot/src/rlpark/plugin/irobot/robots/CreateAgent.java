package rlpark.plugin.irobot.robots;

import rlpark.plugin.irobot.data.CreateAction;
import rltoys.environments.envio.Agent;
import rltoys.environments.envio.observations.TStep;

public interface CreateAgent extends Agent {
  @Override
  CreateAction getAtp1(TStep step);
}
