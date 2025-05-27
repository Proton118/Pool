import Utility.Color;
import Utility.Vector;

public class Ball implements ICollider {
    private static final float FRICTION_CONSTANT = 0.15f;
    private static final float GRAVITY = 386;
    private static final float SURFACE_TOLERANCE = 0.05f;

    private float radius;
    private Vector position;
    private Vector velocity;
    private float acceleration;
    private float mass;
    private Color color;
    private int ballNumber;
    private int previousCollisionNumber = 0;

    public Ball(Color color, int ballNumber, float radius, Vector position, float mass) {
        this.radius = radius;
        this.position = position;
        this.mass = mass;
        this.velocity = new Vector(0, 0);
        this.acceleration = 0;
        this.color = color;
        this.ballNumber = ballNumber;
    }
    public Ball(float radius, Vector position, float mass) {
        this.radius = radius;
        this.position = position;
        this.mass = mass;
        this.velocity = new Vector(0, 0);
        this.acceleration = 0;
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
        acceleration = 0;
        acceleration += GetFriction() / mass;

        Vector originalDirection = velocity.normalize();
        velocity = velocity.add(velocity.normalize().multiply(acceleration * deltaTime));
        Vector newDirection = velocity.normalize();
        if(!originalDirection.directionOf(newDirection)){
            velocity = new Vector(0, 0);
        }

        position = position.add(velocity.multiply(deltaTime));
    }

    /**
     * Get the friction force acting on the ball
     * 
     * @return The friction force
     */
    public float GetFriction() {
        return FRICTION_CONSTANT * mass * -GRAVITY;
    }

    /**
     * Handle collision with another collider
     * 
     * @param other          The other collider to handle collision with
     * @param pointOfContact The point of contact between the two colliders
     */
    @Override
    public void OnCollision(ICollider other, Vector pointOfContact) {
        MoveToSurface(pointOfContact);
        // Bounce(pointOfContact);
    }

    /**
     * Move the ball to the surface of the collider it is colliding with
     * 
     * @param pointOfContact The point of contact between the two colliders
     */
    public void MoveToSurface(Vector pointOfContact) {
        Vector direction = position.subtract(pointOfContact).normalize();
        System.out.println("Position: " + position + ", Point of Contact: " + pointOfContact + ", Direction: " + direction);
        position = pointOfContact.add(direction.multiply(radius + SURFACE_TOLERANCE));
    }

    public void MoveToSurfaceWall(Vector pointOfContact, Vector wallNormal) {
        Vector offset = wallNormal.multiply(-radius + SURFACE_TOLERANCE);
        position = pointOfContact.add(offset);
    }

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
    public ICollider SetVelocity(float velocity) {
        this.velocity = this.velocity.normalize().multiply(velocity);
        return this;
    }
    public Vector GetVelocity() {
        return velocity;
    }

    /**
     * The radius of the ball
     * 
     * @return The radius of the ball
     */
    public float GetRadius() {
        return radius;
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

    public float GetMass() {
        return mass;
    }

    public void SetPreviousCollisionNumber(int previousCollisionNumber) {
        this.previousCollisionNumber = previousCollisionNumber;
    }
    public int GetPreviousCollisionNumber() {
        return previousCollisionNumber;
    }
}
