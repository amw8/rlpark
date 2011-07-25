package rltoys.utils;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

@Monitor
public class MemoryMonitor implements MonitorContainer {
  @Override
  public void addToMonitor(DataMonitor monitor) {
    monitor.add("Free Memory", 0, new Monitored() {
      @Override
      public double monitoredValue() {
        return Runtime.getRuntime().freeMemory();
      }
    });
    monitor.add("Total Memory", 0, new Monitored() {
      @Override
      public double monitoredValue() {
        return Runtime.getRuntime().totalMemory();
      }
    });
  }
}
