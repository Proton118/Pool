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
        float PPI = PoolTable.PIXELS_PER_INCH;
        physicsEngine.AddCollider(new Ball(2.25f, new Vector((width / 2 + 200) / PPI, height / 2 / PPI), 5, 0.08f).SetVelocity(new Vector(-2, 0))).
        AddCollider(new Ball(Color.BLACK, 8, 2.25f, new Vector((width / 2 - 40) / PPI, height / 2 / PPI), 5, 0.08f).SetVelocity(new Vector(20, 0))).
        AddColliders(new PoolTable(width, height).GetWalls());

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
        float PPI = PoolTable.PIXELS_PER_INCH;
        System.out.println(PPI);
        fill(ball.GetColor().r, ball.GetColor().g, ball.GetColor().b);
        circle(ball.GetPosition().x * PPI, ball.GetPosition().y * PPI, ball.GetRadius() * 2 * PPI);
        if(ball.GetBallNumber() != -1){
            fill(255);
            circle(ball.GetPosition().x * PPI, ball.GetPosition().y * PPI, ball.GetRadius() * (9.5f/10) * PPI);
            fill(0);
            textSize(2);
            textAlign(CENTER, CENTER);
            text(ball.GetBallNumber(), ball.GetPosition().x * PPI, (ball.GetPosition().y - 6f) * PPI);
        }
    }

}