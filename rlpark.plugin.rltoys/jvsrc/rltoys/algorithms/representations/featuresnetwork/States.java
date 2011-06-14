package rltoys.algorithms.representations.featuresnetwork;

import java.util.List;

import rltoys.algorithms.representations.features.Feature;
import rltoys.algorithms.representations.features.Multiply;
import rltoys.algorithms.representations.features.Positive;
import rltoys.algorithms.representations.features.StepDelay;
import rltoys.algorithms.representations.features.TrackingHistory;
import rltoys.algorithms.representations.features.envio.ObservationFeature;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.Legends;
import rltoys.math.representations.Function;

public class States {
  public static void addAveraged(ObservationAgentState agentState, double alpha) {
    List<Feature> stateFeatures = agentState.getStateFeatures();
    for (int i = 0; i < stateFeatures.size(); i++)
      agentState.addStateFeature(new TrackingHistory(alpha, stateFeatures.get(i)));
  }

  static public void multiplyStateFeatures(AbstractAgentState agentState) {
    List<Feature> stateFeatures = agentState.getStateFeatures();
    for (int i = 0; i < stateFeatures.size(); i++)
      for (int j = i; j < stateFeatures.size(); j++) {
        Function fi = stateFeatures.get(i);
        Function fj = stateFeatures.get(j);
        Multiply fij = new Multiply(fi, fj);
        agentState.addStateFeature(fij);
      }
  }

  public static void addSplit(AbstractAgentState agentState) {
    List<Feature> stateFeatures = agentState.getStateFeatures();
    for (int i = 0; i < stateFeatures.size(); i++) {
      Feature f = stateFeatures.get(i);
      agentState.addStateFeature(new Positive(f, false));
      agentState.addStateFeature(new Positive(f, true));
    }
  }

  public static void addPastState(ObservationAgentState agentState, int nbPastTimeStep) {
    addPastState(new IndexesRange(0, agentState.getStateFeatures().size()), agentState, nbPastTimeStep);
  }

  public static void addPastState(IndexesRange range, ObservationAgentState agentState, int nbPastTimeStep) {
    List<Feature> stateFeatures = agentState.getStateFeatures();
    for (int t = 1; t <= nbPastTimeStep; t++)
      for (int i = range.min; i < range.max; i++) {
        Function f = stateFeatures.get(i);
        StepDelay fm1 = new StepDelay(f);
        agentState.addStateFeature(fm1);
        stateFeatures.set(i, fm1);
      }
  }

  public static ObservationFeature[] getObservationFeatures(Legend legend, boolean excluded, String... prefixes) {
    int[] indexes = Legends.getSelectedIndexes(legend, excluded, prefixes);
    ObservationFeature[] features = new ObservationFeature[indexes.length];
    for (int i = 0; i < features.length; i++)
      features[i] = new ObservationFeature(legend.label(indexes[i]), indexes[i]);
    return features;
  }
}
