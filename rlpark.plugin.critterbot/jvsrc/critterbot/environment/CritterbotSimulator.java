package critterbot.environment;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.rlcommunity.critterbot.simulator.SimulatorMain;

import rltoys.environments.envio.observations.ObsFilter;
import rltoys.utils.Command;

public class CritterbotSimulator extends CritterbotEnvironment {
  static boolean simulatorInProcess = false;
  static boolean remoteDebugging = false;
  private static String jarPath = null;
  static final Integer SimulatorPort = findFreePort();
  static final String[] simulatorParameters = { "-p", SimulatorPort.toString(), "-nv" };
  static final String[] remoteDebugingArgs = { "-Xdebug",
      "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=1044" };
  protected Command command;

  public CritterbotSimulator() {
    super(new CritterbotConnection("localhost", SimulatorPort));
  }

  @Override
  protected void prepareEnvironment() {
    if (simulatorInProcess)
      startSimulatorInProcess();
    else
      startSimulatorOutProcess();
  }

  private static int findFreePort() {
    int port = 3284;
    ServerSocket socket;
    try {
      socket = new ServerSocket(0);
      port = socket.getLocalPort();
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Problem to find a free port: using port by default");
    }
    return port;
  }

  protected String[] buildSimulatorCommandLine() {
    String absJarPath = new File(jarPath).getAbsolutePath();
    List<String> commandLine = new ArrayList<String>();
    commandLine.add("java");
    if (remoteDebugging)
      for (String parameter : remoteDebugingArgs)
        commandLine.add(parameter);
    commandLine.add("-jar");
    commandLine.add(absJarPath);
    for (String parameter : simulatorParameters)
      commandLine.add(parameter);
    String[] result = new String[commandLine.size()];
    commandLine.toArray(result);
    return result;
  }

  private void startSimulatorOutProcess() {
    if (jarPath == null)
      setJarPath(getDefaultSimulatorJarPath());
    String[] commandLine = buildSimulatorCommandLine();
    System.out.println("Running: " + toCommandLineString(commandLine));
    command = new Command(getClass().getSimpleName(), commandLine);
    try {
      command.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String toCommandLineString(String[] commandLine) {
    StringBuilder result = new StringBuilder();
    for (String arg : commandLine) {
      result.append(arg);
      result.append(" ");
    }
    return result.substring(0, result.length() - 1);
  }

  private String getDefaultSimulatorJarPath() {
    String filePath = SimulatorMain.class.getResource(SimulatorMain.class.getSimpleName() + ".class").getFile();
    int classIndex = filePath.indexOf("/" + SimulatorMain.class.getCanonicalName().replace('.', '/'));
    String pathToFile = filePath.substring(filePath.indexOf('/'), classIndex);
    if (pathToFile.endsWith("!"))
      pathToFile = filePath.substring(0, filePath.length() - 1);
    return pathToFile;
  }

  private void startSimulatorInProcess() {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        SimulatorMain.main(new String[] { "-p", SimulatorPort.toString(), "-nk", "-nv" });
      }
    };
    Thread thread = new Thread(runnable);
    thread.start();
    thread.setPriority(Thread.MAX_PRIORITY);
  }

  public static void setJarPath(String jarPath) {
    CritterbotSimulator.jarPath = jarPath;
  }

  @Override
  public ObsFilter getDefaultFilter() {
    return new ObsFilter(legend(), CritterbotDrops.Motor, CritterbotDrops.RotationVel, CritterbotDrops.IRDistance,
                         CritterbotDrops.Light, CritterbotDrops.Accel);
  }

  @Override
  public void close() {
    super.close();
    if (command != null) {
      command.kill();
      command = null;
    }
  }
}
