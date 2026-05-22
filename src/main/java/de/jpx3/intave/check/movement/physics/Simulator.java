package de.jpx3.intave.check.movement.physics;

import de.jpx3.intave.check.movement.physics.environment.SimulationEnvironment;
import de.jpx3.intave.share.Motion;
import de.jpx3.intave.share.Position;
import de.jpx3.intave.user.User;

public abstract class Simulator {
  public abstract void simulatePreTick(
    User user, Motion motion,
    SimulationEnvironment environment
  );

  public abstract Simulation simulateTick(
    User user, Motion motion,
    SimulationEnvironment environment,
    MovementConfiguration configuration
  );

  public abstract void simulateAfterTick(
    User user, SimulationEnvironment environment,
    Position position, Motion motion
  );

  public abstract void setback(
    User user,
    SimulationEnvironment environment,
    double predictedX, double predictedY, double predictedZ
  );

  public float stepHeight() {
    return 0.6f;
  }

  public boolean affectedByMovementKeys() {
    return true;
  }
}