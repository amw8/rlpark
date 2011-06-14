package zephyr.plugin.critterview;

import java.io.IOException;
import java.util.List;

import zephyr.plugin.core.Utils;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.filehandling.IFileHandler;
import critterbot.crtrlog.CrtrLogFile;

public class FileHandler implements IFileHandler {

  @Override
  public List<String> extensions() {
    return Utils.asList("crtrlog.bz2", "crtrlog.zip", "crtrlog.gz", "crtrlog");
  }

  @Override
  public void handle(String filepath, String[] fileargs) throws IOException {
    handle(filepath);
  }

  static public void handle(String filepath) {
    CrtrLogFile logfile = CrtrLogFile.load(filepath);
    Zephyr.advertise(logfile.clock(), logfile);
    while (logfile.hasNextStep())
      logfile.step();
    logfile.close();
  }
}
