package rltoys.environments.envio.observations;

import java.util.Arrays;

import rltoys.algorithms.representations.actions.Action;

public class TRStep extends TStep {
  final public double r_tp1;

  public TRStep(TStep tobs, double reward) {
    this(tobs.time, tobs.o_t, tobs.a_t, tobs.o_tp1, reward);
  }

  public TRStep(double[] o_tp1, double reward) {
    this(0, null, null, o_tp1, reward);
  }

  public TRStep(long time, double[] o_t, Action a_t, double r_tp1) {
    this(time, o_t, a_t, null, r_tp1);
  }

  public TRStep(TRStep step_t, Action a_t, double[] o_tp1, double r_tp1) {
    this(step_t == null ? 0 : step_t.time + 1, step_t == null ? null : step_t.o_tp1, a_t, o_tp1, r_tp1);
  }

  public TRStep(long timeStep, double[] o_t, Action a_t, double[] o_tp1, double r_tp1) {
    super(timeStep, o_t, a_t, o_tp1);
    this.r_tp1 = r_tp1;
  }

  public TRStep createEndingStep() {
    return new TRStep(time, o_t, a_t, null, r_tp1);
  }

  @Override
  public String toString() {
    return String.format("T=%d: %s,%s->%s,r=%f", time, Arrays.toString(o_t), a_t, Arrays.toString(o_tp1), r_tp1);
  }
}
