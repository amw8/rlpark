package rltoys.demons;

import java.io.Serializable;

public interface OutcomeFunction extends Serializable {
  public class DefaultOutcomeFunction implements OutcomeFunction {
    private static final long serialVersionUID = -795158506392207870L;

    @Override
    public double outcome() {
      return 0.0;
    }
  }

  double outcome();
}
