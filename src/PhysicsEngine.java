import java.util.ArrayList;

import Utility.CollisionData;
import Utility.Vector;

public class PhysicsEngine {
    private static final float COLLISION_LOSS = 0.15f;

    private ArrayList<ICollider> colliders;
    private ArrayList<TablePocket> pockets;

    public PhysicsEngine() {
        colliders = new ArrayList<>();
        pockets = new ArrayList<>();
    }

    public PhysicsEngine AddCollider(ICollider collider) {
        colliders.add(collider);
        return this;
    }

    public PhysicsEngine AddColliders(ArrayList<ICollider> colliders) {
        this.colliders.addAll(colliders);
        return this;
    }

    public PhysicsEngine AddPockets(ArrayList<TablePocket> pockets) {
        this.pockets.addAll(pockets);
        return this;
    }

    public void UpdateColliderPositions(float deltaTime) {
        for (ICollider collider : colliders) {
            collider.Update(deltaTime);
        }
    }

    public void HandleCollisions() {
        for (int i = 0; i < colliders.size(); i++) {
            ICollider colliderA = colliders.get(i);
            for (int j = i + 1; j < colliders.size(); j++) {
                ICollider colliderB = colliders.get(j);
                CheckCollision(colliderA, colliderB);
            }
        }
    }

    private void CheckCollision(ICollider a, ICollider b) {
        if (a instanceof Ball ballA && b instanceof Ball ballB) {
            CollisionData collisionData = CollisionCalculator.CalculateBallCollision(ballA, ballB);
            if (collisionData.GetCollided()) {

                // if(ballA.GetPreviousCollisionNumber() == ballB.GetBallNumber()) {
                // ballA.MoveToSurface(collisionData.GetPointOfContact());
                // ballB.MoveToSurface(collisionData.GetPointOfContact());
                // return;
                // }
                HandleBallCollision(ballA, ballB, collisionData);
                ballA.SetPreviousCollisionNumber(ballB.GetBallNumber());
                ballB.SetPreviousCollisionNumber(ballA.GetBallNumber());
            }
        }

        if (a instanceof Wall wall && b instanceof Ball ball) {
            CollisionData collisionData = CollisionCalculator.CalculateWallCollision(ball, wall);
            if (collisionData.GetCollided()) {
                // if(ball.GetPreviousCollisionNumber() == wall.GetWallNumber()) {
                // ball.MoveToSurface(collisionData.GetPointOfContact());
                // return;
                // }
                HandleWallCollision(ball, collisionData);
                ClearCollisionNumbers();
            }
        }

        if (a instanceof Ball ball && b instanceof Wall wall) {
            CollisionData collisionData = CollisionCalculator.CalculateWallCollision(ball, wall);
            if (collisionData.GetCollided()) {
                // if(ball.GetPreviousCollisionNumber() == wall.GetWallNumber()) {
                // ball.MoveToSurface(collisionData.GetPointOfContact());
                // return;
                // }
                HandleWallCollision(ball, collisionData);
                ClearCollisionNumbers();
            }
        }
    }

    public void CheckPockets() { // TODO: make sure a ball cannot miss the pocket
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

    public void CheckOutOfBounds(Vector screenSize) {
        ArrayList<Ball> ballsToRemove = new ArrayList<>();
        float PPI = PoolTable.PIXELS_PER_INCH;
        float buffer = 1;
        screenSize = screenSize.multiply(1 / PPI);
        for (ICollider collider : colliders) {
            if (collider instanceof Ball ball) {
                boolean outOfBounds = ball.GetPosition().x + ball.GetRadius() > screenSize.x / 2 + (PoolTable.TABLE_WIDTH + buffer) / 2 ||
                        ball.GetPosition().x - ball.GetRadius() < screenSize.x / 2 - (PoolTable.TABLE_WIDTH + buffer)/ 2 ||
                        ball.GetPosition().y + ball.GetRadius() > screenSize.y / 2 + (PoolTable.TABLE_HEIGHT + buffer) / 2 ||
                        ball.GetPosition().y - ball.GetRadius() < screenSize.y / 2 - (PoolTable.TABLE_HEIGHT + buffer) / 2;
                if (outOfBounds) {
                    ballsToRemove.add(ball);
                }
            }
        }
        colliders.removeAll(ballsToRemove);
    }

    public boolean IsBallInArea(Vector position, float radius) {
        for (ICollider collider : colliders) {
            if (collider instanceof Ball ball) {
                if (ball.GetPosition().distance(position) <= radius + ball.GetRadius()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean IsBallInPocket(Ball ball) {
        return colliders.contains(ball);
    }

    public void HandleWallCollision(Ball ball, CollisionData collisionData) { // TODO: (Unlikely) BALLS GETTING STUCK IN
                                                                              // WALLS
        if (collisionData.GetWallNormal() != null) {
            ball.SetPosition(ball.GetPosition().add(ball.GetVelocity().multiply(-0.01f)));
            ball.MoveToSurfaceWall(collisionData.GetPointOfContact(), collisionData.GetWallNormal());
        } else {
            ball.MoveToSurface(collisionData.GetPointOfContact());
        }

        Vector reflectionVector = ball.GetPosition().subtract(collisionData.GetPointOfContact()).normalize();
        Vector projectionVector = reflectionVector.multiply((ball.GetVelocity().dot(reflectionVector))
                /
                (reflectionVector.dot(reflectionVector)));
        Vector newVelocityDirection = projectionVector.multiply(2).subtract(ball.GetVelocity()).multiply(-1);
        ball.SetVelocity(newVelocityDirection.multiply(1 - COLLISION_LOSS));
    }

    public void HandleBallCollision(Ball ballA, Ball ballB, CollisionData collisionData) {
        ballA.MoveToSurface(collisionData.GetPointOfContact());
        ballB.MoveToSurface(collisionData.GetPointOfContact());
        if (ballA.GetPosition().equals(ballB.GetPosition(), 0.1f)) {
            ballA.SetPosition(ballA.GetPosition().add(ballA.GetVelocity().multiply(-0.1f)));
            ballA.MoveToSurface(collisionData.GetPointOfContact());
            ballB.MoveToSurface(collisionData.GetPointOfContact());
        }

        float momentumA = ballA.GetMass() * ballA.GetVelocity().magnitude();
        float momentumB = ballB.GetMass() * ballB.GetVelocity().magnitude();

        float massA = ballA.GetMass() + ballA.GetVelocity().magnitude() / 10000;
        float massB = ballB.GetMass() + ballB.GetVelocity().magnitude() / 10000;

        Vector collisionAngle = CalculateCollisionAngle(ballA, ballB);

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

        ballA.SetVelocity(new Vector(newVelocityAX, newVelocityAY).multiply(1 - COLLISION_LOSS));
        ballB.SetVelocity(new Vector(newVelocityBX, newVelocityBY).multiply(1 - COLLISION_LOSS));
    }

    private Vector CalculateCollisionAngle(Ball ballA, Ball ballB) {
        return new Vector(
                ballB.GetPosition().x - ballA.GetPosition().x,
                ballB.GetPosition().y - ballA.GetPosition().y)
                .multiply(1 / ballA.GetPosition().distance(ballB.GetPosition()));
    }

    public ArrayList<ICollider> GetColliders() {
        return colliders;
    }

    public ArrayList<TablePocket> GetPockets() {
        return pockets;
    }

    public void ClearCollisionNumbers() {
        for (ICollider collider : colliders) {
            if (collider instanceof Ball ball) {
                ball.SetPreviousCollisionNumber(0);
            }
        }
    }
}
