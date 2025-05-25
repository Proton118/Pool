import Utility.Vector;
import processing.core.PApplet;

public class PoolCue {
    private static final float MAX_CUE_LENGTH = 200;
    private static final float MAX_CUE_SPEED = 300;

    private PApplet app;
    private Vector startPosition;
    private boolean isActive = false;
    private float cueMagnitude = 0;

    public PoolCue(PApplet app) {
        this.app = app;
        startPosition = new Vector(0, 0);
    }

    public Vector UpdateCue(Ball cueBall) {
        // app.noCursor();

        float PPI = PoolTable.PIXELS_PER_INCH;
        Vector cueBallPosition = cueBall.GetPosition().multiply(PPI);
        Vector mousePosition = new Vector(app.mouseX, app.mouseY);
        Vector cueDirection = mousePosition.subtract(cueBallPosition).normalize();
        Vector startCueDirection = startPosition.subtract(cueBallPosition).normalize();

        app.fill(255);
        app.noStroke();
        app.circle(startPosition.x, startPosition.y, 10);

        if (app.mousePressed) {
            isActive = true;

            Vector mouseVector = mousePosition.subtract(cueBallPosition);
            float projectionLength = mouseVector.dot(startCueDirection);

            Vector cueEndPosition = cueBallPosition.add(startCueDirection.multiply(projectionLength));
            cueMagnitude = Math.abs(projectionLength);

            if (cueMagnitude > MAX_CUE_LENGTH) {
                cueMagnitude = MAX_CUE_LENGTH;
                cueEndPosition = cueBallPosition.add(startCueDirection.multiply(MAX_CUE_LENGTH));
            }

            float actualCueLength = mousePosition.subtract(startPosition).dot(startCueDirection);
            if (actualCueLength < 0 || projectionLength < 0) {
                cueMagnitude = 0;
                return null;
            }

            app.stroke(255);
            app.line(startPosition.x, startPosition.y, cueEndPosition.x, cueEndPosition.y);
        } else {
            if (isActive) {
                isActive = false;
                startPosition = new Vector(-100, -100);
                return cueMagnitude > 0 ? startCueDirection.multiply(-cueMagnitude / MAX_CUE_LENGTH * MAX_CUE_SPEED) : null;
            }

            startPosition = cueBallPosition.add(cueDirection.multiply(10 + cueBall.GetRadius() * PPI));
        }

        return null;
    }

}