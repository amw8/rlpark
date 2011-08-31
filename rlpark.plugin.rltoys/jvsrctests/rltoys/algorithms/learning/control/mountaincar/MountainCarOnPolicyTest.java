package rltoys.algorithms.learning.control.mountaincar;

import java.io.File;
import java.util.Random;

import rltoys.agents.AgentFA;
import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.representations.tilescoding.TileCoders;
import rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rltoys.environments.envio.Runner;
import rltoys.environments.mountaincar.MountainCar;
import rltoys.math.ranges.Range;
import rltoys.utils.Utils;


public abstract class MountainCarOnPolicyTest {
  protected interface MountainCarControlFactory {
    Control createControl(MountainCar mountainCar, TileCoders tilesCoder);
  };

  protected interface TileCodersFactory {
    TileCoders create(Range[] ranges);
  }

  private final TileCodersFactory defaultTileCodersFactory = new TileCodersFactory() {
    @Override
    public TileCoders create(Range[] ranges) {
      return new TileCodersNoHashing(ranges);
    }
  };

  public void runTestOnOnMountainCar(int maxTimeSteps, MountainCarControlFactory controlFactory) {
    runTestOnOnMountainCar(defaultTileCodersFactory, maxTimeSteps, controlFactory);
  }

  public void runTestOnOnMountainCar(TileCodersFactory tileCodersFactory, int maxTimeSteps,
      MountainCarControlFactory controlFactory) {
    MountainCar mountainCar = new MountainCar(new Random(0));
    final int nbEpisode = 20;
    final int maxNbTimeSteps = nbEpisode * maxTimeSteps;
    TileCoders tilesCoder = tileCodersFactory.create(mountainCar.getObservationRanges());
    tilesCoder.addFullTilings(9, 10);
    Control control = controlFactory.createControl(mountainCar, tilesCoder);
    AgentFA agent = new AgentFA(control, tilesCoder);
    Runner runner = new Runner(mountainCar, agent, nbEpisode, maxNbTimeSteps);
    runner.run();
    File tempFile = Utils.createTempFile("junit");
    Utils.save(agent, tempFile);
    Utils.load(tempFile);
  }
}