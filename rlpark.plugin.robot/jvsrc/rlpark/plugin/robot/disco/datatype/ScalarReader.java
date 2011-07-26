package rlpark.plugin.robot.disco.datatype;


public interface ScalarReader {
  int getInt(LightByteBuffer buffer);

  double getDouble(LightByteBuffer buffer);
}
