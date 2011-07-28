package rlpark.plugin.robot.disco.datatype;


public interface ScalarReader {
  int getInt(LiteByteBuffer buffer);

  double getDouble(LiteByteBuffer buffer);
}
