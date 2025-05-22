import java.util.ArrayList;

import Utility.CollisionData;
import Utility.Vector;

public class PhysicsEngine {
    private static final float COLLISION_LOSS = 0.25f;

    private ArrayList<ICollider> colliders;

    public PhysicsEngine() {
        this.colliders = new ArrayList<>();   
    }
    
    public PhysicsEngine AddCollider(ICollider collider) {
        colliders.add(collider);
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
            if(collisionData.GetCollided()){
                HandleBallCollision(ballA, ballB, collisionData);
            }
        }

        // if(a instanceof Wall wall && b instanceof Ball ball){
        // }

        // if(a instanceof Ball ball && b instanceof Wall wall){
        // }
    }

    public void HandleBallCollision(Ball ballA, Ball ballB, CollisionData collisionData) {
        Ball movingBall;
        Ball stationaryBall;

        if(ballA.GetVelocity().x > ballB.GetVelocity().x){
            movingBall = ballA;
            stationaryBall = ballB;
        } else {
            movingBall = ballB;
            stationaryBall = ballA;
        }
        System.out.println(CalculateCollisionAngle(ballA, ballB));

        movingBall.OnCollision(stationaryBall, collisionData.GetPointOfContact());
        stationaryBall.OnCollision(movingBall, collisionData.GetPointOfContact());

        movingBall.SetVelocity(movingBall.GetVelocity().x * (1 - COLLISION_LOSS));
        float stationaryBallVelocityFinal = (2 * movingBall.GetMass() * movingBall.GetVelocity().x + stationaryBall.GetMass() * stationaryBall.GetVelocity().x) / (movingBall.GetMass() + stationaryBall.GetMass());
        float movingBallVelocityFinal = movingBall.GetVelocity().x * (movingBall.GetMass() - stationaryBall.GetMass()) / (movingBall.GetMass() + stationaryBall.GetMass());

        stationaryBall.SetVelocity(movingBall.GetVelocity().normalize().multiply(stationaryBallVelocityFinal));
        movingBall.SetVelocity(movingBallVelocityFinal);
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
