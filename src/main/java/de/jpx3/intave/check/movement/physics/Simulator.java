package de.jpx3.intave.check.movement.physics;

import de.jpx3.intave.check.movement.Physics;
import de.jpx3.intave.check.movement.physics.environment.SimulationEnvironment;
import de.jpx3.intave.share.Motion;
import de.jpx3.intave.share.Position;
import de.jpx3.intave.user.User;

public abstract class Simulator {
  private Physics physics;

  public final void enterLinkage(Physics physics) {
    this.physics = physics;
  }

  public abstract void simulatePreInput(
    User user, Motion motion,
    SimulationEnvironment environment
  );

  public abstract Simulation simulatePrePosition(
    User user, Motion motion,
    SimulationEnvironment environment,
    MovementConfiguration configuration
  );

  public abstract void simulateAfterPosition(
    User user, SimulationEnvironment environment,
    Position position, Motion motion
  );

  public abstract void setback(
    User user,
    SimulationEnvironment environment,
    double predictedX, double predictedY, double predictedZ
  );

  public String debugName() {
    return "";
  }

  @Deprecated
  protected Physics physics() {
    return physics;
  }

  public float stepHeight() {
    return 0.6f;
  }

  public boolean affectedByMovementKeys() {
    return true;
  }
}