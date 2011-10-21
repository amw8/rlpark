package rlpark.example.surprise;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rlpark.plugin.irobot.data.CreateAction;
import rlpark.plugin.irobot.data.IRobotSongs;
import rlpark.plugin.irobot.robots.CreateRobot;
import rlpark.plugin.irobot.robots.IRobotEnvironment;
import rltoys.algorithms.learning.predictions.td.OnPolicyTD;
import rltoys.algorithms.learning.predictions.td.TDLambda;
import rltoys.algorithms.representations.AgentState;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.observations.Observation;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.ObsFilter;
import rltoys.horde.Horde;
import rltoys.horde.Surprise;
import rltoys.horde.demons.Demon;
import rltoys.horde.demons.PredictionDemon;
import rltoys.horde.functions.RewardFunction;
import rltoys.horde.functions.RewardObservationFunction;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Chrono;
import zephyr.plugin.core.api.synchronization.Clock;

@Monitor
public class CreateSurprise implements Runnable {
  static final private double MinSongPeriod = 2 * 60;
  static final private int SurpriseTrackingSpeed = 100;
  static final private Action[] Actions = new Action[] { CreateAction.DontMove, CreateAction.SpinLeft,
      CreateAction.SpinRight, CreateAction.Forward };
  static final private String[] PredictedLabels = new String[] { "WheelDrop", "Bump", "WheelOverCurrent", "ICOmni",
      "DriveDistance", "DriveAngle", "BatteryCurrent", "BatteryCharge", "WallSignal", "CliffSignal",
      "ConnectedHomeBase", "OIMode", "WheelRequested" };
  static final private double[] Gammas = new double[] { .0, 0.9, 0.99 };
  static final private double Lambda = .7;
  final private Chrono lastSongTime = new Chrono(Chrono.longTimeAgo());
  final private IRobotEnvironment robot = new CreateRobot();
  final private Clock clock = new Clock("Surprise");
  final private Horde horde;
  final private Surprise surprise;
  private final AgentState agentState;
  private final Policy robotBehaviour;
  private RealVector x_t;
  private Action a_t;

  public CreateSurprise() {
    Zephyr.advertise(clock, this);
    horde = createHorde();
    surprise = new Surprise(horde.demons(), SurpriseTrackingSpeed);
    agentState = new RobotState();
    robotBehaviour = new RobotBehaviour(new Random(0), .25, Actions);
  }

  private Horde createHorde() {
    List<RewardFunction> rewardFunctions = createRewardFunctions();
    List<Demon> demons = new ArrayList<Demon>();
    for (RewardFunction rewardFunction : rewardFunctions) {
      for (double gamma : Gammas) {
        OnPolicyTD td = new TDLambda(Lambda, gamma, .1 / agentState.stateNorm(), agentState.stateSize());
        demons.add(new PredictionDemon(rewardFunction, td));
      }
    }
    return new Horde(demons, rewardFunctions, null, null);
  }

  private List<RewardFunction> createRewardFunctions() {
    ArrayList<RewardFunction> rewardFunctions = new ArrayList<RewardFunction>();
    Legend legend = robot.legend();
    ObsFilter filter = new ObsFilter(legend, PredictedLabels);
    for (String label : filter.legend().getLabels())
      rewardFunctions.add(new RewardObservationFunction(legend, label));
    return rewardFunctions;
  }

  @Override
  public void run() {
    while (clock.tick()) {
      Observation o_tp1 = robot.waitNewRawObs();
      RealVector x_tp1 = agentState.update(a_t, o_tp1);
      horde.update(o_tp1, x_t, a_t, x_tp1);
      double surpriseMeasure = surprise.updateSurpriseMeasure();
      if (surpriseMeasure > 8.0 && lastSongTime.getCurrentChrono() > MinSongPeriod) {
        robot.playSong(IRobotSongs.composeHappySong());
        lastSongTime.start();
      }
      Action a_tp1 = robotBehaviour.decide(x_tp1);
      robot.sendAction((CreateAction) a_tp1);
      x_t = x_tp1;
      a_t = a_tp1;
    }
  }
}
