package critterbot.crtrlog;

import java.io.File;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import rltoys.algorithms.representations.features.envio.ObservationFeature;
import rltoys.algorithms.representations.featuresnetwork.ObservationAgentState;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.TStep;
import rltoys.math.ranges.Range;
import rltoys.math.vector.implementations.PVector;
import rltoys.math.vector.testing.VectorsTestsUtils;
import rltoys.utils.Paths;
import rltoys.utils.Utils;

public class CrtrLogFileTest {

  static final double[][] inputExpected01 = { { 22000.0, 1.0, 2.0, 3.0, 4.0, 5.0 },
      { 22010.0, 2.0, 2.0, 3.0, 4.0, 5.0 }, { 22020.0, 1.0, 2.0, 3.0, 4.0, 5.0 }, { 22030.0, 2.0, 2.0, 3.0, 4.0, 5.0 },
      { 22040.0, 3.0, 2.0, 3.0, 4.0, 5.0 } };

  @Test
  public void testRanges() {
    Range[] ranges = LogFiles.extractRanges(Paths.getDataPath("rlpark.plugin.critterbot", "unittesting01.crtrlog"),
                                            false);
    Range[] expectedRanges = new Range[] { new Range(22000, 222040), new Range(1, 3), new Range(2, 2), new Range(3, 3),
        new Range(4, 4), new Range(5, 5) };
    Assert.assertEquals(expectedRanges.length, ranges.length);
    for (int i = 0; i < expectedRanges.length; i++)
      ranges[i].equals(expectedRanges[i]);
  }

  @Test
  public void testLogFileToFeatures() {
    CrtrLogFile logFile = new CrtrLogFile(Paths.getDataPath("rlpark.plugin.critterbot", "unittesting01.crtrlog"));
    ObservationFeature[] inputFeatures = createObservationFeatures(logFile.legend());
    compareWithExpected(logFile, inputFeatures, inputExpected01);
    Assert.assertEquals(inputExpected01.length, logFile.nbSteps());
  }

  static public void compareWithExpected(CrtrLogFile logFile, ObservationFeature[] testedFeatures, double[][] expected) {
    ObservationAgentState agentState = new ObservationAgentState(0, testedFeatures);
    agentState.addObservationsToState();
    int timeIndex = 0;
    while (logFile.hasNextStep()) {
      TStep step = logFile.step();
      agentState.update(step);
      PVector featureValues = agentState.currentState();
      if (expected[timeIndex] == null)
        Assert.assertNull(featureValues);
      else
        VectorsTestsUtils.assertEquals(new PVector(expected[timeIndex]), featureValues);
      timeIndex += 1;
    }
    Assert.assertEquals(timeIndex, expected.length);
    File tmpFile = Utils.createTempFile("junit");
    Utils.save(agentState, tmpFile);
    Utils.load(tmpFile);
  }

  static private ObservationFeature[] createObservationFeatures(Legend legend) {
    ObservationFeature[] features = new ObservationFeature[legend.nbLabels()];
    for (Map.Entry<String, Integer> entry : legend.legend().entrySet())
      features[entry.getValue()] = new ObservationFeature(entry.getKey(), entry.getValue());
    return features;
  }
}
