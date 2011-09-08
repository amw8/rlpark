package rltoys.experiments.parametersweep;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.interfaces.Context;
import rltoys.experiments.parametersweep.interfaces.JobWithParameters;
import rltoys.experiments.parametersweep.interfaces.SweepDescriptor;
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

public class SweepAll {
  static private boolean verbose = true;
  private final Scheduler scheduler;
  int nbJobs;


  public SweepAll() {
    this(new LocalScheduler());
  }

  public SweepAll(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  private void createAndSubmitRequiredJobs(SweepDescriptor sweepDescriptor, ExperimentCounter counter, Context context,
      ParametersLogFile logFile) {
    List<Parameters> allParameters = sweepDescriptor.provideParameters(context);
    String[] parameterLabels = allParameters.get(0).labels();
    Set<FrozenParameters> doneParameters = logFile.extractParameters(parameterLabels);
    List<Runnable> todoJobList = new ArrayList<Runnable>();
    for (Parameters parameters : allParameters) {
      if (!doneParameters.contains(parameters.froze()))
        todoJobList.add(context.createJob(parameters, counter));
    }
    if (todoJobList.size() > 0)
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

  public void runSweep(SweepDescriptor sweepDescriptor, ExperimentCounter counter) {
    submitSweep(sweepDescriptor, counter);
    runAll();
  }

  public void runAll() {
    scheduler.runAll();
  }

  public void submitSweep(SweepDescriptor sweepDescriptor, ExperimentCounter counter) {
    while (counter.hasNext()) {
      counter.nextExperiment();
      submitOneSweep(sweepDescriptor, counter);
    }
  }

  private void submitOneSweep(SweepDescriptor sweepDescriptor, ExperimentCounter counter) {
    List<? extends Context> contexts = sweepDescriptor.provideContexts();
    for (Context context : contexts) {
      String filename = counter.folderFilename(context.folderPath(), context.fileName());
      ParametersLogFile logFile = new ParametersLogFile(filename);
      createAndSubmitRequiredJobs(sweepDescriptor, counter, context, logFile);
    }
  }

  public int nbJobs() {
    return nbJobs;
  }

  static public void disableVerbose() {
    verbose = false;
  }

  public Scheduler scheduler() {
    return scheduler;
  }
}
