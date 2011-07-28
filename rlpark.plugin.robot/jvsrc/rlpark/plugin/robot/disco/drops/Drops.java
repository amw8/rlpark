package rlpark.plugin.robot.disco.drops;

import java.util.ArrayList;
import java.util.List;

import rlpark.plugin.robot.disco.datatype.GrayCodeConverter;
import rlpark.plugin.robot.disco.datatype.LiteByteBuffer;

public class Drops {
  public static List<DropData> flatten(Drop drop) {
    List<DropData> flatten = new ArrayList<DropData>();
    for (DropData dropData : drop.dropDatas())
      if (dropData instanceof DropArray)
        flatten((DropArray) dropData, flatten);
      else
        flatten.add(dropData);
    return flatten;
  }

  private static void flatten(DropArray dropArray, List<DropData> flatten) {
    for (DropData dropData : dropArray.dropDatas())
      if (dropData instanceof DropArray)
        flatten((DropArray) dropData, flatten);
      else
        flatten.add(dropData);
  }

  public static byte[] toGrayCode(LiteByteBuffer buffer, List<DropData> dropDatas) {
    LiteByteBuffer result = new LiteByteBuffer(buffer.capacity(), buffer.order());
    for (DropData dropData : dropDatas) {
      if (dropData instanceof GrayCodeConverter) {
        ((GrayCodeConverter) dropData).convert(buffer, result);
        continue;
      }
      for (int i = 0; i < dropData.size(); i++)
        result.put(buffer.get());
    }
    return result.array();
  }
}
