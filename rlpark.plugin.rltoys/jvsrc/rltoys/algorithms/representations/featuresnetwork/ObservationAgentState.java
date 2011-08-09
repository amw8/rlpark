package rltoys.algorithms.representations.featuresnetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rltoys.algorithms.representations.features.envio.ActionFeature;
import rltoys.algorithms.representations.features.envio.ObservationFeature;
import rltoys.algorithms.representations.features.envio.StepFeature;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.TStep;


public class ObservationAgentState extends AbstractAgentState {

  private static final long serialVersionUID = 8083504087752741914L;
  private final List<ObservationFeature> observationFunctions = new ArrayList<ObservationFeature>();
  private final List<ActionFeature> actionFunctions = new ArrayList<ActionFeature>();

  public ObservationAgentState(Legend legend, int nbActionParameters) {
    this(legend);
    for (int i = 0; i < nbActionParameters; i++)
      actionFunctions.add(new ActionFeature(i));
  }

  public ObservationAgentState(Legend legend) {
    for (Map.Entry<String, Integer> entry : legend.legend().entrySet())
      observationFunctions.add(new ObservationFeature(entry.getKey(), entry.getValue()));
  }

  public ObservationAgentState(int nbActionParameters, ObservationFeature... observationFunctions) {
    for (ObservationFeature observation : observationFunctions)
      this.observationFunctions.add(observation);
    for (int i = 0; i < nbActionParameters; i++)
      actionFunctions.add(new ActionFeature(i));
  }

  public IndexesRange addObservationsToState() {
    return addStepFeatureToState(observationFunctions);
  }

  public IndexesRange addActionsToState() {
    assert !actionFunctions.isEmpty();
    return addStepFeatureToState(actionFunctions);
  }

  private IndexesRange addStepFeatureToState(List<? extends StepFeature> stepFeatures) {
    int beforeStateSize = getStateFeatures().size();
    for (StepFeature stepFeature : stepFeatures)
      addStateFeature(stepFeature);
    return new IndexesRange(beforeStateSize, getStateFeatures().size());
  }

  public void update(TStep step) {
    if (step.o_tp1 == null) {
      stateVector().set(0.0);
      return;
    }
    for (StepFeature function : observationFunctions)
      function.update(step);
    for (StepFeature function : actionFunctions)
      function.update(step);
    update();
  }

  public List<ObservationFeature> observations() {
    return observationFunctions;
  }
}
