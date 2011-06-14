package zephyr.plugin.critterview;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class CritterviewPlugin extends AbstractUIPlugin {
  static private CritterviewPlugin plugin;

  public CritterviewPlugin() {
    super();
  }

  @Override
  public void start(BundleContext context) throws Exception {
    plugin = this;
    super.start(context);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  public static CritterviewPlugin getDefault() {
    return plugin;
  }
}
