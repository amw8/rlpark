package rlpark.example.learning;

import java.util.Random;

import rltoys.algorithms.learning.control.acting.EpsilonGreedy;
import rltoys.algorithms.learning.control.sarsa.Sarsa;
import rltoys.algorithms.learning.control.sarsa.SarsaControl;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.TabularAction;
import rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rltoys.algorithms.representations.traces.AMaxTraces;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.mountaincar.MountainCar;
import rltoys.math.vector.BinaryVector;
import rltoys.math.vector.RealVector;

public class SarsaMountainCar {
  public static void main(String[] args) {
    MountainCar problem = new MountainCar(null);
    TileCodersNoHashing tileCoders = new TileCodersNoHashing(problem.getObservationRanges());
    tileCoders.addFullTilings(10, 10);
    tileCoders.includeActiveFeature();
    TabularAction toStateAction = new TabularAction(problem.actions(), tileCoders.vectorSize());
    double alpha = .2 / tileCoders.nbActive();
    double gamma = 0.99;
    double lambda = .3;
    Sarsa sarsa = new Sarsa(alpha, gamma, lambda, toStateAction.vectorSize(), new AMaxTraces());
    double epsilon = 0.01;
    Policy acting = new EpsilonGreedy(new Random(0), problem.actions(), toStateAction, sarsa, epsilon);
    SarsaControl control = new SarsaControl(acting, toStateAction, sarsa);
    TRStep step = problem.initialize();
    int nbEpisode = 0;
    RealVector x_t = null;
    while (nbEpisode < 1000) {
      BinaryVector x_tp1 = tileCoders.project(step.o_tp1);
      Action action = control.step(x_t, step.a_t, x_tp1, step.r_tp1);
      x_t = x_tp1;
      if (step.isEpisodeEnding()) {
        System.out.println(String.format("Episode %d: %d steps", nbEpisode, step.time));
        step = problem.initialize();
        x_t = null;
        nbEpisode++;
      } else
        step = problem.step(action);
    }
  }
}
