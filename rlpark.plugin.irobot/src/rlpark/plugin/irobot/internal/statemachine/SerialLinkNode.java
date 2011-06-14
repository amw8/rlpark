package rlpark.plugin.irobot.internal.statemachine;

import rlpark.plugin.robot.statemachine.StateNode;

public interface SerialLinkNode extends StateNode<Byte> {
  int sum();
}
