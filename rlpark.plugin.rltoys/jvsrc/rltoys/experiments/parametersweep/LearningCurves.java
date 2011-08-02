package rltoys.experiments.parametersweep;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.interfaces.Context;
import rltoys.experiments.parametersweep.interfaces.ContextProvider;
import rltoys.experiments.parametersweep.interfaces.ParameterSweepProvider;
import rltoys.experiments.parametersweep.interfaces.ParametersProvider;
import rltoys.experiments.parametersweep.onpolicy.ContextOnPolicyEvaluation;
import rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.scheduling.interfaces.Scheduler;
import rltoys.experiments.scheduling.schedulers.LocalScheduler;

public class LearningCurves {
  private final ParametersProvider parametersProvider;
  private final ContextProvider contextProvider;
  private final ExperimentCounter counter;
  private final Scheduler scheduler = new LocalScheduler();
  private final Set<FrozenParameters> todoParameters;


  public LearningCurves(Set<FrozenParameters> todoParameters, ParameterSweepProvider provider,
      ExperimentCounter counter) {
    this(todoParameters, provider, provider, counter);
  }

  public LearningCurves(Set<FrozenParameters> todoParameters, ContextProvider contextProvider,
      ParametersProvider parametersProvider, ExperimentCounter counter) {
    this.contextProvider = contextProvider;
    this.parametersProvider = parametersProvider;
    this.counter = counter;
    this.todoParameters = todoParameters;
  }

  private List<Parameters> createJobsDescription(Context context) {
    Set<FrozenParameters> contextTodoParameters = selectConsistentParameters(context);
    ArrayList<Parameters> result = new ArrayList<Parameters>();
    if (contextTodoParameters.isEmpty())
      return result;
    List<Parameters> allParameters = parametersProvider.provideParameters(context);
    for (Parameters parameters : allParameters) {
      if (contextTodoParameters.contains(parameters.froze()))
        result.add(parameters);
    }
    return result;
  }

  private Set<FrozenParameters> selectConsistentParameters(Context context) {
    ContextOnPolicyEvaluation onPolicyContext = (ContextOnPolicyEvaluation) context;
    String algorithmLabel = onPolicyContext.agentFactory().label();
    String problemLabel = onPolicyContext.problemFactory().label();
    Set<FrozenParameters> selected = new LinkedHashSet<FrozenParameters>();
    for (FrozenParameters parameters : todoParameters)
      if (parameters.hasFlag(problemLabel) && parameters.hasFlag(algorithmLabel)) {
        Parameters parametersCompleted = new Parameters(parameters);
        onPolicyContext.problemFactory().setExperimentParameters(parametersCompleted);
        selected.add(parametersCompleted.froze());
      }
    return selected;
  }

  public void generateLearningCurve() {
    System.out.println("Preparing job descriptions...");
    Map<Context, List<Parameters>> descriptions = new LinkedHashMap<Context, List<Parameters>>();
    for (Context context : contextProvider.provideContexts())
      descriptions.put(context, createJobsDescription(context));
    while (counter.hasNext()) {
      counter.nextExperiment();
      for (Map.Entry<Context, List<Parameters>> entry : descriptions.entrySet())
        for (Parameters parameters : entry.getValue())
          scheduler.add(entry.getKey().createLearningCurveJob(parameters, counter), null);
      scheduler.runAll();
    }
  }

  public static Set<FrozenParameters> toParametersSet(String[] args) {
    Set<FrozenParameters> results = new LinkedHashSet<FrozenParameters>();
    for (String arg : args) {
      String[] components = arg.split("_");
      Parameters parameters = new Parameters();
      parameters.enableFlag(components[0]);
      parameters.enableFlag(components[1]);
      for (int i = 1; i < components.length / 2; i++)
        parameters.put(components[i * 2], Double.parseDouble(components[i * 2 + 1]));
      results.add(parameters.froze());
    }
    return results;
  }
}
