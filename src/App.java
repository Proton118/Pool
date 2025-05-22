import java.util.ArrayList;

import Utility.Color;
import Utility.Vector;
import processing.core.*;

public class App extends PApplet {
    private PhysicsEngine physicsEngine = new PhysicsEngine();
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
        physicsEngine.AddCollider(new Ball(55, new Vector(width / 2 + 200, height / 2), 5, 0.08f).SetVelocity(new Vector(-10, 0))).
        AddCollider(new Wall(new Vector(width / 2, height / 2 + 300), 1000, 10)).
        AddCollider(new Ball(Color.BLACK, 8, 50, new Vector(width / 2 - 40, height / 2), 5, 0.08f).SetVelocity(new Vector(80, 0)));

        previousTime = System.currentTimeMillis();
    }

    public void draw() {
        background(180);

        float deltaTime = (System.currentTimeMillis() - previousTime) / 1000f;
        previousTime = System.currentTimeMillis();

        physicsEngine.UpdateColliderPositions(deltaTime);
        physicsEngine.HandleCollisions();
        DrawColliders();
    }

    public void DrawColliders() {
        ArrayList<ICollider> colliders = physicsEngine.GetColliders();
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

}