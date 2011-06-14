package rlpark.plugin.robot.disco.datatype;

import java.nio.ByteBuffer;

public interface GrayCodeConverter {
  void convert(ByteBuffer source, ByteBuffer target);
}
