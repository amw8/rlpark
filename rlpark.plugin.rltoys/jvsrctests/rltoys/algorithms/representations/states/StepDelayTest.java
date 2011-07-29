package rltoys.algorithms.representations.states;

import org.junit.Assert;
import org.junit.Test;

import rltoys.algorithms.representations.features.Identity;
import rltoys.algorithms.representations.features.StepDelay;
import rltoys.algorithms.representations.featuresnetwork.AgentStateNetwork;


public class StepDelayTest {
  @Test
  public void testStepDelay() {
    Identity identity = new Identity();
    StepDelay stepDelay = new StepDelay(identity);
    AgentStateNetwork agentState = new AgentStateNetwork(stepDelay);
    identity.setValue(4.0);
    agentState.update();
    identity.setValue(5.0);
    agentState.update();
    Assert.assertEquals(4.0, stepDelay.value(), 0.0);
  }
}
