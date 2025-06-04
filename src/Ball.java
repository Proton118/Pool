import Utility.Color;
import Utility.Vector;

/**
 * A class representing a ball in a pool game.
 */
public class Ball implements ICollider {
    public static final float RADIUS = 2.25f;
    public static final float MASS = 0.17f;

    private static final float FRICTION_CONSTANT = 0.14f;
    private static final float GRAVITY = 386;

    private Vector position;
    private Vector velocity;
    private Color color;
    private int ballNumber;

    /**
     * Constructs a Ball.
     * 
     * @param color      The color of the ball
     * @param ballNumber The number of the ball
     * @param position   the position to start the ball at
     */
    public Ball(Color color, int ballNumber, Vector position) {
        this.position = position;
        this.velocity = new Vector(0, 0);
        this.color = color;
        this.ballNumber = ballNumber;
    }

    /**
     * Constructs a white cue ball with no number.
     * 
     * @param position the position to start the ball at
     */
    public Ball(Vector position) {
        this.position = position;
        this.velocity = new Vector(0, 0);
        this.color = Color.WHITE;
        this.ballNumber = -1;
    }

    /**
     * Update the ball's position and velocity based on the current acceleration
     * 
     * @param deltaTime The time since the last update
     */
    @Override
    public void Update(float deltaTime) {
        float acceleration = GRAVITY * FRICTION_CONSTANT * -1;

        Vector originalDirection = velocity.normalize();
        velocity = velocity.add(velocity.normalize().multiply(acceleration * deltaTime));
        Vector newDirection = velocity.normalize();
        if (!originalDirection.directionOf(newDirection)) {
            velocity = new Vector(0, 0);
        }

        position = position.add(velocity.multiply(deltaTime));
    }

    /**
     * Set the position of the ball
     * 
     * @param position the position to set the ball to
     */
    public void SetPosition(Vector position) {
        this.position = position;
    }

    /**
     * Set the velocity of the ball
     * 
     * @param velocity The new velocity vector
     * @return The current instance of the ball
     */
    public ICollider SetVelocity(Vector velocity) {
        this.velocity = velocity;
        return this;
    }

    /**
     * Get the velocity of the ball
     * 
     * @return The current velocity vector of the ball
     */
    public Vector GetVelocity() {
        return velocity;
    }

    /**
     * The position of the ball
     * 
     * @return The position of the ball
     */
    public Vector GetPosition() {
        return position;
    }

    /**
     * The color of the ball
     * 
     * @return The color of the ball
     */
    public Color GetColor() {
        return color;
    }

    /**
     * The Number of the ball
     * 
     * @return The number of the ball
     */
    public int GetBallNumber() {
        return ballNumber;
    }
}
