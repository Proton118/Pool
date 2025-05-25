import java.util.ArrayList;

import Utility.Color;
import Utility.Vector;
import processing.core.*;

public class App extends PApplet {
    private PhysicsEngine physicsEngine = new PhysicsEngine();
    private long previousTime = 0;
    private PoolCue cue;

    private PoolTable table;

    public static void main(String[] args) {
        PApplet.main("App");
    }

    public void settings() {
        fullScreen();
        pixelDensity(2);
        smooth(16);
    }

    public void setup() {
        table = new PoolTable(width, height, this);
        float PPI = PoolTable.PIXELS_PER_INCH;
        physicsEngine.AddCollider(new Ball(2.25f, new Vector((width / 2 + 200) / PPI, height / 2 / PPI), 0.17f))
                .AddCollider(new Ball(Color.BLACK, 8, 2.25f, new Vector((width / 2) / PPI, height / 2 / PPI), 0.17f))
                .AddCollider(
                        new Ball(Color.BLUE, 5, 2.25f, new Vector((width / 2 + 50) / PPI, height / 2 / PPI), 0.17f))
                .AddCollider(
                        new Ball(Color.GREEN, 3, 2.25f, new Vector((width / 2 - 50) / PPI, height / 2 / PPI), 0.17f))
                .AddColliders(table.GetWalls());

        cue = new PoolCue(this);

        previousTime = System.currentTimeMillis();
    }

    public void draw() {
        background(180);
        int tableWidth = 1400;
        int tableHeight = 700;
        image(table.GetTableImage(), width / 2 - tableWidth / 2, height / 2 - tableHeight / 2, tableWidth, tableHeight);

        float deltaTime = (System.currentTimeMillis() - previousTime) / 1000f;
        previousTime = System.currentTimeMillis();

        physicsEngine.UpdateColliderPositions(deltaTime);
        physicsEngine.HandleCollisions();
        DrawColliders();
        if (AreAllBallsAtRest()) {
            Vector fireVector = cue.UpdateCue((Ball) physicsEngine.GetColliders().get(0));
            if (fireVector != null) {
                Ball cueBall = (Ball) physicsEngine.GetColliders().get(0);
                cueBall.SetPreviousCollisionNumber(0);
                cueBall.SetVelocity(fireVector);
            }
        }
    }

    public void DrawColliders() {
        ArrayList<ICollider> colliders = physicsEngine.GetColliders();
        noStroke();
        for (ICollider collider : colliders) {
            if (collider instanceof Ball) {
                Ball ball = (Ball) collider;
                DrawBall(ball);
            } else if (collider instanceof Wall) {
                // float PPI = PoolTable.PIXELS_PER_INCH;
                // Wall wall = (Wall) collider;
                // fill(255);
                // rect(wall.GetPosition().x * PPI - wall.GetWidth() / 2 * PPI,
                // wall.GetPosition().y * PPI - wall.GetHeight() / 2 * PPI,
                // wall.GetWidth() * PPI, wall.GetHeight() * PPI);
            } else if (collider instanceof TablePocket) {

            }
        }
    }

    public void DrawBall(Ball ball) {
        float PPI = PoolTable.PIXELS_PER_INCH;
        fill(ball.GetColor().r, ball.GetColor().g, ball.GetColor().b);
        circle(ball.GetPosition().x * PPI, ball.GetPosition().y * PPI, ball.GetRadius() * 2 * PPI);
        if (ball.GetBallNumber() != -1) {
            fill(255);
            circle(ball.GetPosition().x * PPI, ball.GetPosition().y * PPI, ball.GetRadius() * (9.75f / 10) * PPI);
            fill(0);
            textSize(12);
            textAlign(CENTER, CENTER);
            text(ball.GetBallNumber(), ball.GetPosition().x * PPI, (ball.GetPosition().y - 0.25f) * PPI);
        }
    }

    public boolean AreAllBallsAtRest() {
        for (ICollider collider : physicsEngine.GetColliders()) {
            if (collider instanceof Ball) {
                Ball ball = (Ball) collider;
                if(ball.GetVelocity().magnitude() > 0.01f) {
                    System.out.println("Ball " + ball.GetBallNumber() + " is moving with velocity: " + ball.GetVelocity());
                    return false;
                }
            }
        }
        return true;
    }

}