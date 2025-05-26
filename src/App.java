import java.util.ArrayList;

import Utility.Vector;
import processing.core.*;

public class App extends PApplet {
    private static final float TIME_SCALE = 1 / 2f;

    private PhysicsEngine physicsEngine = new PhysicsEngine();
    private long previousTime = 0;

    private PoolCue cue;
    private Ball cueBall;
    private BallSetup ballSetup;
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
        cueBall = new Ball(2.25f, new Vector((width / 2 + 320) / PPI, height / 2 / PPI), 0.17f);
        ballSetup = new BallSetup(new Vector((width / 2 - 320) / PPI, height / 2 / PPI));

        physicsEngine.AddCollider(cueBall)
                .AddColliders(ballSetup.GetBalls())
                .AddColliders(table.GetWalls())
                .AddPockets(table.GetPockets());

        cue = new PoolCue(this);

        previousTime = System.currentTimeMillis();
    }

    public void draw() {
        background(180);
        int tableWidth = 1400;
        int tableHeight = 700;
        image(table.GetTableImage(), width / 2 - tableWidth / 2, height / 2 - tableHeight / 2, tableWidth, tableHeight);

        float deltaTime = (System.currentTimeMillis() - previousTime) / 1000f * TIME_SCALE;
        previousTime = System.currentTimeMillis();

        physicsEngine.UpdateColliderPositions(deltaTime);
        physicsEngine.HandleCollisions();
        physicsEngine.CheckPockets();
        DrawColliders();
        if (AreAllBallsAtRest()) {
            UpdateCueBall();
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
            }
        }
        float PPI = PoolTable.PIXELS_PER_INCH;
        for(TablePocket pocket : physicsEngine.GetPockets()) {
        fill(255);
        circle(pocket.GetPosition().x * PPI, pocket.GetPosition().y * PPI,
        pocket.GetRadius() * 2 * PPI);
        circle(pocket.GetPosition().x * PPI, pocket.GetPosition().y * PPI,
        pocket.GetRadius() * 2 * PPI);
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
                if (ball.GetVelocity().magnitude() > 0.01f) {
                    return false;
                }
            }
        }
        return true;
    }

    public void UpdateCueBall() {
        if(!physicsEngine.IsBallInPocket(cueBall)) {
            PlaceBall();
            return;
        }

        Vector fireVector = cue.UpdateCue(cueBall);
        if (fireVector != null) {
            cueBall.SetPreviousCollisionNumber(0);
            cueBall.SetVelocity(fireVector);
        }
    }

    public void PlaceBall(){
        float PPI = PoolTable.PIXELS_PER_INCH;

        boolean cueBallCollidingWithOtherBall = physicsEngine.IsBallInArea(new Vector(mouseX / PPI, mouseY / PPI), cueBall.GetRadius());
        boolean cueBallOutOfBounds = mouseX + cueBall.GetRadius() * PPI > width / 2 + PoolTable.TABLE_WIDTH * PPI / 2 ||
                mouseX - cueBall.GetRadius() * PPI < width / 2 - PoolTable.TABLE_WIDTH * PPI / 2 ||
                mouseY + cueBall.GetRadius() * PPI > height / 2 + PoolTable.TABLE_HEIGHT * PPI / 2 ||
                mouseY - cueBall.GetRadius() * PPI < height / 2 - PoolTable.TABLE_HEIGHT * PPI / 2;

        if(cueBallCollidingWithOtherBall || cueBallOutOfBounds) {
            fill(255, 100, 100, 150);
            circle(mouseX, mouseY, cueBall.GetRadius() * 2 * PPI);
            return;
        }

        if(mousePressed) {
            cueBall.SetPosition(new Vector(mouseX / PPI, mouseY / PPI));
            cueBall.SetVelocity(new Vector(0, 0));
            cueBall.SetPreviousCollisionNumber(0);
            physicsEngine.AddCollider(cueBall);
        }
        fill(255, 255, 255, 150);
        circle(mouseX, mouseY, PPI * cueBall.GetRadius() * 2);
    }

}