package rlpark.plugin.rltoysview.internal;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class RLToysViewPlugin extends AbstractUIPlugin {
  static private RLToysViewPlugin plugin;

  public RLToysViewPlugin() {
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  static public RLToysViewPlugin getDefault() {
    return plugin;
  }
}
