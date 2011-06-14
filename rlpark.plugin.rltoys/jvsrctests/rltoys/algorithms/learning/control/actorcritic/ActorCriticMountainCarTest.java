package rltoys.algorithms.learning.control.actorcritic;


import java.util.Random;

import org.junit.Test;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.learning.control.actorcritic.onpolicy.ActorCritic;
import rltoys.algorithms.learning.control.actorcritic.onpolicy.ActorLambda;
import rltoys.algorithms.learning.control.actorcritic.policystructure.BoltzmannDistribution;
import rltoys.algorithms.learning.control.mountaincar.MountainCarOnPolicyTest;
import rltoys.algorithms.learning.predictions.td.OnPolicyTD;
import rltoys.algorithms.learning.predictions.td.TDLambda;
import rltoys.algorithms.learning.predictions.td.TDLambdaAutostep;
import rltoys.algorithms.representations.acting.PolicyDistribution;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.algorithms.representations.actions.TabularAction;
import rltoys.algorithms.representations.tilescoding.TileCoders;
import rltoys.environments.mountaincar.MountainCar;


public class ActorCriticMountainCarTest extends MountainCarOnPolicyTest {
  static class MountainCarActorCriticControlFactory implements MountainCarControlFactory {
    @Override
    public Control createControl(MountainCar mountainCar, TileCoders tilesCoder) {
      final double lambda = .3;
      final double gamma = .99;
      OnPolicyTD critic = createCritic(tilesCoder, lambda, gamma);
      StateToStateAction toStateAction = new TabularAction(mountainCar.actions(), tilesCoder.vectorSize());
      PolicyDistribution distribution = new BoltzmannDistribution(new Random(0), mountainCar.actions(), toStateAction);
      ActorLambda actor = new ActorLambda(lambda, distribution, .01 / tilesCoder.nbActive(), tilesCoder.vectorSize());
      return new ActorCritic(critic, actor);
    }

    protected OnPolicyTD createCritic(TileCoders tilesCoder, final double lambda, final double gamma) {
      return new TDLambda(lambda, gamma, .1 / tilesCoder.nbActive(), tilesCoder.vectorSize());
    }
  }

  @Test
  public void testDiscreteActorCriticOnMountainCar() {
    runTestOnOnMountainCar(2000, new MountainCarActorCriticControlFactory());
  }

  @Test
  public void testDiscreteAutostepActorCriticOnMountainCar() {
    runTestOnOnMountainCar(2000, new MountainCarActorCriticControlFactory() {
      @Override
      protected OnPolicyTD createCritic(TileCoders tilesCoder, final double lambda, final double gamma) {
        return new TDLambdaAutostep(lambda, gamma, tilesCoder.vectorSize());
      }
    });
  }
}
