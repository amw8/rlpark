package rltoys.horde;

import java.util.List;

import rltoys.horde.demons.Demon;
import rltoys.math.normalization.MovingMeanVarNormalizer;
import zephyr.plugin.core.api.monitoring.annotations.IgnoreMonitor;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class Surprise {
  private final MovingMeanVarNormalizer[] errorNormalizers;
  @IgnoreMonitor
  private final Demon[] demons;
  private double surpriseMeasure;

  public Surprise(List<Demon> demons, int trackingSpeed) {
    this.demons = new Demon[demons.size()];
    demons.toArray(this.demons);
    errorNormalizers = new MovingMeanVarNormalizer[demons.size()];
    for (int i = 0; i < errorNormalizers.length; i++)
      errorNormalizers[i] = new MovingMeanVarNormalizer(trackingSpeed);
  }

  public double updateSurpriseMeasure() {
    surpriseMeasure = 0;
    for (int i = 0; i < demons.length; i++) {
      double error = demons[i].learner().error();
      errorNormalizers[i].update(error);
      double scaledError = errorNormalizers[i].normalize(error);
      surpriseMeasure = Math.max(surpriseMeasure, Math.abs(scaledError));
    }
    return surpriseMeasure;
  }
}
