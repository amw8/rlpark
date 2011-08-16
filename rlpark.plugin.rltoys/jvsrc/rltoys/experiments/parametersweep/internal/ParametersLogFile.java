package rltoys.experiments.parametersweep.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.utils.Utils;
import zephyr.plugin.core.api.monitoring.fileloggers.LoggerRow;
import zephyr.plugin.core.api.monitoring.helpers.Loggers;

public class ParametersLogFile {
  public final String filepath;

  public ParametersLogFile(String filepath) {
    this.filepath = filepath;
  }

  private String[] readLabels(BufferedReader reader) throws IOException {
    String line = reader.readLine();
    if (line == null)
      return null;
    while (line.startsWith("#") || line.startsWith(" "))
      line = line.substring(1);
    return line.split(" ");
  }

  private FrozenParameters readParameter(Set<String> parameterLabels, String[] labels, BufferedReader reader)
      throws IOException {
    String read = reader.readLine();
    if (read == null)
      return null;
    Map<String, Double> parameterMap = new LinkedHashMap<String, Double>();
    Map<String, Double> resultMap = new LinkedHashMap<String, Double>();
    String[] values = read.split(" ");
    for (int i = 0; i < values.length; i++)
      if (parameterLabels.contains(labels[i]))
        parameterMap.put(labels[i], Double.parseDouble(values[i]));
      else
        resultMap.put(labels[i], Double.parseDouble(values[i]));
    return new FrozenParameters(parameterMap, resultMap);
  }

  public Set<FrozenParameters> extractParameters(String... parameterLabelsArray) {
    Set<String> parameterLabels = Utils.asSet(parameterLabelsArray);
    Set<FrozenParameters> result = new LinkedHashSet<FrozenParameters>();
    if (!canRead())
      return result;
    FileInputStream in = null;
    try {
      in = new FileInputStream(filepath);
      InputStreamReader inReader = new InputStreamReader(in);
      BufferedReader reader = new BufferedReader(inReader);
      String[] labels = readLabels(reader);
      if (labels == null) {
        in.close();
        return result;
      }
      while (reader.ready())
        result.add(readParameter(parameterLabels, labels, reader));
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
      if (in != null)
        try {
          in.close();
        } catch (IOException e1) {
        }
    }
    return result;
  }

  private boolean canRead() {
    return new File(filepath).canRead();
  }

  public void writeParameters(Set<FrozenParameters> unsortedResultingParameters) throws IOException {
    List<FrozenParameters> resultingParameters = new ArrayList<FrozenParameters>(unsortedResultingParameters);
    Collections.sort(resultingParameters);
    LoggerRow loggerRow = new LoggerRow(filepath);
    loggerRow.writeLegend(resultingParameters.get(0).labels());
    for (FrozenParameters parameters : resultingParameters)
      loggerRow.writeRow(parameters.values());
    loggerRow.close();
  }

  public void appendParameters(Parameters mutableParameters) {
    FrozenParameters parameters = mutableParameters.froze();
    boolean canRead = canRead();
    try {
      Loggers.checkParentFolder(this.filepath);
      LoggerRow loggerRow = new LoggerRow(new PrintWriter(new FileOutputStream(filepath, true), true));
      if (!canRead)
        loggerRow.writeLegend(parameters.labels());
      loggerRow.writeRow(parameters.values());
      loggerRow.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
