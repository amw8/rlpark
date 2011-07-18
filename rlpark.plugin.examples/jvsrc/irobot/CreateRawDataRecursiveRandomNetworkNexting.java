package irobot;

import java.util.List;
import java.util.Random;

import rlpark.plugin.irobot.data.CreateAction;
import rlpark.plugin.irobot.robots.CreateRobot;
import rlpark.plugin.irobot.robots.IRobotEnvironment;
import rlpark.plugin.robot.Robots;
import rlpark.plugin.robot.sync.ObservationVersatile;
import rltoys.algorithms.learning.predictions.LinearLearner;
import rltoys.algorithms.learning.predictions.td.TDLambda;
import rltoys.algorithms.representations.acting.RandomPolicy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.ltu.StateUpdate;
import rltoys.algorithms.representations.ltu.discovery.RepresentationDiscovery;
import rltoys.algorithms.representations.ltu.discovery.WeightSorter;
import rltoys.algorithms.representations.ltu.networks.RandomNetwork;
import rltoys.algorithms.representations.ltu.networks.RandomNetworkAdaptive;
import rltoys.algorithms.representations.ltu.units.LTU;
import rltoys.algorithms.representations.ltu.units.LTUAdaptive;
import rltoys.algorithms.representations.traces.NRTraces;
import rltoys.demons.DemonScheduler;
import rltoys.demons.PredictionDemon;
import rltoys.demons.PredictionDemonVerifier;
import rltoys.demons.RewardFunction;
import rltoys.demons.RewardObservationFunction;
import rltoys.environments.envio.observations.Legend;
import rltoys.math.GrayCode;
import rltoys.math.vector.BVector;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.labels.Labels;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.parsing.LabelProvider;
import zephyr.plugin.core.api.synchronization.Clock;

@Monitor
public class CreateRawDataRecursiveRandomNetworkNexting implements Runnable {
  public final static double[] Gammas = new double[] { .9, .99 };
  protected static final double MinDensity = 0.01;
  protected static final double MaxDensity = 0.05;
  private final int NetworkOutputVectorSize = 10000;
  private final Clock clock = new Clock("Nexting");
  private final IRobotEnvironment environment = new CreateRobot();
  private final Random random = new Random(0);
  private final RandomPolicy policy = new RandomPolicy(random, CreateAction.AllActions);
  private final int rawObsVectorSize = environment.observationPacketSize() * 8;
  private final LTU prototype = new LTUAdaptive(MinDensity, MaxDensity);
  private final DemonScheduler demonScheduler;
  private final RewardObservationFunction[] rewardFunctions;
  private final PredictionDemonVerifier[] verifiers;
  private final StateUpdate stateUpdate;
  private final RepresentationDiscovery discovery;
  private BVector x_t;
  private Action a_t;
  double error;

  public CreateRawDataRecursiveRandomNetworkNexting() {
    RandomNetwork representation = new RandomNetworkAdaptive(random, NetworkOutputVectorSize + rawObsVectorSize + 1,
                                                             NetworkOutputVectorSize, MinDensity, MaxDensity);
    stateUpdate = new StateUpdate(representation, rawObsVectorSize);
    rewardFunctions = createRewardFunctions(environment.legend());
    int stateVectorSize = stateUpdate.stateSize();
    demonScheduler = createNextingDemons(Gammas, MaxDensity * stateVectorSize, stateVectorSize);
    verifiers = createDemonVerifiers();
    WeightSorter sorter = new WeightSorter(extractLinearLearners());
    discovery = new RepresentationDiscovery(random, representation, sorter, prototype, NetworkOutputVectorSize / 10, 5);
    discovery.fillNetwork();
    Zephyr.advertise(clock, this);
  }

  private LinearLearner[] extractLinearLearners() {
    LinearLearner[] learners = new LinearLearner[demonScheduler.nbDemons()];
    for (int i = 0; i < learners.length; i++)
      learners[i] = demonScheduler.demons().get(i).learner();
    return learners;
  }

  private PredictionDemonVerifier[] createDemonVerifiers() {
    PredictionDemonVerifier[] verifiers = new PredictionDemonVerifier[demonScheduler.nbDemons()];
    for (int i = 0; i < verifiers.length; i++) {
      PredictionDemon demon = (PredictionDemon) demonScheduler.demons().get(i);
      verifiers[i] = new PredictionDemonVerifier(demon);
    }
    return verifiers;
  }

  @LabelProvider(ids = { "rewardFunctions" })
  String rewardLabelOf(int rewardIndex) {
    return Labels.label(rewardFunctions[rewardIndex].label());
  }

  @LabelProvider(ids = { "verifiers", "demons" })
  String labelOf(int demonIndex) {
    return Labels.label(demonScheduler.demons().get(demonIndex));
  }

  private RewardObservationFunction[] createRewardFunctions(Legend legend) {
    List<String> labels = legend.getLabels();
    RewardObservationFunction[] rewardFunctions = new RewardObservationFunction[labels.size()];
    for (int i = 0; i < rewardFunctions.length; i++)
      rewardFunctions[i] = new RewardObservationFunction(legend, labels.get(i));
    return rewardFunctions;
  }

  private DemonScheduler createNextingDemons(double[] gammas, double stateFeatureNorm, int vectorSize) {
    DemonScheduler demonScheduler = new DemonScheduler();
    for (RewardFunction rewardFunction : rewardFunctions) {
      for (double gamma : gammas) {
        double alpha = .1 / stateFeatureNorm;
        int nbFeatures = vectorSize;
        TDLambda td = new TDLambda(.7, gamma, alpha, nbFeatures, new NRTraces((int) (stateFeatureNorm * 10)));
        demonScheduler.add(new PredictionDemon(rewardFunction, td));
      }
    }
    return demonScheduler;
  }

  protected void updateDemons(RealVector x_t, Action a_t, RealVector x_tp1) {
    demonScheduler.update(x_t, a_t, x_tp1);
    for (PredictionDemonVerifier verifier : verifiers) {
      error = verifier.update(false);
    }
  }

  protected void updateRewards(double[] o_tp1) {
    for (RewardObservationFunction rewardFunction : rewardFunctions)
      rewardFunction.update(o_tp1);
  }

  @Override
  public void run() {
    while (!environment.isClosed() && clock.tick()) {
      ObservationVersatile lastObs = Robots.last(environment.waitNewRawObs());
      updateRewards(lastObs.doubleValues());
      BVector binaryObs = BVector.toBinary(GrayCode.toGrayCode(lastObs.rawData()));
      BVector x_tp1 = stateUpdate.updateState(binaryObs);
      updateDemons(x_t, a_t, x_tp1);
      if (clock.timeStep() % 1000 == 0)
        discovery.changeRepresentation(1);
      a_t = policy.decide(x_tp1);
      environment.sendAction((CreateAction) a_t);
      x_t = x_tp1;
    }
  }

  public static void main(String[] args) {
    new CreateRawDataRecursiveRandomNetworkNexting().run();
  }
}
