package rltoys.horde;

import java.util.ArrayList;
import java.util.List;

import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.observations.Observation;
import rltoys.horde.demons.Demon;
import rltoys.horde.demons.DemonScheduler;
import rltoys.horde.functions.GammaFunction;
import rltoys.horde.functions.HordeUpdatable;
import rltoys.horde.functions.OutcomeFunction;
import rltoys.horde.functions.RewardFunction;
import rltoys.math.vector.RealVector;

public class Horde {
  final private List<HordeUpdatable> functions = new ArrayList<HordeUpdatable>();
  final private List<Demon> demons = new ArrayList<Demon>();
  private final DemonScheduler demonScheduler;

  public Horde() {
    this(new DemonScheduler());
  }

  public Horde(DemonScheduler demonScheduler) {
    this.demonScheduler = demonScheduler;
  }

  public Horde(List<Demon> demons, List<RewardFunction> rewardFunctions, List<OutcomeFunction> outcomeFunctions,
      List<GammaFunction> gammaFunctions) {
    this();
    this.demons.addAll(demons);
    addFunctions(rewardFunctions);
    addFunctions(outcomeFunctions);
    addFunctions(gammaFunctions);
  }

  private void addFunctions(List<?> functions) {
    if (functions == null)
      return;
    for (Object function : functions)
      this.functions.add((HordeUpdatable) function);
  }

  public void update(Observation o_tp1, RealVector x_t, Action a_t, RealVector x_tp1) {
    for (HordeUpdatable function : functions)
      function.update(o_tp1, x_t, a_t, x_tp1);
    demonScheduler.update(demons, x_t, a_t, x_tp1);
  }

  public List<HordeUpdatable> functions() {
    return functions;
  }

  public List<Demon> demons() {
    return demons;
  }
}
