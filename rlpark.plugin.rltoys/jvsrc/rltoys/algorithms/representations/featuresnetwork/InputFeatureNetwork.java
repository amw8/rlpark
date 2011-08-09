package rltoys.algorithms.representations.featuresnetwork;

import java.util.ArrayList;
import java.util.List;

import rltoys.algorithms.representations.features.Identity;
import rltoys.math.vector.PVector;


public class InputFeatureNetwork extends AbstractFeatureNetwork {
  private static final long serialVersionUID = -5260899356819905490L;
  private final List<Identity> inputs = new ArrayList<Identity>();
  private PVector currentFeatureVector = null;

  public void setInputs(List<Identity> inputs) {
    this.inputs.addAll(inputs);
  }

  public void setInputAndUpdate(double... values) {
    assert inputs.size() == values.length;
    if (currentFeatureVector == null)
      currentFeatureVector = new PVector(features().size());
    for (int i = 0; i < values.length; i++)
      inputs.get(i).setValue(values[i]);
    updateFeatures();
    rltoys.algorithms.representations.features.Functions.set(features(), currentFeatureVector);
  }

  public Identity newInput() {
    Identity input = new Identity();
    inputs.add(input);
    return input;
  }
}
