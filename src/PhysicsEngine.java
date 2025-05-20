import java.util.ArrayList;

import Utility.CollisionData;

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

        if(ballA.GetVelocity() > ballB.GetVelocity()){
            movingBall = ballA;
            stationaryBall = ballB;
        } else {
            movingBall = ballB;
            stationaryBall = ballA;
        }

        movingBall.OnCollision(stationaryBall, collisionData.GetPointOfContact());
        stationaryBall.OnCollision(movingBall, collisionData.GetPointOfContact());

        movingBall.SetVelocity(movingBall.GetVelocity() * (1 - COLLISION_LOSS));
        float stationaryBallVelocityFinal = (2 * movingBall.GetMass() * movingBall.GetVelocity() + stationaryBall.GetMass() * stationaryBall.GetVelocity()) / (movingBall.GetMass() + stationaryBall.GetMass());
        System.out.println(stationaryBallVelocityFinal);

        float movingBallVelocityFinal = movingBall.GetVelocity() * (movingBall.GetMass() - stationaryBall.GetMass()) / (movingBall.GetMass() + stationaryBall.GetMass());
        System.out.println(movingBallVelocityFinal);

        stationaryBall.SetVelocity(stationaryBallVelocityFinal);
        stationaryBall.SetVelocityDirection(movingBall.GetVelocityDirection());
        movingBall.SetVelocity(movingBallVelocityFinal);
    }

    public ArrayList<ICollider> GetColliders() {
        return colliders;
    }
}
