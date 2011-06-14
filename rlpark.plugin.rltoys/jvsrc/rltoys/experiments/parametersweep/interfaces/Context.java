package rltoys.experiments.parametersweep.interfaces;

import java.io.Serializable;

import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.parameters.Parameters;

public interface Context extends Serializable {
  String folderPath();

  String fileName();

  Runnable createSweepJob(Parameters parameters, ExperimentCounter counter);

  Runnable createLearningCurveJob(Parameters parameters, ExperimentCounter counter);
}
