package rltoys.algorithms.representations.featuresnetwork;

import java.util.ArrayList;
import java.util.List;

import rltoys.algorithms.representations.features.Identity;
import rltoys.math.vector.CachedVector;
import rltoys.math.vector.PVector;


public class InputFeatureNetwork extends AbstractFeatureNetwork {
  private static final long serialVersionUID = -5260899356819905490L;
  private final List<Identity> inputs = new ArrayList<Identity>();
  private final CachedVector currentFeatureVector = new CachedVector();

  public void setInputs(List<Identity> inputs) {
    this.inputs.addAll(inputs);
  }

  public void setInputAndUpdate(double... values) {
    assert inputs.size() == values.length;
    for (int i = 0; i < values.length; i++)
      inputs.get(i).setValue(values[i]);
    updateFeatures();
    currentFeatureVector.set(features());
  }

  public Identity newInput() {
    Identity input = new Identity();
    inputs.add(input);
    return input;
  }

  public PVector currentFeatureVector() {
    return currentFeatureVector.values();
  }

}
