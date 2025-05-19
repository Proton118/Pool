import java.util.ArrayList;

import processing.core.*;

public class App extends PApplet {
    private static final float COLLISION_LOSS = 0.25f;

    private ArrayList<ICollider> colliders = new ArrayList<>();
    private long previousTime = 0;

    public static void main(String[] args) {
        PApplet.main("App");
    }

    public void settings() {
        fullScreen();
        pixelDensity(2);
        smooth(16);
    }

    public void setup() {
        colliders.add(new Ball(55, new Vector(width / 2 + 200, height / 2), 5, 3f));
        colliders.add(new Wall(new Vector(width / 2, height / 2 + 300), 1000, 10));
        colliders.add(new Ball(Color.BLACK, 8, 50, new Vector(width / 2 + 40, height / 2), 5, 3).SetVelocity(new Vector(150, 0)));

        previousTime = System.currentTimeMillis();
    }

    public void draw() {
        background(180);

        float deltaTime = (System.currentTimeMillis() - previousTime) / 1000f;
        previousTime = System.currentTimeMillis();

        UpdateColliders(deltaTime);
        CheckCollisions();
        DrawColliders();
    }

    public void DrawColliders() {
        noStroke();
        for (ICollider collider : colliders) {
            if (collider instanceof Ball) {
                Ball ball = (Ball) collider;
                DrawBall(ball);
            }
            else if (collider instanceof Wall) {
                Wall wall = (Wall) collider;
                fill(255);
                rect(wall.GetPosition().x - wall.GetWidth() / 2, wall.GetPosition().y - wall.GetHeight() / 2,
                        wall.GetWidth(), wall.GetHeight());
            }
        }
    }

    public void DrawBall(Ball ball) {
        fill(ball.GetColor().r, ball.GetColor().g, ball.GetColor().b);
        circle(ball.GetPosition().x, ball.GetPosition().y, ball.GetRadius() * 2);
        if(ball.GetBallNumber() != -1){
            fill(255);
            circle(ball.GetPosition().x, ball.GetPosition().y, ball.GetRadius() * 9.5f/10);
            fill(0);
            textSize(40);
            textAlign(CENTER, CENTER);
            text(ball.GetBallNumber(), ball.GetPosition().x, ball.GetPosition().y - 6f);
        }
    }

    public void UpdateColliders(float deltaTime) {
        for (ICollider collider : colliders) {
            collider.Update(deltaTime);
        }
    }
    public void CheckCollisions() {
        for (int i = 0; i < colliders.size(); i++) {
            ICollider colliderA = colliders.get(i);
            for (int j = i + 1; j < colliders.size(); j++) {
                ICollider colliderB = colliders.get(j);
                CollisionData collisionData = colliderA.IsCollidingWith(colliderB);
                if (collisionData.GetCollided()) {
                    CheckBallBallCollisions(colliderA, colliderB, collisionData);
                }
            }
        }
    }

    public void CheckBallBallCollisions(ICollider colliderA, ICollider colliderB, CollisionData collisionData) {
        if (colliderA instanceof Ball && colliderB instanceof Ball) {
            Ball ballA = (Ball) colliderA;
            Ball ballB = (Ball) colliderB;

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
    }
}