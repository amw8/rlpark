package rltoys.experiments.reinforcementlearning;

import java.util.Random;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.RLParameters;

@SuppressWarnings("serial")
class RLProblemFactoryTest implements ProblemFactory {
  private final int nbEpisode;
  private final int nbTimeSteps;
  static final Action Action01 = new ActionArray(1.0);
  static final Action Action02 = new ActionArray(2.0);

  RLProblemFactoryTest(int nbEpisode, int nbTimeSteps) {
    this.nbEpisode = nbEpisode;
    this.nbTimeSteps = nbTimeSteps;
  }

  @Override
  public String label() {
    return "Problem";
  }

  @Override
  public void setExperimentParameters(Parameters parameters) {
    parameters.put(RLParameters.MaxEpisodeTimeSteps, nbTimeSteps);
    parameters.put(RLParameters.NbEpisode, nbEpisode);
  }

  @Override
  public RLProblem createEnvironment(Random random) {
    return new RLProblem() {
      TRStep last = null;

      @Override
      public TRStep step(Action action) {
        double reward = ((ActionArray) action).actions[0];
        TRStep result = new TRStep(last, action, new double[] {}, reward);
        last = result;
        return result;
      }

      @Override
      public Legend legend() {
        return new Legend();
      }

      @Override
      public TRStep initialize() {
        last = new TRStep(new double[] {}, 1);
        return last;
      }
    };
  }
}