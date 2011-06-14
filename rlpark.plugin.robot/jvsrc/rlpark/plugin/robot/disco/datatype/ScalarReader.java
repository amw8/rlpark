package rlpark.plugin.robot.disco.datatype;

import java.nio.ByteBuffer;

public interface ScalarReader {
  int getInt(ByteBuffer buffer);

  double getDouble(ByteBuffer buffer);
}
