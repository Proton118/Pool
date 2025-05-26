import java.util.ArrayList;

import Utility.Color;
import Utility.Vector;

public class BallSetup {
    private static final float BALL_RADIUS = 2.25f;
    private static final float BALL_MASS = 0.17f;
    private static final float MIN_OFFSET = 0.25f;

    private ArrayList<ICollider> balls;

    private int[] numberOrder = {1, 11, 5, 2, 8, 10, 9, 7, 14, 4, 6, 15, 13, 3, 12};
    
    public BallSetup(Vector position) {
        balls = new ArrayList<>();

        int currentNumber = 0;
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < i + 1; j++){
                float xOffset = (i * BALL_RADIUS * 2 + MIN_OFFSET);
                float yOffset = -i*BALL_RADIUS + j * BALL_RADIUS * 2 + MIN_OFFSET;
                Vector ballPosition = new Vector(position.x - xOffset, position.y + yOffset);
                balls.add(new Ball(Color.BLACK, numberOrder[currentNumber],BALL_RADIUS, ballPosition, BALL_MASS));
                currentNumber++;
            }
        }
    }

    public ArrayList<ICollider> GetBalls() {
        return balls;
    }
}
