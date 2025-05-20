import Utility.Color;
import Utility.Vector;

public class Ball implements ICollider {
    private float radius;
    private Vector position;
    private float velocity;
    private Vector velocityDirection;
    private float acceleration;
    private float frictionConstant;
    private float mass;
    private Color color;
    private int ballNumber;

    public Ball(Color color, int ballNumber, float radius, Vector position, float mass, float frictionConstant) {
        this.radius = radius;
        this.position = position;
        this.mass = mass;
        this.velocity = 0;
        this.velocityDirection = new Vector(0, 0);
        this.acceleration = 0;
        this.frictionConstant = frictionConstant;
        this.color = color;
        this.ballNumber = ballNumber;
    }
    public Ball(float radius, Vector position, float mass, float frictionConstant) {
        this.radius = radius;
        this.position = position;
        this.mass = mass;
        this.velocity = 0;
        this.velocityDirection = new Vector(0, 0);
        this.acceleration = 0;
        this.frictionConstant = frictionConstant;
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
        acceleration += GetFriction(deltaTime) / mass;

        velocity += acceleration * deltaTime;
        if (velocity < 0) {
            velocity = 0;
        }

        position = position.add(velocityDirection.multiply(velocity * deltaTime));
    }

    /**
     * Get the friction force acting on the ball
     * 
     * @param deltaTime The time since the last update
     * @return The friction force
     */
    public float GetFriction(float deltaTime) {
        return frictionConstant * mass * -9.81f;
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
        position = pointOfContact.add(direction.multiply(radius));
    }

    /**
     * Bounce the ball off the collider it is colliding with
     * 
     * @param other          The other collider to bounce off of
     * @param pointOfContact The point of contact between the two colliders
     */
    public void Bounce(Vector pointOfContact) {
        Vector reflectionVector = position.subtract(pointOfContact).normalize();
        Vector projectionVector = reflectionVector.multiply((velocityDirection.dot(reflectionVector))
                /
                (reflectionVector.dot(reflectionVector)));
        Vector newVelocityDirection = projectionVector.multiply(2).subtract(velocityDirection).multiply(-1);
        velocityDirection = newVelocityDirection;
    }

    /**
     * Set the velocity of the ball
     * 
     * @param velocity The new velocity vector
     * @return The current instance of the ball
     */
    public ICollider SetVelocity(Vector velocity) {
        this.velocity = velocity.magnitude();
        this.velocityDirection = velocity.normalize();
        return this;
    }
    public ICollider SetVelocity(float velocity) {
        this.velocity = velocity;
        if(velocity < 0){
            this.velocity = -velocity;
            this.velocityDirection = velocityDirection.multiply(-1);
        }
        return this;
    }
    public void SetVelocityDirection(Vector velocityDirection) {
        this.velocityDirection = velocityDirection;
    }
    public Vector GetVelocityDirection() {
        return velocityDirection;
    }
    public float GetVelocity() {
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
}
