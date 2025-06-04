import Utility.Vector;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * A class representing a pool cue.
 */
public class PoolCue {
    private static final int MAX_CUE_LENGTH = 250;
    public static final float MAX_CUE_SPEED = 600;
    private static final float CUE_ANIMATION_SPEED = 200;

    private PApplet app;
    private boolean isActive = false;
    private PImage cueImage;

    private Vector cueStartPosition;
    private Vector cueDirection;
    private float cueDistance;
    private float cueSpeed;

    /**
     * Constructs a PoolCue.
     *
     * @param app The PApplet instance for rendering
     */
    public PoolCue(PApplet app) {
        this.app = app;
        cueImage = app.loadImage("images/Pool Cue.png");
        float cueRatio = cueImage.width / cueImage.height;
        cueImage.resize(MAX_CUE_LENGTH, (int) (MAX_CUE_LENGTH / cueRatio));
    }

    private void DrawCue(Vector startPosition, Vector cueDirection, float cueDistance) {
        app.stroke(255);
        app.line(startPosition.x, startPosition.y, startPosition.x + -cueDirection.x * cueDistance,
                startPosition.y + -cueDirection.y * cueDistance);

        Vector cueStartPosition = new Vector(startPosition.x - cueDirection.x * cueDistance,
                startPosition.y - cueDirection.y * cueDistance);

        app.pushMatrix();
        app.translate(cueStartPosition.x, cueStartPosition.y);
        app.rotate(cueDirection.angle());
        app.image(cueImage, -cueImage.width, -cueImage.height / 2);
        app.popMatrix();
    }

    /**
     * Updates the cue based on the position of the cue ball and mouse input.
     *
     * @param cueBall  The cue ball to aim at
     * @param maxSpeed The maximum speed of the cue
     * @return A vector representing the direction and speed of the cue, or null if
     *         cue not released
     */
    public Vector UpdateCue(Ball cueBall, float maxSpeed) {
        float PPI = PoolTable.PIXELS_PER_INCH;

        Vector cueBallPosition = cueBall.GetPosition().multiply(PPI);
        Vector cueBallToMouseDirection = cueBallPosition.subtract(new Vector(app.mouseX, app.mouseY)).normalize();
        cueStartPosition = cueBallPosition.subtract(cueBallToMouseDirection.multiply(Ball.RADIUS * PPI + 10));

        app.fill(255);
        app.circle(cueStartPosition.x, cueStartPosition.y, 10);

        if (!isActive && !app.mousePressed) {
            return null;
        }

        Vector mousePosition = new Vector(app.mouseX, app.mouseY);
        cueDirection = cueStartPosition.subtract(mousePosition).normalize();
        cueDistance = mousePosition.distance(cueStartPosition);

        if (cueDistance > MAX_CUE_LENGTH) {
            cueDistance = MAX_CUE_LENGTH;
        }

        boolean isMouseOverCueBall = !cueDirection.equals(cueBallToMouseDirection, 0.01f);
        if (isMouseOverCueBall) {
            isActive = false;
            return null;
        }

        if (!app.mousePressed) {
            isActive = false;
            cueSpeed = cueDistance / MAX_CUE_LENGTH * maxSpeed;
            return cueDirection.multiply(cueSpeed);
        } else {
            isActive = true;
        }

        app.stroke(255);
        app.line(cueStartPosition.x, cueStartPosition.y, cueStartPosition.x + -cueDirection.x * cueDistance,
                cueStartPosition.y + -cueDirection.y * cueDistance);

        DrawCue(cueStartPosition, cueDirection, cueDistance);

        return null;
    }

    /**
     * Updates the cue based on the position of the cue ball and the mouse input,
     * using the default maximum cue speed.
     *
     * @param cueBall The cue ball to aim at
     * @return A vector representing the direction and speed of the cue, or null if
     *         cue not released
     */
    public Vector UpdateCue(Ball cueBall) {
        return UpdateCue(cueBall, MAX_CUE_SPEED);
    }

    /**
     * Animates the cue towards the cue ball.
     * 
     * @param deltaTime The time since the last frame in seconds
     * @return true if the animation is complete, false otherwise
     */
    public boolean AnimateCue(float deltaTime) {
        cueDistance -= CUE_ANIMATION_SPEED * cueSpeed / PoolTable.PIXELS_PER_INCH * deltaTime;
        if (cueDistance <= 0) {
            cueDistance = 0;
            return true;
        }
        DrawCue(cueStartPosition, cueDirection, cueDistance);
        return false;
    }

    /**
     * Makes the cue inactive
     */
    public void ResetCue() {
        isActive = false;
    }
}