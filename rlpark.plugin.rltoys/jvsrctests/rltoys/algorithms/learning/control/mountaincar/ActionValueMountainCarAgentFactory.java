package rltoys.algorithms.learning.control.mountaincar;

import java.util.Random;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.learning.control.acting.EpsilonGreedy;
import rltoys.algorithms.learning.control.mountaincar.MountainCarOnPolicyTest.MountainCarControlFactory;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.algorithms.representations.actions.TabularAction;
import rltoys.algorithms.representations.tilescoding.TileCoders;
import rltoys.environments.mountaincar.MountainCar;

public abstract class ActionValueMountainCarAgentFactory implements MountainCarControlFactory {
  @Override
  public Control createControl(MountainCar mountainCar, TileCoders tilesCoder) {
    StateToStateAction toStateAction = new TabularAction(mountainCar.actions(), tilesCoder.vectorSize());
    Predictor predictor = createPredictor(mountainCar.actions(), toStateAction,
                                          tilesCoder.nbActive(), toStateAction.actionStateFeatureSize());
    EpsilonGreedy acting = new EpsilonGreedy(new Random(0), mountainCar.actions(), toStateAction, predictor, 0.1);
    return createControl(predictor, tilesCoder, toStateAction, acting);
  }

  protected abstract Predictor createPredictor(Action[] actions, StateToStateAction toStateAction,
      int nbActiveFatures, int nbFeatures);

  protected abstract Control createControl(Predictor predictor, TileCoders tilesCoder,
      StateToStateAction toStateAction, EpsilonGreedy acting);
}