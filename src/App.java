import java.util.ArrayList;

import processing.core.*;

public class App extends PApplet {
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
        System.out.println("Screen size: " + width + "x" + height);
        // colliders.add(new Ball(50, new Vector(width / 2 + 200, height / 2 + 10), 1, 0f));
        colliders.add(new Ball(50, new Vector(width / 2 + 20, height / 2), 1, 2f).SetVelocity(new Vector(0,100)));
        colliders.add(new Wall(new Vector(width / 2, height / 2 + 200), 20, 10));

        previousTime = System.currentTimeMillis();
    }

    public void draw() {
        background(0);

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
                circle(ball.GetPosition().x, ball.GetPosition().y, ball.GetRadius() * 2);
            }
            else if (collider instanceof Wall) {
                Wall wall = (Wall) collider;
                fill(255);
                rect(wall.GetPosition().x - wall.GetWidth() / 2, wall.GetPosition().y - wall.GetHeight() / 2,
                        wall.GetWidth(), wall.GetHeight());
            }
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
                    colliderA.OnCollision(colliderB, collisionData.GetPointOfContact());
                    colliderB.OnCollision(colliderA, collisionData.GetPointOfContact());
                }
            }
        }
    }
}