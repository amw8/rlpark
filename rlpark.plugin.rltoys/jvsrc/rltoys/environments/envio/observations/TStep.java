package rltoys.environments.envio.observations;

import java.util.Arrays;

import rltoys.algorithms.representations.actions.Action;

public class TStep {
  // Time of o_tp1
  final public long time;
  final public double[] o_t;
  final public Action a_t;
  final public double[] o_tp1;

  TStep(long time, double[] o_t, Action a_t, double[] o_tp1) {
    this.time = time;
    this.o_t = o_t == null ? null : o_t.clone();
    this.a_t = a_t;
    this.o_tp1 = o_tp1 == null ? null : o_tp1.clone();
    assert a_t != null || o_t == null;
    assert o_t != null || o_tp1 != null;
  }

  @Override
  public String toString() {
    return String.format("T=%d: %s,%s->%s", time, Arrays.toString(o_t), a_t, Arrays.toString(o_tp1));
  }

  public boolean isEpisodeStarting() {
    return o_t == null && o_tp1 != null;
  }


  public boolean isEpisodeEnding() {
    return o_t != null && o_tp1 == null;
  }
}
