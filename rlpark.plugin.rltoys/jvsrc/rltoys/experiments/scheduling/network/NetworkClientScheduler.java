package rltoys.experiments.scheduling.network;

import java.io.IOException;
import java.net.UnknownHostException;

import rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rltoys.experiments.scheduling.interfaces.JobQueue;
import rltoys.experiments.scheduling.network.internal.NetworkJobQueue;
import rltoys.experiments.scheduling.schedulers.LocalScheduler;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Chrono;

public class NetworkClientScheduler {
  private static final int SleepWhenPersitentSeconds = 600;
  static private boolean persistentClient = false;
  static private double maximumMinutesTime = -1;
  static private String serverHost = "";
  static private int serverPort = ServerScheduler.DefaultPort;
  static private int nbCore = LocalScheduler.getDefaultNbThreads();

  private final LocalScheduler localScheduler;
  final protected NetworkJobQueue networkJobQueue;

  public NetworkClientScheduler(String serverHost, int port) throws UnknownHostException, IOException {
    this(new LocalScheduler(createJobQueue(serverHost, port)));
  }

  public NetworkClientScheduler(int nbThread, String serverHost, int port) throws UnknownHostException, IOException {
    this(new LocalScheduler(nbThread, createJobQueue(serverHost, port)));
  }

  public NetworkClientScheduler(final LocalScheduler localScheduler) {
    this.localScheduler = localScheduler;
    networkJobQueue = (NetworkJobQueue) localScheduler.queue();
    networkJobQueue.onJobReceived.connect(new Listener<JobQueue>() {
      @Override
      public void listen(JobQueue eventInfo) {
        localScheduler.start();
      }
    });
  }

  private static NetworkJobQueue createJobQueue(String serverHost, int port) throws UnknownHostException, IOException {
    return new NetworkJobQueue(serverHost, port);
  }

  public void start() {
    localScheduler.start();
  }

  private void setMaximumTime(final double wallTime) {
    networkJobQueue.onJobDone().connect(new Listener<JobDoneEvent>() {
      final Chrono chrono = new Chrono();

      @Override
      public void listen(JobDoneEvent event) {
        if (chrono.getCurrentChrono() > wallTime)
          networkJobQueue.denyNewJobRequest();
      }
    });
  }

  public void run() {
    while (networkJobQueue.canAnswerJobRequest()) {
      localScheduler.runAll();
    }
  }

  public void dispose() {
    localScheduler.dispose();
    networkJobQueue.close();
  }

  private static void readParams(String[] args) {
    for (String arg : args)
      if (arg.startsWith("-"))
        readOption(arg);
      else
        readServerInfo(arg);
  }

  private static void readOption(String arg) {
    switch (arg.charAt(1)) {
    case 't':
      maximumMinutesTime = Double.parseDouble(arg.substring(2));
      break;
    case 'p':
      persistentClient = Boolean.parseBoolean(arg.substring(2));
      break;
    case 'c':
      nbCore = Integer.parseInt(arg.substring(2));
      break;
    default:
      System.err.println("Unknown option: " + arg);
    }
  }

  private static void readServerInfo(String arg) {
    int portSeparator = arg.lastIndexOf(":");
    serverHost = portSeparator >= 0 ? arg.substring(0, portSeparator) : arg;
    if (portSeparator >= 0)
      serverPort = Integer.parseInt(arg.substring(portSeparator));
  }

  private static void sleep() {
    try {
      Thread.sleep(SleepWhenPersitentSeconds * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static void startScheduler() throws UnknownHostException, IOException {
    NetworkClientScheduler scheduler = new NetworkClientScheduler(nbCore, serverHost, serverPort);
    if (maximumMinutesTime > 0)
      scheduler.setMaximumTime(maximumMinutesTime * 60);
    scheduler.run();
    scheduler.dispose();
  }

  private static void printParams() {
    System.out.println("persistentClient: " + String.valueOf(persistentClient));
    System.out.println("maximumMinutesTime: " + String.valueOf(maximumMinutesTime));
    System.out.println("nbCore: " + String.valueOf(nbCore));
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      System.err
          .println("Usage: java -jar <jarfile.jar> -t<max time: 30,60,... mins> -p<persistent: 0 or 1> -c<nb cores> <hostname:port>");
      System.exit(1);
    }
    readParams(args);
    printParams();
    do {
      try {
        startScheduler();
      } catch (Exception e) {
        if (!persistentClient)
          e.printStackTrace();
      }
      if (maximumMinutesTime > 0)
        break;
      if (persistentClient)
        sleep();
    } while (persistentClient);
  }
}
