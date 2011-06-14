/**
 * 
 */
package critterbot;

import rltoys.environments.envio.Agent;
import rltoys.environments.envio.observations.TStep;
import critterbot.actions.CritterbotAction;


public interface CritterbotAgent extends Agent {
  @Override
  CritterbotAction getAtp1(TStep step);
}