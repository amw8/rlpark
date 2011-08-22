package rltoys.experiments.parametersweep;

import java.io.File;
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
import rltoys.experiments.scheduling.interfaces.JobPool;
import rltoys.experiments.scheduling.interfaces.JobPool.JobPoolListener;
import rltoys.experiments.scheduling.interfaces.Scheduler;
import rltoys.experiments.scheduling.pools.FileJobPool;
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
    List<Parameters> allParameters = parametersProvider.provideParameters(context);
    String[] parameterLabels = allParameters.get(0).labels();
    Set<FrozenParameters> doneParameters = logFile.extractParameters(parameterLabels);
    List<Runnable> todoJobList = new ArrayList<Runnable>();
    for (Parameters parameters : allParameters) {
      if (!doneParameters.contains(parameters.froze()))
        todoJobList.add(context.createSweepJob(parameters, counter));
    }
    println(String.format("Submitting %d/%d jobs for %s...", todoJobList.size(), allParameters.size(),
                          extractName(logFile)));
    submitRequiredJob(logFile, parameterLabels, todoJobList);
  }

  private void submitRequiredJob(ParametersLogFile logFile, String[] parameterLabels, List<Runnable> todoJobList) {
    Listener<JobDoneEvent> jobListener = createJobListener(logFile);
    JobPoolListener poolListener = createPoolListener(logFile, parameterLabels);
    JobPool pool = new FileJobPool(extractName(logFile), poolListener, jobListener);
    for (Runnable job : todoJobList)
      pool.add(job);
    pool.submitTo(scheduler);
  }

  private String extractName(ParametersLogFile logFile) {
    File file = new File(logFile.filepath);
    File algoNameParentFile = file.getParentFile();
    File problemNameParentFile = algoNameParentFile.getParentFile();
    return String.format("%s/%s/%s", problemNameParentFile.getName(), algoNameParentFile.getName(), file.getName());
  }

  private JobPoolListener createPoolListener(final ParametersLogFile logFile, final String[] parameterLabels) {
    return new JobPoolListener() {
      @Override
      public void listen(JobPool eventInfo) {
        logFile.reorganizeLogFile(parameterLabels);
      }
    };
  }

  private Listener<JobDoneEvent> createJobListener(final ParametersLogFile logFile) {
    return new Listener<JobDoneEvent>() {
      @Override
      public void listen(JobDoneEvent eventInfo) {
        Parameters doneParameters = ((JobWithParameters) eventInfo.done).parameters();
        logFile.appendParameters(doneParameters);
        nbJobs++;
      }
    };
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
