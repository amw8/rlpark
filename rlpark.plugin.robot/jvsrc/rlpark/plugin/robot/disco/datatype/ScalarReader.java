package rlpark.plugin.robot.disco.datatype;


public interface ScalarReader extends Ranged {
  int getInt(LiteByteBuffer buffer);

  double getDouble(LiteByteBuffer buffer);
}
