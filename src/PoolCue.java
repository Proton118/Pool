import Utility.Vector;
import processing.core.PApplet;
import processing.core.PImage;

public class PoolCue {
    private static final int MAX_CUE_LENGTH = 250;
    public static final float MAX_CUE_SPEED = 350;
    public static final float CUE_ANIMATION_SPEED = 0.5f;

    private PApplet app;
    private boolean isActive = false;
    private PImage cueImage;

    private Vector startPosition;
    private Vector cueDirection;
    private float cueDistance;

    public PoolCue(PApplet app) {
        this.app = app;
        cueImage = app.loadImage("images/Pool Cue.png");
        float cueRatio = cueImage.width / cueImage.height;
        cueImage.resize(MAX_CUE_LENGTH, (int) (MAX_CUE_LENGTH / cueRatio));
    }

    public Vector UpdateCue(Ball cueBall, float maxSpeed) {
        float PPI = PoolTable.PIXELS_PER_INCH;

        Vector cuePosition = cueBall.GetPosition().multiply(PPI);
        Vector cueMouseDirection = cuePosition.subtract(new Vector(app.mouseX, app.mouseY)).normalize();
        startPosition = cuePosition.subtract(cueMouseDirection.multiply(cueBall.GetRadius() * PPI + 10));

        app.fill(255);
        app.circle(startPosition.x, startPosition.y, 10);

        if(!isActive && !app.mousePressed) {
            return null;
        }

        Vector mousePosition = new Vector(app.mouseX, app.mouseY);
        Vector cueDirection = startPosition.subtract(mousePosition).normalize();
        cueDistance = mousePosition.distance(startPosition);

        if (cueDistance > MAX_CUE_LENGTH) {
            cueDistance = MAX_CUE_LENGTH;
        }

        float speed = cueDistance / MAX_CUE_LENGTH * maxSpeed;

        if(!cueDirection.equals(cueMouseDirection, 0.01f)){
            isActive = false;
            return null;
        }

        if(!app.mousePressed){
            isActive = false;
            return cueDirection.multiply(speed);
        } else {
            isActive = true;
        }

        app.stroke(255);
        app.line(startPosition.x, startPosition.y, startPosition.x + -cueDirection.x * cueDistance, startPosition.y + -cueDirection.y * cueDistance);

        DrawCue(startPosition, cueDirection, cueDistance);


        return null;
    }
    public Vector UpdateCue(Ball cueBall) {
        return UpdateCue(cueBall, MAX_CUE_SPEED);
    }

    public boolean AnimateCue(float deltaTime) {
        cueDistance -= CUE_ANIMATION_SPEED * deltaTime;
        if(cueDistance <= 0) {
            cueDistance = 0;
            return true;
        }
        DrawCue(startPosition, cueDirection, cueDistance);
        return false;
    }

    private void DrawCue(Vector startPosition, Vector cueDirection, float cueDistance) {
        app.stroke(255);
        app.line(startPosition.x, startPosition.y, startPosition.x + -cueDirection.x * cueDistance, startPosition.y + -cueDirection.y * cueDistance);

        Vector cueStartPosition = new Vector(startPosition.x - cueDirection.x * cueDistance, startPosition.y - cueDirection.y * cueDistance);

        app.pushMatrix();
        app.translate(cueStartPosition.x, cueStartPosition.y);
        app.rotate(cueDirection.angle());
        app.image(cueImage, -cueImage.width, -cueImage.height / 2);
        app.popMatrix();
    }

}