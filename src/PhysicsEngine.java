import java.util.ArrayList;

import Utility.CollisionData;
import Utility.Vector;

public class PhysicsEngine {
    private static final float COLLISION_LOSS_WALL = 0.3f;
    private static final float COLLISION_LOSS_BALL = 0.1f;

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
    public void ClearBalls(){
        ArrayList<ICollider> ballsToRemove = new ArrayList<>();
        for (ICollider collider : colliders) {
            if (collider instanceof Ball) {
                ballsToRemove.add(collider);
            }
        }
        colliders.removeAll(ballsToRemove);
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
        float ballStep = 0.000015f;
        int ballRecursionDepth = 11000;

        float wallStep = 0.001f;
        int wallRecursionDepth = 10000;

        if (a instanceof Ball ballA && b instanceof Ball ballB) {
            CollisionData isCollided = CollisionCalculator.CalculateBallCollision(ballA, ballB);
            if (isCollided.GetCollided()) {
                CollisionData collisionData = RecursivelyFindCollisionPoint(ballA, ballB, ballStep, ballRecursionDepth);

                HandleBallCollision(ballA, ballB, collisionData);
                ballA.SetPreviousCollisionNumber(ballB.GetBallNumber());
                ballB.SetPreviousCollisionNumber(ballA.GetBallNumber());
            }
        }

        if (a instanceof Wall wall && b instanceof Ball ball) {
            CollisionData isCollided = CollisionCalculator.CalculateWallCollision(ball, wall);
            if (isCollided.GetCollided()) {
                CollisionData collisionData = RecursivelyFindCollisionPoint(ball, wall, wallStep, wallRecursionDepth);

                HandleWallCollision(ball, collisionData);
                ClearCollisionNumbers();
            }
        }

        if (a instanceof Ball ball && b instanceof Wall wall) {
            CollisionData isCollided = CollisionCalculator.CalculateWallCollision(ball, wall);
            if (isCollided.GetCollided()) {
                CollisionData collisionData = RecursivelyFindCollisionPoint(ball, wall, wallStep, wallRecursionDepth);
                
                HandleWallCollision(ball, collisionData);
                ClearCollisionNumbers();
            }
        }
    }

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

    public void CheckOutOfBounds(Vector screenSize) {
        ArrayList<Ball> ballsToRemove = new ArrayList<>();
        float PPI = PoolTable.PIXELS_PER_INCH;
        float buffer = 1;
        screenSize = screenSize.multiply(1 / PPI);
        for (ICollider collider : colliders) {
            if (collider instanceof Ball ball) {
                boolean outOfBounds = ball.GetPosition().x + Ball.RADIUS > screenSize.x / 2 + (PoolTable.TABLE_WIDTH + buffer) / 2 ||
                        ball.GetPosition().x - Ball.RADIUS < screenSize.x / 2 - (PoolTable.TABLE_WIDTH + buffer)/ 2 ||
                        ball.GetPosition().y + Ball.RADIUS > screenSize.y / 2 + (PoolTable.TABLE_HEIGHT + buffer) / 2 ||
                        ball.GetPosition().y - Ball.RADIUS < screenSize.y / 2 - (PoolTable.TABLE_HEIGHT + buffer) / 2;
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
                if (ball.GetPosition().distance(position) <= radius + Ball.RADIUS) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean IsBallInPocket(Ball ball) {
        return !colliders.contains(ball);
    }

    public void HandleWallCollision(Ball ball, CollisionData collisionData) {
        Vector reflectionVector = ball.GetPosition().subtract(collisionData.GetPointOfContact()).normalize();
        Vector projectionVector = reflectionVector.multiply((ball.GetVelocity().dot(reflectionVector))
                /
                (reflectionVector.dot(reflectionVector)));
        Vector newVelocityDirection = projectionVector.multiply(2).subtract(ball.GetVelocity()).multiply(-1);
        ball.SetVelocity(newVelocityDirection.multiply(1 - COLLISION_LOSS_WALL));
    }

    public void HandleBallCollision(Ball ballA, Ball ballB, CollisionData collisionData) {
        float momentumA = ballA.GetMass() * ballA.GetVelocity().magnitude();
        float momentumB = ballB.GetMass() * ballB.GetVelocity().magnitude();

        float massA = ballA.GetMass() + ballA.GetVelocity().magnitude() / 5000;
        float massB = ballB.GetMass() + ballB.GetVelocity().magnitude() / 5000;

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

        ballA.SetVelocity(new Vector(newVelocityAX, newVelocityAY).multiply(1 - COLLISION_LOSS_BALL));
        ballB.SetVelocity(new Vector(newVelocityBX, newVelocityBY).multiply(1 - COLLISION_LOSS_BALL));
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

    private CollisionData RecursivelyFindCollisionPoint(Ball ballA, Ball ballB, float step, int recursionDeath){
        if (recursionDeath <= 0) {
            System.out.println("Max recursion depth reached for ball collision");
            return CollisionCalculator.CalculateBallCollision(ballA, ballB);
        }
        if(ballA.GetVelocity().magnitude() == 0 && ballB.GetVelocity().magnitude() == 0){
            ballA.SetPosition(ballA.GetPosition().add(new Vector(-1, 0).multiply(-step)));
            ballB.SetPosition(ballB.GetPosition().add(new Vector(1, 0).multiply(-step)));
        } else {
            ballA.SetPosition(ballA.GetPosition().add(ballA.GetVelocity().multiply(-step)));
            ballB.SetPosition(ballB.GetPosition().add(ballB.GetVelocity().multiply(-step)));
        }
        CollisionData collisionData = CollisionCalculator.CalculateBallCollision(ballA, ballB);
        if(!collisionData.GetCollided()) {
            return collisionData;
        } else {
            return RecursivelyFindCollisionPoint(ballA, ballB, step, recursionDeath - 1);
        }
    }
    private CollisionData RecursivelyFindCollisionPoint(Ball ball, Wall wall, float step, int recursionDeath){
        if (recursionDeath <= 0) {
            System.out.println("Max recursion depth reached for wall collision");
            return CollisionCalculator.CalculateWallCollision(ball, wall);
        }
        ball.SetPosition(ball.GetPosition().add(ball.GetVelocity().normalize().multiply(-step)));
        CollisionData collisionData = CollisionCalculator.CalculateWallCollision(ball, wall);
        if(!collisionData.GetCollided()) {
            ball.SetPosition(ball.GetPosition().add(ball.GetVelocity().normalize().multiply(step)));
            collisionData = CollisionCalculator.CalculateWallCollision(ball, wall);
            return collisionData;
        } else {
            return RecursivelyFindCollisionPoint(ball, wall, step, recursionDeath - 1);
        }
    }
}
