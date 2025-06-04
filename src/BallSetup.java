import java.util.ArrayList;

import Utility.Color;
import Utility.Vector;

/**
 * A class representing the colored balls in a pool game.
 */
public class BallSetup {
    private static final float MIN_OFFSET = 0.26f;
    private static final Color C1 = new Color(230, 230, 20);
    private static final Color C2 = new Color(230, 20, 20);
    private static final int[] NUMBER_ORDER = {1, 11, 5, 2, 8, 10, 9, 7, 14, 4, 6, 15, 13, 3, 12};
    private static final Color[] COLOR_ORDER = {C1, C2, C1, C1, Color.BLACK, C2, C2, C1, C2, C1, C1, C2, C2, C1, C2};

    private ArrayList<ICollider> balls;
    private Ball eightBall;

    /**
     * Constructs a BallSetup with balls arranged in a triangle formation.
     * 
     * @param position The position of the triangle's top vertex
     */
    public BallSetup(Vector position) {
        balls = new ArrayList<>();

        int currentNumber = 0;
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < i + 1; j++){
                float xOffset = (i * Ball.RADIUS * 2 + MIN_OFFSET * i);
                float yOffset = -i*(Ball.RADIUS + MIN_OFFSET) + j * (Ball.RADIUS + MIN_OFFSET) * 2;
                Vector ballPosition = new Vector(position.x - xOffset, position.y + yOffset);
                balls.add(new Ball(COLOR_ORDER[currentNumber], NUMBER_ORDER[currentNumber], ballPosition));
                currentNumber++;
            }
        }

        eightBall = (Ball)balls.get(4);
    }

    /**
     * Returns the list of balls in the setup.
     * 
     * @return an ArrayList of ICollider representing the balls
     */
    public ArrayList<ICollider> GetBalls() {
        return balls;
    }

    /**
     * Returns the eight ball in the setup.
     * 
     * @return the Ball representing the eight ball
     */
    public Ball GetEightBall() {
        return eightBall;
    }
}
