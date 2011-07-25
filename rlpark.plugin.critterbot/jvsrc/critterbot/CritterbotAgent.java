/**
 * 
 */
package critterbot;

import rltoys.environments.envio.Agent;
import rltoys.environments.envio.observations.TStep;
import critterbot.actions.CritterbotAction;

/**
 * Create your own interface with your own main loop
 */
@Deprecated
public interface CritterbotAgent extends Agent {
  @Override
  CritterbotAction getAtp1(TStep step);
}