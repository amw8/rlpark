package rlpark.plugin.eclipse;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ClasspathVariableInitializer;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.osgi.framework.Bundle;

public class CritterbotClasspathVariableInitializer extends ClasspathVariableInitializer {
  static final private String CRITTERBOT_HOME = "CRITTERBOT_HOME";
  static final private String CRITTERBOTSIMULATOR_HOME = "CRITTERBOTSIMULATOR_HOME";

  @Override
  public void initialize(String variable) {
    Bundle bundle = Platform.getBundle("rlpark.plugin.critterbot");
    if (bundle == null) {
      JavaCore.removeClasspathVariable(variable, null);
      return;
    }
    final String jarPath = jarPath(variable);
    if (jarPath == null) {
      JavaCore.removeClasspathVariable(variable, null);
      return;
    }
    URL installLocation = bundle.getEntry(jarPath);
    URL local = null;
    try {
      local = FileLocator.toFileURL(installLocation);
    } catch (IOException e) {
      JavaCore.removeClasspathVariable(variable, null);
      return;
    }
    try {
      String fullPath = new File(local.getPath()).getAbsolutePath();
      JavaCore.setClasspathVariable(variable, Path.fromOSString(fullPath), null);
    } catch (JavaModelException e) {
      JavaCore.removeClasspathVariable(variable, null);
    }
  }

  private String jarPath(String variable) {
    if (variable.equals(CRITTERBOT_HOME))
      return "/dist/critterbot.jar";
    else if (variable.equals(CRITTERBOTSIMULATOR_HOME))
      return "/libs/CritterbotSimulator.jar";
    return null;
  }
}
