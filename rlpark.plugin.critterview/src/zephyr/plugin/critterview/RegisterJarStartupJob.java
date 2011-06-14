package zephyr.plugin.critterview;

import java.io.File;

import zephyr.plugin.core.startup.StartupJob;
import zephyr.plugin.core.utils.Helper;
import critterbot.environment.CritterbotSimulator;

public class RegisterJarStartupJob implements StartupJob {
  @Override
  public int level() {
    return 100;
  }

  @Override
  public void run() {
    String path = Helper.getPluginLocation(CritterviewPlugin.getDefault().getBundle(),
                                           "./libs/CritterbotSimulator.jar");
    CritterbotSimulator.setJarPath(new File(path).getAbsolutePath());
  }
}
