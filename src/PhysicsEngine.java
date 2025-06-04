import java.util.ArrayList;

import Utility.CollisionData;
import Utility.Vector;

/**
 * A class that handles the physics engine for a pool table game, managing
 * collisions between balls and walls, and checking for pockets.
 */
public class PhysicsEngine {
    private static final float COLLISION_LOSS_WALL = 0.3f;
    private static final float COLLISION_LOSS_BALL = 0.1f;

    private ArrayList<ICollider> colliders;
    private ArrayList<TablePocket> pockets;

    public PhysicsEngine() {
        colliders = new ArrayList<>();
        pockets = new ArrayList<>();
    }

    private void CheckCollision(ICollider a, ICollider b) {
        float ballStep = 1.2f;
        int ballRecursionDepth = 5000;
        float ballStartingMagnitude = 1;

        float wallStep = 1.2f;
        int wallRecursionDepth = 5000;
        float wallStartingMagnitude = 1;

        if (a instanceof Ball ballA && b instanceof Ball ballB) {
            CollisionData isCollided = CollisionCalculator.CalculateBallCollision(ballA, ballB);
            if (isCollided.GetCollided()) {
                CollisionData collisionData = RecursivelyFindCollisionPoint(ballA, ballB, ballStep, ballRecursionDepth,
                        ballStartingMagnitude);

                HandleBallCollision(ballA, ballB, collisionData);
            }
        }

        if (a instanceof Wall wall && b instanceof Ball ball) {
            CollisionData isCollided = CollisionCalculator.CalculateWallCollision(ball, wall);
            if (isCollided.GetCollided()) {
                CollisionData collisionData = RecursivelyFindCollisionPoint(ball, wall, wallStep, wallRecursionDepth,
                        wallStartingMagnitude);

                HandleWallCollision(ball, collisionData);
            }
        }

        if (a instanceof Ball ball && b instanceof Wall wall) {
            CollisionData isCollided = CollisionCalculator.CalculateWallCollision(ball, wall);
            if (isCollided.GetCollided()) {
                CollisionData collisionData = RecursivelyFindCollisionPoint(ball, wall, wallStep, wallRecursionDepth,
                        wallStartingMagnitude);

                HandleWallCollision(ball, collisionData);
            }
        }
    }

    private void HandleWallCollision(Ball ball, CollisionData collisionData) {
        Vector reflectionVector = ball.GetPosition().subtract(collisionData.GetPointOfContact()).normalize();
        Vector projectionVector = reflectionVector.multiply((ball.GetVelocity().dot(reflectionVector))
                /
                (reflectionVector.dot(reflectionVector)));
        Vector newVelocityDirection = projectionVector.multiply(2).subtract(ball.GetVelocity()).multiply(-1);
        ball.SetVelocity(newVelocityDirection.multiply(1 - COLLISION_LOSS_WALL));
    }

    private void HandleBallCollision(Ball ballA, Ball ballB, CollisionData collisionData) {
        float momentumA = Ball.MASS * ballA.GetVelocity().magnitude();
        float momentumB = Ball.MASS * ballB.GetVelocity().magnitude();

        float massA = Ball.MASS + ballA.GetVelocity().magnitude() / 5000;
        float massB = Ball.MASS + ballB.GetVelocity().magnitude() / 5000;

        Vector collisionAngle = CalculateBallCollisionAngle(ballA, ballB);

        Vector velocityA = ballA.GetVelocity().normalize().multiply(momentumA / massA);
        Vector velocityB = ballB.GetVelocity().normalize().multiply(momentumB / massB);

        float velocityANormal = velocityA.x * collisionAngle.x + velocityA.y * collisionAngle.y;
        float velocityATangent = velocityA.x * -collisionAngle.y + velocityA.y * collisionAngle.x;

        float velocityBNormal = velocityB.x * collisionAngle.x + velocityB.y * collisionAngle.y;
        float velocityBTangent = velocityB.x * -collisionAngle.y + velocityB.y * collisionAngle.x;

        float newVelocityANormal = velocityANormal * (massA - massB) + 2 * massB * velocityBNormal
                /
                (massA + massB);
        float newVelocityBNormal = velocityBNormal * (massB - massA) + 2 * massA * velocityANormal
                /
                (massA + massB);

        float newVelocityAX = newVelocityANormal * collisionAngle.x - velocityATangent * collisionAngle.y;
        float newVelocityAY = newVelocityANormal * collisionAngle.y + velocityATangent * collisionAngle.x;

        float newVelocityBX = newVelocityBNormal * collisionAngle.x - velocityBTangent * collisionAngle.y;
        float newVelocityBY = newVelocityBNormal * collisionAngle.y + velocityBTangent * collisionAngle.x;

        ballA.SetVelocity(new Vector(newVelocityAX, newVelocityAY).multiply(1 - COLLISION_LOSS_BALL));
        ballB.SetVelocity(new Vector(newVelocityBX, newVelocityBY).multiply(1 - COLLISION_LOSS_BALL));
    }

    private Vector CalculateBallCollisionAngle(Ball ballA, Ball ballB) {
        return new Vector(
                ballB.GetPosition().x - ballA.GetPosition().x,
                ballB.GetPosition().y - ballA.GetPosition().y)
                .multiply(1 / ballA.GetPosition().distance(ballB.GetPosition()));
    }

    private CollisionData RecursivelyFindCollisionPoint(Ball ballA, Ball ballB, float step, int depth,
            float startingMagnitude) {
        if (step <= 1) {
            throw new IllegalArgumentException("Step must be greater than 1");
        }
        if (depth <= 0) {
            return CollisionCalculator.CalculateBallCollision(ballA, ballB);
        }
        if (ballA.GetVelocity().magnitude() == 0 && ballB.GetVelocity().magnitude() == 0) {
            ballA.SetPosition(ballA.GetPosition().add(new Vector(-1, 0).multiply(-startingMagnitude)));
            ballB.SetPosition(ballB.GetPosition().add(new Vector(1, 0).multiply(-startingMagnitude)));
            CollisionData collisionData = CollisionCalculator.CalculateBallCollision(ballA, ballB);
            if (!collisionData.GetCollided()) {
                ballA.SetPosition(ballA.GetPosition().add(new Vector(-1, 0).multiply(startingMagnitude)));
                ballB.SetPosition(ballB.GetPosition().add(new Vector(1, 0).multiply(startingMagnitude)));
            }
        } else {
            ballA.SetPosition(ballA.GetPosition().add(ballA.GetVelocity().multiply(-startingMagnitude)));
            ballB.SetPosition(ballB.GetPosition().add(ballB.GetVelocity().multiply(-startingMagnitude)));
            CollisionData collisionData = CollisionCalculator.CalculateBallCollision(ballA, ballB);
            if (!collisionData.GetCollided()) {
                ballA.SetPosition(ballA.GetPosition().add(ballA.GetVelocity().multiply(startingMagnitude)));
                ballB.SetPosition(ballB.GetPosition().add(ballB.GetVelocity().multiply(startingMagnitude)));
            }
        }
        return RecursivelyFindCollisionPoint(ballA, ballB, step, depth - 1, startingMagnitude / step);
    }

    private CollisionData RecursivelyFindCollisionPoint(Ball ball, Wall wall, float step, int depth,
            float startingMagnitude) {
        if (step <= 1) {
            throw new IllegalArgumentException("Step must be greater than 1");
        }
        if (depth <= 0) {
            return CollisionCalculator.CalculateWallCollision(ball, wall);
        }
        ball.SetPosition(ball.GetPosition().add(ball.GetVelocity().multiply(-startingMagnitude)));
        CollisionData collisionData = CollisionCalculator.CalculateWallCollision(ball, wall);
        if (!collisionData.GetCollided()) {
            ball.SetPosition(ball.GetPosition().add(ball.GetVelocity().multiply(startingMagnitude)));
        }
        return RecursivelyFindCollisionPoint(ball, wall, step, depth - 1, startingMagnitude / step);
    }

    /**
     * Updates the positions of all colliders based on their velocities.
     *
     * @param deltaTime The time elapsed since the last update.
     */
    public void UpdateColliderPositions(float deltaTime) {
        for (ICollider collider : colliders) {
            collider.Update(deltaTime);
        }
    }

    /**
     * Handles collisions between all colliders in the physics engine.
     * It checks for collisions between pairs of colliders and processes them.
     */
    public void HandleCollisions() {
        for (int i = 0; i < colliders.size(); i++) {
            ICollider colliderA = colliders.get(i);
            for (int j = i + 1; j < colliders.size(); j++) {
                ICollider colliderB = colliders.get(j);
                CheckCollision(colliderA, colliderB);
            }
        }
    }

    /**
     * Checks if any balls are in the pockets and removes them from the colliders
     * list.
     */
    public void CheckPockets() {
        ArrayList<Ball> ballsToRemove = new ArrayList<>();
        for (TablePocket pocket : pockets) {
            for (ICollider collider : colliders) {
                if (collider instanceof Ball ball) {
                    if (pocket.GetPosition().distance(ball.GetPosition()) <= pocket.GetRadius()) {
                        ballsToRemove.add(ball);
                    }
                }
            }
        }

        colliders.removeAll(ballsToRemove);
    }

    /**
     * Checks if any balls are out of bounds based on the table size and removes
     * them
     * from the colliders list.
     *
     * @param screenSize The size of the screen in pixels.
     */
    public void CheckOutOfBounds(Vector screenSize) {
        ArrayList<Ball> ballsToRemove = new ArrayList<>();
        float PPI = PoolTable.PIXELS_PER_INCH;
        float buffer = 1;
        screenSize = screenSize.multiply(1 / PPI);
        for (ICollider collider : colliders) {
            if (collider instanceof Ball ball) {
                boolean outOfBounds = ball.GetPosition().x + Ball.RADIUS > screenSize.x / 2
                        + (PoolTable.TABLE_WIDTH + buffer) / 2 ||
                        ball.GetPosition().x - Ball.RADIUS < screenSize.x / 2 - (PoolTable.TABLE_WIDTH + buffer) / 2 ||
                        ball.GetPosition().y + Ball.RADIUS > screenSize.y / 2 + (PoolTable.TABLE_HEIGHT + buffer) / 2 ||
                        ball.GetPosition().y - Ball.RADIUS < screenSize.y / 2 - (PoolTable.TABLE_HEIGHT + buffer) / 2;
                if (outOfBounds) {
                    ballsToRemove.add(ball);
                }
            }
        }
        colliders.removeAll(ballsToRemove);
    }

    /**
     * Checks if a ball is within a specified area defined by a position and radius.
     * 
     * @param position the center position of the area to check
     * @param radius   the radius of the area to check
     * @return true if a ball is within the area, false otherwise
     */
    public boolean IsBallInArea(Vector position, float radius) {
        for (ICollider collider : colliders) {
            if (collider instanceof Ball ball) {
                if (ball.GetPosition().distance(position) <= radius + Ball.RADIUS) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a specific ball is in a pocket.
     * 
     * @param ball the ball to check
     * @return true if the ball is in a pocket, false otherwise
     */
    public boolean IsBallInPocket(Ball ball) {
        return !colliders.contains(ball);
    }

    /**
     * Adds a collider to the physics engine.
     *
     * @param collider The collider to add.
     * @return The PhysicsEngine instance for method chaining.
     */
    public PhysicsEngine AddCollider(ICollider collider) {
        colliders.add(collider);
        return this;
    }

    /**
     * Adds multiple colliders to the physics engine.
     *
     * @param colliders The list of colliders to add.
     * @return The PhysicsEngine instance for method chaining.
     */
    public PhysicsEngine AddColliders(ArrayList<ICollider> colliders) {
        this.colliders.addAll(colliders);
        return this;
    }

    /**
     * Clears all balls from the colliders list.
     */
    public void ClearBalls() {
        ArrayList<ICollider> ballsToRemove = new ArrayList<>();
        for (ICollider collider : colliders) {
            if (collider instanceof Ball) {
                ballsToRemove.add(collider);
            }
        }
        colliders.removeAll(ballsToRemove);
    }

    /**
     * Adds a list of pockets to the physics engine.
     *
     * @param pockets The pockets to add.
     * @return The PhysicsEngine instance for method chaining.
     */
    public PhysicsEngine AddPockets(ArrayList<TablePocket> pockets) {
        this.pockets.addAll(pockets);
        return this;
    }

    /**
     * Gets the list of colliders in the physics engine.
     *
     * @return The list of colliders.
     */
    public ArrayList<ICollider> GetColliders() {
        return colliders;
    }
}