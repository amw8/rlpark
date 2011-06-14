package rlpark.plugin.robot;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rlpark.plugin.robot.disco.datagroup.DiscoSocketTest;
import rlpark.plugin.robot.disco.datagroup.DropTest;
import rlpark.plugin.robot.statemachine.StateMachineTest;


@RunWith(Suite.class)
@Suite.SuiteClasses({ DropTest.class, DiscoSocketTest.class, StateMachineTest.class, DiscoLogfileTest.class })
public class RobotTests {
}