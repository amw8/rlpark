package rltoys.demons;

import java.io.Serializable;

public interface RewardFunction extends Serializable {
  double reward();
}
