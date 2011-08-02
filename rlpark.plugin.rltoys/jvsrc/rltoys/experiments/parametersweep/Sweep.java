package rltoys.experiments.parametersweep;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.interfaces.Context;
import rltoys.experiments.parametersweep.interfaces.ContextProvider;
import rltoys.experiments.parametersweep.interfaces.JobWithParameters;
import rltoys.experiments.parametersweep.interfaces.ParameterSweepProvider;
import rltoys.experiments.parametersweep.interfaces.ParametersProvider;
import rltoys.experiments.parametersweep.internal.ParametersLogFile;
import rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.interfaces.Scheduler;
import rltoys.experiments.scheduling.schedulers.LocalScheduler;
import rltoys.experiments.scheduling.schedulers.Schedulers;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Chrono;

public class Sweep {
  static private boolean verbose = true;
  private final ParametersProvider parametersProvider;
  private final ContextProvider contextProvider;
  private final ExperimentCounter counter;
  private final Scheduler scheduler;
  private int nbJobs;


  public Sweep(ParameterSweepProvider provider, ExperimentCounter counter) {
    this(new LocalScheduler(), provider, counter);
  }

  public Sweep(Scheduler scheduler, ParameterSweepProvider provider, ExperimentCounter counter) {
    this(scheduler, provider, provider, counter);
  }

  public Sweep(Scheduler scheduler, ContextProvider contextProvider, ParametersProvider parametersProvider,
      ExperimentCounter counter) {
    this.contextProvider = contextProvider;
    this.parametersProvider = parametersProvider;
    this.counter = counter;
    this.scheduler = scheduler;
  }

  private Set<FrozenParameters> createAndRunRequiredJobs(Context context, ParametersLogFile logFile) {
    println(logFile.filepath);
    List<Parameters> allParameters = parametersProvider.provideParameters(context);
    Set<FrozenParameters> doneParameters = logFile.extractParameters(allParameters.get(0).labels());
    List<Runnable> todoJobList = new ArrayList<Runnable>();
    for (Parameters parameters : allParameters) {
      if (!doneParameters.contains(parameters.froze()))
        todoJobList.add(context.createSweepJob(parameters, counter));
    }
    print(String.format("Running %d/%d jobs for run %d...", todoJobList.size(),
                        allParameters.size(), counter.currentIndex()));
    Listener<JobDoneEvent> listener = createJobListener(logFile, doneParameters, todoJobList);
    runRequiredJob(listener, todoJobList);
    return doneParameters;
  }

  private void runRequiredJob(Listener<JobDoneEvent> listener, List<Runnable> todoJobList) {
    scheduler.queue().onJobDone().connect(listener);
    Schedulers.addAll(scheduler, todoJobList);
    Chrono chrono = new Chrono();
    nbJobs += scheduler.queue().nbJobs();
    scheduler.runAll();
    scheduler.queue().onJobDone().disconnect(listener);
    println(chrono.toString());
  }

  private Listener<JobDoneEvent> createJobListener(final ParametersLogFile logFile,
      final Set<FrozenParameters> doneParametersSet, final List<Runnable> todoJobList) {
    return new Listener<JobDoneEvent>() {
      @Override
      public void listen(JobDoneEvent eventInfo) {
        if (!todoJobList.contains(eventInfo.todo))
          return;
        Parameters doneParameters = ((JobWithParameters) eventInfo.done).parameters();
        doneParametersSet.add(doneParameters.froze());
        logFile.appendParameters(doneParameters);
      }
    };
  }

  private void print(String message) {
    if (!verbose)
      return;
    System.out.print(message);
    System.out.flush();
  }

  private void println(String message) {
    if (!verbose)
      return;
    System.out.println(message);
  }

  public void runSweep() throws IOException {
    while (counter.hasNext()) {
      counter.nextExperiment();
      runOneSweep();
    }
  }

  private void runOneSweep() throws IOException {
    List<Context> contexts = contextProvider.provideContexts();
    for (Context context : contexts) {
      String filename = counter.folderFilename(context.folderPath(), context.fileName());
      ParametersLogFile logFile = new ParametersLogFile(filename);
      Set<FrozenParameters> resultingParameters = createAndRunRequiredJobs(context, logFile);
      logFile.writeParameters(resultingParameters);
    }
  }

  public ContextProvider contextProvider() {
    return contextProvider;
  }

  public int nbJobs() {
    return nbJobs;
  }

  static public void disableVerbose() {
    verbose = false;
  }
}
