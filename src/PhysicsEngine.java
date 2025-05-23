import java.util.ArrayList;

import Utility.CollisionData;
import Utility.Vector;

public class PhysicsEngine {
    private static final float COLLISION_LOSS = 0.15f;

    private ArrayList<ICollider> colliders;

    public PhysicsEngine() {
        this.colliders = new ArrayList<>();   
    }
    
    public PhysicsEngine AddCollider(ICollider collider) {
        colliders.add(collider);
        return this;
    }
    public PhysicsEngine AddColliders(ArrayList<ICollider> colliders) {
        this.colliders.addAll(colliders);
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

    private void CheckCollision(ICollider a, ICollider b){
        if(a instanceof Ball ballA && b instanceof Ball ballB){
            CollisionData collisionData = CollisionCalculator.CalculateBallCollision(ballA, ballB);
            if(ballA.GetPreviousCollisionNumber() == ballB.GetBallNumber()) { return; }
            if(collisionData.GetCollided()){
                HandleBallCollision(ballA, ballB, collisionData);
                ballA.SetPreviousCollisionNumber(ballB.GetBallNumber());
                ballB.SetPreviousCollisionNumber(ballA.GetBallNumber());
            }
        }

        if(a instanceof Wall wall && b instanceof Ball ball){
            CollisionData collisionData = CollisionCalculator.CalculateWallCollision(ball, wall);
            if(ball.GetPreviousCollisionNumber() == wall.GetWallNumber()) { return; }
            if(collisionData.GetCollided()){
                HandleWallCollision(ball, collisionData);
                ball.SetPreviousCollisionNumber(wall.GetWallNumber());
            }
        }

        if(a instanceof Ball ball && b instanceof Wall wall){
            CollisionData collisionData = CollisionCalculator.CalculateWallCollision(ball, wall);
            if(ball.GetPreviousCollisionNumber() == wall.GetWallNumber()) { return; }
            if(collisionData.GetCollided()){
                HandleWallCollision(ball, collisionData);
                ball.SetPreviousCollisionNumber(wall.GetWallNumber());
            }
        }
    }

    public void HandleWallCollision(Ball ball, CollisionData collisionData) {
        ball.MoveToSurface(collisionData.GetPointOfContact());

        Vector reflectionVector = ball.GetPosition().subtract(collisionData.GetPointOfContact()).normalize();
        Vector projectionVector = reflectionVector.multiply((ball.GetVelocity().dot(reflectionVector))
                /
                (reflectionVector.dot(reflectionVector)));
        Vector newVelocityDirection = projectionVector.multiply(2).subtract(ball.GetVelocity()).multiply(-1);
        ball.SetVelocity(newVelocityDirection.multiply(1 - COLLISION_LOSS));
    }

    public void HandleBallCollision(Ball ballA, Ball ballB, CollisionData collisionData) {
        Vector collisionAngle = CalculateCollisionAngle(ballA, ballB);

        Vector velocityA = ballA.GetVelocity();
        Vector velocityB = ballB.GetVelocity();

        float velocityANormal = velocityA.x *collisionAngle.x + velocityA.y * collisionAngle.y;
        float velocityATangent = velocityA.x * -collisionAngle.y + velocityA.y * collisionAngle.x;

        float velocityBNormal = velocityB.x *collisionAngle.x + velocityB.y * collisionAngle.y;
        float velocityBTangent = velocityB.x * -collisionAngle.y + velocityB.y * collisionAngle.x;

        float newVelocityANormal = velocityANormal * (ballA.GetMass() - ballB.GetMass()) + 2 * ballB.GetMass() * velocityBNormal
                                                                                            /
                                                                        (ballA.GetMass() + ballB.GetMass());
        float newVelocityBNormal = velocityBNormal * (ballB.GetMass() - ballA.GetMass()) + 2 * ballA.GetMass() * velocityANormal
                                                                                            /
                                                                        (ballA.GetMass() + ballB.GetMass());

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
                ballB.GetPosition().y - ballA.GetPosition().y
        ).multiply(1 / ballA.GetPosition().distance(ballB.GetPosition()));
    }

    public ArrayList<ICollider> GetColliders() {
        return colliders;
    }
}
