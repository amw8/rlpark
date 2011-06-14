package rlpark.plugin.robot.ranges;

import java.util.HashMap;
import java.util.Map;

import rlpark.plugin.robot.disco.datagroup.DropScalarGroup;
import rlpark.plugin.robot.disco.datatype.Ranged;
import rlpark.plugin.robot.disco.drops.DropData;
import rltoys.environments.envio.observations.Legend;
import rltoys.math.ranges.Range;

public class RangeProvider {
  private final Map<String, Range> labelToRanges = new HashMap<String, Range>();

  public RangeProvider(DropScalarGroup datas, Map<String, Range> missing) {
    for (DropData drop : datas.drop().dropDatas())
      if (drop instanceof Ranged)
        labelToRanges.put(drop.label, ((Ranged) drop).range());
    if (missing != null)
      labelToRanges.putAll(missing);
  }

  public Range[] ranges(Legend legend) {
    Range[] ranges = new Range[legend.nbLabels()];
    for (int i = 0; i < ranges.length; i++) {
      String label = legend.label(i);
      Range range = labelToRanges.get(label);
      assert range != null;
      ranges[i] = range;
    }
    return ranges;
  }
}
