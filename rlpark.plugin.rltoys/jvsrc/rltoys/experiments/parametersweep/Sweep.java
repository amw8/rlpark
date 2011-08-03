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
import rltoys.experiments.scheduling.JobPool;
import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.interfaces.Scheduler;
import rltoys.experiments.scheduling.schedulers.LocalScheduler;
import zephyr.plugin.core.api.signals.Listener;

public class Sweep {
  static private boolean verbose = true;
  private final ParametersProvider parametersProvider;
  private final ContextProvider contextProvider;
  private final ExperimentCounter counter;
  private final Scheduler scheduler;
  int nbJobs;


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

  private void createAndSubmitRequiredJobs(Context context, ParametersLogFile logFile) {
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
    submitRequiredJob(logFile, doneParameters, todoJobList);
  }

  private void submitRequiredJob(ParametersLogFile logFile, Set<FrozenParameters> doneParameters,
      List<Runnable> todoJobList) {
    Listener<JobDoneEvent> jobListener = createJobListener(logFile, doneParameters);
    Listener<JobPool> poolListener = createPoolListener(logFile, doneParameters);
    JobPool pool = new JobPool(poolListener);
    for (Runnable job : todoJobList)
      pool.add(job, jobListener);
    pool.submitTo(scheduler);
  }

  private Listener<JobPool> createPoolListener(final ParametersLogFile logFile,
      final Set<FrozenParameters> doneParameters) {
    return new Listener<JobPool>() {
      @Override
      public void listen(JobPool eventInfo) {
        try {
          logFile.writeParameters(doneParameters);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
  }

  private Listener<JobDoneEvent> createJobListener(final ParametersLogFile logFile,
      final Set<FrozenParameters> doneParametersSet) {
    return new Listener<JobDoneEvent>() {
      @Override
      public void listen(JobDoneEvent eventInfo) {
        Parameters doneParameters = ((JobWithParameters) eventInfo.done).parameters();
        doneParametersSet.add(doneParameters.froze());
        logFile.appendParameters(doneParameters);
        nbJobs++;
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

  public void runSweep() {
    while (counter.hasNext()) {
      counter.nextExperiment();
      submitOneSweep();
    }
    System.out.println(scheduler.queue().nbJobs() + " to run in total.");
    scheduler.runAll();
  }

  private void submitOneSweep() {
    List<Context> contexts = contextProvider.provideContexts();
    for (Context context : contexts) {
      String filename = counter.folderFilename(context.folderPath(), context.fileName());
      ParametersLogFile logFile = new ParametersLogFile(filename);
      createAndSubmitRequiredJobs(context, logFile);
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
