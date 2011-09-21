package rlpark.plugin.robot;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rlpark.plugin.robot.disco.datagroup.DiscoSocketTest;
import rlpark.plugin.robot.disco.datagroup.DropTest;
import rlpark.plugin.robot.disco.datatype.LightByteBufferTest;
import rlpark.plugin.robot.statemachine.StateMachineTest;


@RunWith(Suite.class)
@Suite.SuiteClasses({ LightByteBufferTest.class, DropTest.class, DiscoSocketTest.class, StateMachineTest.class,
    DiscoLogfileTest.class, RobotsTest.class })
public class RobotTests {
}