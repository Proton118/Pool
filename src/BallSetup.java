import java.util.ArrayList;

import Utility.Color;
import Utility.Vector;

public class BallSetup {
    private static final float BALL_MASS = 0.17f;
    private static final float MIN_OFFSET = 0.26f;
    private static final Color C1 = new Color(230, 230, 20);
    private static final Color C2 = new Color(230, 20, 20);

    private ArrayList<ICollider> balls;
    private Ball eightBall;

    private int[] numberOrder = {1, 11, 5, 2, 8, 10, 9, 7, 14, 4, 6, 15, 13, 3, 12};
    private Color[] colorOrder = {C1, C2, C1, C1, Color.BLACK, C2, C2, C1, C2, C1, C1, C2, C2, C1, C2};
    
    public BallSetup(Vector position) {
        balls = new ArrayList<>();

        int currentNumber = 0;
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < i + 1; j++){
                float xOffset = (i * Ball.RADIUS * 2 + MIN_OFFSET * i);
                float yOffset = -i*(Ball.RADIUS + MIN_OFFSET) + j * (Ball.RADIUS + MIN_OFFSET) * 2;
                Vector ballPosition = new Vector(position.x - xOffset, position.y + yOffset);
                balls.add(new Ball(colorOrder[currentNumber], numberOrder[currentNumber], ballPosition, BALL_MASS));
                currentNumber++;
            }
        }
        eightBall = (Ball)balls.get(4);
    }

    public ArrayList<ICollider> GetBalls() {
        return balls;
    }
    public Ball GetEightBall() {
        return eightBall;
    }
}
