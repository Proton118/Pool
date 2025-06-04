import java.util.ArrayList;

import Utility.Vector;
import processing.core.*;

public class App extends PApplet {
    private static final float TIME_SCALE = 1 / 2.2f;
    private static final String SAVE_FILE_NAME = "savedGame.txt";

    private AppState appState;
    private GamePhase gamePhase;

    private PhysicsEngine physicsEngine;
    private long previousTime = 0;

    private boolean waitingForMouseUp = false;

    private PoolCue cue;
    private Ball cueBall;
    private BallSetup ballSetup;
    private PoolTable table;

    private boolean breakShot;

    private PoolSaveManager saveManager;
    private boolean startUpMainScreen = true;

    private enum AppState {
        MAIN_SCREEN,
        GAME,
        GAME_OVER
    }

    private enum GamePhase {
        PLAYING,
        CUE_FIRING,
        CUE_ANIMATION,
        PLACE_BALL,
        PAUSED,
    }

    public static void main(String[] args) {
        PApplet.main("App");
    }

    public void settings() {
        fullScreen();
        pixelDensity(2);
        smooth(16);
    }

    public void setup() {
        cue = new PoolCue(this);
        table = new PoolTable(width, height, this);

        GameSetup();
        appState = AppState.MAIN_SCREEN;

        saveManager = new PoolSaveManager(SAVE_FILE_NAME);
    }

    public void draw() {
        if (!mousePressed && waitingForMouseUp) {
            waitingForMouseUp = false;
        }
        switch (appState) {
            case MAIN_SCREEN:
                DrawMainScreen();
                break;
            case GAME:
                DrawGamePhase();
                break;
            case GAME_OVER:
                DrawGameOver();
                break;
        }
    }

    private void DrawMainScreen() {
        GameBase();
        DrawColliders();

        fill(0);
        textSize(100);
        textAlign(CENTER, CENTER);
        text("Pool", width / 2, height / 2);

        Button startButton = new Button(new Vector(width / 2, height / 2 + 100), new Vector(400, 50), "Start New Game",
                this);
        startButton.drawText();
        if (startButton.isMouseOver() && mousePressed && !waitingForMouseUp) {
            GameSetup();
            waitingForMouseUp = true;
            startUpMainScreen = false;
            return;
        }

        if (startUpMainScreen) {
            if (saveManager.isSaveFileEmpty()) {
                return;
            }
            Button loadButton = new Button(new Vector(width / 2, height / 2 + 160), new Vector(400, 50), "Load Game",
                    this);
            loadButton.drawText();
            if (loadButton.isMouseOver() && mousePressed && !waitingForMouseUp) {
                physicsEngine.ClearBalls();

                cueBall = saveManager.loadCueBall(physicsEngine);
                ballSetup = saveManager.loadBallSetup(physicsEngine);

                startUpMainScreen = false;
                waitingForMouseUp = true;
                breakShot = false;
            }
            return;
        }

        Button continueButton = new Button(new Vector(width / 2, height / 2 + 160), new Vector(400, 50),
                "Continue Game", this);
        continueButton.drawText();
        if (continueButton.isMouseOver() && mousePressed && !waitingForMouseUp) {
            appState = AppState.GAME;
            gamePhase = GamePhase.PLAYING;
            getDeltaTime();
            waitingForMouseUp = true;
        }
    }

    private void DrawGameOver() {
        GameBase();
        DrawColliders();
        float deltaTime = getDeltaTime();
        physicsEngine.UpdateColliderPositions(deltaTime);
        physicsEngine.HandleCollisions();
        physicsEngine.CheckPockets();
        physicsEngine.CheckOutOfBounds(new Vector(width, height));

        fill(0);
        textSize(120);
        textAlign(CENTER, CENTER);
        text("Game Over!", width / 2, height / 2);
        if (mousePressed) {
            appState = AppState.MAIN_SCREEN;
            waitingForMouseUp = true;
        }
    }

    private void DrawColliders() {
        ArrayList<ICollider> colliders = physicsEngine.GetColliders();
        noStroke();
        for (ICollider collider : colliders) {
            if (collider instanceof Ball) {
                Ball ball = (Ball) collider;
                DrawBall(ball);
            }
        }
    }

    private void DrawBall(Ball ball) {
        float PPI = PoolTable.PIXELS_PER_INCH;
        fill(ball.GetColor().r, ball.GetColor().g, ball.GetColor().b);
        circle(ball.GetPosition().x * PPI, ball.GetPosition().y * PPI, Ball.RADIUS * 2 * PPI);
        if (ball.GetBallNumber() != -1) {
            fill(255);
            circle(ball.GetPosition().x * PPI, ball.GetPosition().y * PPI, Ball.RADIUS * (9.75f / 10) * PPI);
            fill(0);
            textSize(12);
            textAlign(CENTER, CENTER);
            text(ball.GetBallNumber(), ball.GetPosition().x * PPI, (ball.GetPosition().y - 0.25f) * PPI);
        }
    }

    private boolean AreAllBallsAtRest() {
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

    private void UpdateCueBall() {
        if (waitingForMouseUp) {
            return;
        }
        Vector fireVector;
        if (breakShot) {
            fireVector = cue.UpdateCue(cueBall, PoolCue.MAX_CUE_SPEED * 1.75f);
        } else {
            fireVector = cue.UpdateCue(cueBall);
        }
        if (fireVector != null) {
            cueBall.SetVelocity(fireVector);
            gamePhase = GamePhase.CUE_ANIMATION;
            breakShot = false;
        }
    }

    private void PlaceBall() {
        float PPI = PoolTable.PIXELS_PER_INCH;

        boolean cueBallCollidingWithOtherBall = physicsEngine.IsBallInArea(new Vector(mouseX / PPI, mouseY / PPI),
                Ball.RADIUS);
        boolean cueBallOutOfBounds = mouseX + Ball.RADIUS * PPI > width / 2 + PoolTable.TABLE_WIDTH * PPI / 2 ||
                mouseX - Ball.RADIUS * PPI < width / 2 - PoolTable.TABLE_WIDTH * PPI / 2 ||
                mouseY + Ball.RADIUS * PPI > height / 2 + PoolTable.TABLE_HEIGHT * PPI / 2 ||
                mouseY - Ball.RADIUS * PPI < height / 2 - PoolTable.TABLE_HEIGHT * PPI / 2;

        if (cueBallCollidingWithOtherBall || cueBallOutOfBounds) {
            fill(255, 100, 100, 150);
            circle(mouseX, mouseY, Ball.RADIUS * 2 * PPI);
            return;
        }

        if (mousePressed && !waitingForMouseUp) {
            cueBall.SetPosition(new Vector(mouseX / PPI, mouseY / PPI));
            cueBall.SetVelocity(new Vector(0, 0));
            physicsEngine.AddCollider(cueBall);
            gamePhase = GamePhase.CUE_FIRING;
        }
        fill(255, 255, 255, 150);
        circle(mouseX, mouseY, PPI * Ball.RADIUS * 2);
    }

    @Override
    public void keyPressed() {
        if (key == ESC) {
            if (appState != AppState.GAME) {
                return;
            }
            if (gamePhase != GamePhase.PAUSED) {
                gamePhase = GamePhase.PAUSED;
                cue.ResetCue();
                waitingForMouseUp = true;
                key = 0;
            } else {
                appState = AppState.MAIN_SCREEN;
                key = 0;
                if (!breakShot) {
                    saveManager.SavePoolGame(cueBall, ballSetup, physicsEngine);
                } else {
                    saveManager.ClearSaveFile();
                }
            }
        }
    }

    private void DrawGamePhase() {
        GameBase();

        float deltaTime = getDeltaTime();

        switch (gamePhase) {
            case PLAYING:
                physicsEngine.UpdateColliderPositions(deltaTime);
                physicsEngine.HandleCollisions();
                physicsEngine.CheckPockets();
                physicsEngine.CheckOutOfBounds(new Vector(width, height));

                if (physicsEngine.IsBallInPocket(ballSetup.GetEightBall())) {
                    appState = AppState.GAME_OVER;
                    saveManager.ClearSaveFile();
                    break;
                }

                if (AreAllBallsAtRest()) {
                    gamePhase = GamePhase.CUE_FIRING;
                }
                break;
            case CUE_FIRING:
                if (physicsEngine.IsBallInPocket(cueBall)) {
                    gamePhase = GamePhase.PLACE_BALL;
                    break;
                }
                UpdateCueBall();
                break;
            case CUE_ANIMATION:
                if (cue.AnimateCue(deltaTime)) {
                    gamePhase = GamePhase.PLAYING;
                }
                break;
            case PLACE_BALL:
                PlaceBall();
                break;
            case PAUSED:
                fill(0);
                noStroke();
                textSize(50);
                textAlign(CENTER, CENTER);
                text("PAUSED", width / 2, height / 2 - 30);
                textSize(35);
                text("Click to Resume or esc again to quit to main menu", width / 2, height / 2 + 50);
                if (mousePressed && !waitingForMouseUp) {
                    gamePhase = GamePhase.PLAYING;
                    waitingForMouseUp = true;
                }
                break;

        }

        DrawColliders();
    }

    private void GameBase() {
        background(180);
        int tableWidth = 1400;
        int tableHeight = 700;
        image(table.GetTableImage(), width / 2 - tableWidth / 2, height / 2 - tableHeight / 2, tableWidth, tableHeight);
    }

    private void GameSetup() {
        float PPI = PoolTable.PIXELS_PER_INCH;
        cueBall = new Ball(new Vector((width / 2 + 320) / PPI, height / 2 / PPI));
        ballSetup = new BallSetup(new Vector((width / 2 - 320) / PPI, height / 2 / PPI));

        physicsEngine = new PhysicsEngine();
        physicsEngine.AddCollider(cueBall)
                .AddColliders(ballSetup.GetBalls())
                .AddColliders(table.GetWalls())
                .AddPockets(table.GetPockets());

        breakShot = true;
        appState = AppState.GAME;
        gamePhase = GamePhase.CUE_FIRING;

        previousTime = System.currentTimeMillis();
    }

    private float getDeltaTime() {
        float deltaTime = (System.currentTimeMillis() - previousTime) / 1000f * TIME_SCALE;
        previousTime = System.currentTimeMillis();
        return deltaTime;
    }

}