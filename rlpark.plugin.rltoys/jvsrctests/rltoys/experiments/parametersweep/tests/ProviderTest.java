package rltoys.experiments.parametersweep.tests;


import java.io.Serializable;
import java.util.List;

import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.interfaces.Context;
import rltoys.experiments.parametersweep.interfaces.JobWithParameters;
import rltoys.experiments.parametersweep.interfaces.ParameterSweepProvider;
import rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.utils.Utils;

public class ProviderTest implements ParameterSweepProvider, Context {
  private static final String SweepDone = "sweepDone";
  public static final String ParameterName = "ID";
  public static final String ContextPath = "providertest";

  static public class SweepJob implements JobWithParameters {
    private static final long serialVersionUID = 4238136913870025414L;
    private final Parameters parameters;
    private final int counter;

    SweepJob(Parameters parameters, ExperimentCounter counter) {
      this.parameters = parameters;
      this.counter = counter.currentIndex();
    }

    @Override
    public void run() {
      parameters.putResult(SweepDone, 1);
      parameters.putResult("counter", counter);
    }

    @Override
    public Parameters parameters() {
      return parameters;
    }
  }

  static public class LearningCurveJob implements Runnable, Serializable {
    private static final long serialVersionUID = 6213584778601355711L;
    private final Parameters parameters;
    private final ExperimentCounter counter;

    LearningCurveJob(Parameters parameters, ExperimentCounter counter) {
      this.parameters = parameters;
      this.counter = counter;
    }

    @Override
    public void run() {
      parameters.putResult(SweepDone, 1);
      parameters.putResult("counter", counter.currentIndex());
    }

    public Parameters parameters() {
      return parameters;
    }
  }

  private static final long serialVersionUID = 7141220137708536488L;
  private final int nbParameters;

  public ProviderTest(int nbParameters) {
    this.nbParameters = nbParameters;
  }

  @Override
  public List<? extends Context> provideContexts() {
    return Utils.asList((Context) this);
  }

  @Override
  public List<Parameters> provideParameters(Context context) {
    double[] result = new double[nbParameters];
    for (int i = 0; i < result.length; i++)
      result[i] = i;
    return Parameters.combine(null, ParameterName, result);
  }

  @Override
  public String folderPath() {
    return ContextPath;
  }

  @Override
  public String fileName() {
    return ExperimentCounter.DefaultFileName;
  }

  @Override
  public Runnable createLearningCurveJob(Parameters parameters, ExperimentCounter counter) {
    return new LearningCurveJob(parameters, counter);
  }

  @Override
  public SweepJob createSweepJob(final Parameters parameters, final ExperimentCounter counter) {
    return new SweepJob(parameters, counter);
  }

  public static boolean parametersHasBeenDone(FrozenParameters parameters) {
    return parameters.get(SweepDone) == 1;
  }
}