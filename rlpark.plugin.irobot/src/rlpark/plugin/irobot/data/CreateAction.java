package rlpark.plugin.irobot.data;

import rltoys.environments.envio.actions.ActionArray;

public class CreateAction extends ActionArray {
  private static final long serialVersionUID = -3988828504637894677L;
  public static final int DefaultVelocity = 200;
  public static final CreateAction DontMove = new CreateAction(0, 0);
  public static final CreateAction SpinLeftForward = new CreateAction(DefaultVelocity, 0);
  public static final CreateAction SpinLeftBackward = new CreateAction(-DefaultVelocity, 0);
  public static final CreateAction SpinRightForward = new CreateAction(0, DefaultVelocity);
  public static final CreateAction SpinRightBackward = new CreateAction(0, -DefaultVelocity);
  public static final CreateAction SpinLeft = new CreateAction(-DefaultVelocity, DefaultVelocity);
  public static final CreateAction SpinRight = new CreateAction(DefaultVelocity, -DefaultVelocity);
  public static final CreateAction Backward = new CreateAction(-DefaultVelocity, -DefaultVelocity);
  public static final CreateAction Forward = new CreateAction(DefaultVelocity, DefaultVelocity);

  public static final CreateAction DoNothing = new CreateAction(null);

  public CreateAction(double wheelLeft, double wheelRight) {
    super(wheelLeft, wheelRight);
  }

  public CreateAction(double[] actions) {
    super(actions);
  }

  public double right() {
    return actions[1];
  }

  public double left() {
    return actions[0];
  }
}
