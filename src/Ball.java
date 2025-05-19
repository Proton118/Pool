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
     * Check if the ball is colliding with another collider
     * 
     * @param other The other collider to check for collision
     * @return CollisionData containing collision information
     */
    @Override
    public CollisionData IsCollidingWith(ICollider other) {
        switch (other.getClass().getSimpleName()) {
            case "Ball":
                Ball ball = (Ball) other;
                return HandleBallCollision(ball);
            case "Wall":
                Wall wall = (Wall) other;
                return HandleWallCollision(wall);
        }
        return new CollisionData(false);
    }

    /**
     * Handle collision with another ball
     * 
     * @param other The other ball to check for collision
     * @return CollisionData containing collision information
     */
    public CollisionData HandleBallCollision(Ball other) {
        float distance = position.subtract(other.position).magnitude();
        if (distance < radius + other.radius) {
            Vector direction = position.subtract(other.position).normalize();
            Vector pointOfContact = other.position.add(direction.multiply(other.GetRadius()));
            return new CollisionData(true, pointOfContact);
        }
        return new CollisionData(false);
    }

    /**
     * Handle collision with a wall
     * 
     * @param wall The wall to check for collision
     * @return CollisionData containing collision information
     */
    public CollisionData HandleWallCollision(Wall wall) {
        Vector circleDistance = new Vector(Math.abs(position.x - wall.GetPosition().x),
                Math.abs(position.y - wall.GetPosition().y));

        if (Math.abs(circleDistance.x) > (wall.GetWidth() / 2 + radius)) {
            return new CollisionData(false);
        }
        if (Math.abs(circleDistance.y) > (wall.GetHeight() / 2 + radius)) {
            return new CollisionData(false);
        }

        if (circleDistance.x <= wall.GetWidth() / 2) {
            int contactDirection = position.y > wall.GetPosition().y ? 1 : -1;
            Vector pointOfContact = new Vector(position.x,
                    wall.GetPosition().y + wall.GetHeight() / 2 * contactDirection);
            return new CollisionData(true, pointOfContact);
        }
        if (circleDistance.y <= wall.GetHeight() / 2) {
            int contactDirection = position.x > wall.GetPosition().x ? 1 : -1;
            Vector pointOfContact = new Vector(wall.GetPosition().x + wall.GetWidth() / 2 * contactDirection,
                    position.y);
            return new CollisionData(true, pointOfContact);
        }

        float cornerDist_sq = (circleDistance.x - wall.GetWidth() / 2)
                * (circleDistance.x - wall.GetWidth() / 2) +
                (circleDistance.y - wall.GetHeight() / 2) * (circleDistance.y - wall.GetHeight() / 2);

        if (cornerDist_sq <= (radius * radius)) {
            Vector pointOfContact = new Vector(position.x > wall.GetPosition().x ? 1 : -1,
                    position.y > wall.GetPosition().y ? 1 : -1);
            pointOfContact.x *= wall.GetWidth() / 2;
            pointOfContact.y *= wall.GetHeight() / 2;
            pointOfContact = wall.GetPosition().add(pointOfContact);
            return new CollisionData(true, pointOfContact);
        }
        return new CollisionData(false);
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
