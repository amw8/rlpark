package rltoys.math;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rltoys.math.history.HistoryTest;
import rltoys.math.normalization.MinMaxNormalizerTest;
import rltoys.math.normalization.MovingMeanVarNormalizerTest;
import rltoys.math.normalization.NormalizerTest;
import rltoys.math.ranges.RangeTest;
import rltoys.math.vector.ArrayToBinaryVectorTest;
import rltoys.math.vector.BVectorTest;
import rltoys.math.vector.PVectorTest;
import rltoys.math.vector.SVectorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ BVectorTest.class, PVectorTest.class, SVectorTest.class, RangeTest.class,
    HistoryTest.class, NormalizerTest.class, MinMaxNormalizerTest.class, MovingMeanVarNormalizerTest.class,
    ArrayToBinaryVectorTest.class, GrayCodeTest.class })
public class Tests {
}