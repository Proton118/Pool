public class Ball implements ICollider {
    private float radius;
    private Vector position;
    private float velocity;
    private Vector velocityDirection;
    private float acceleration;
    private float frictionConstant;
    private float mass;

    public Ball(float radius, Vector position, float mass, float frictionConstant) {
        this.radius = radius;
        this.position = position;
        this.mass = mass;
        this.velocity = 0;
        this.velocityDirection = new Vector(0, 0);
        this.acceleration = 0;
        this.frictionConstant = frictionConstant;
    }

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

    public CollisionData HandleBallCollision(Ball other) {
        float distance = position.subtract(other.position).magnitude();
        if (distance < radius + other.radius) {
            Vector direction = position.subtract(other.position).normalize();
            Vector pointOfContact = other.position.add(direction.multiply(radius));
            return new CollisionData(true, pointOfContact);
        }
        return new CollisionData(false);
    }

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
            Vector direction = position.subtract(wall.GetPosition()).normalize();
            Vector pointOfContact = wall.GetPosition().add(direction.multiply(radius));
            return new CollisionData(true, pointOfContact);
        }
        return new CollisionData(false);
    }

    public float GetFriction(float deltaTime) {
        return frictionConstant * mass * -9.81f;
    }

    @Override
    public void OnCollision(ICollider other, Vector pointOfContact) {
        MoveToSurface(pointOfContact);
        Bounce(other, pointOfContact);
    }

    public void MoveToSurface(Vector pointOfContact) {
        Vector direction = position.subtract(pointOfContact).normalize();
        position = pointOfContact.add(direction.multiply(radius));
    }

    public void Bounce(ICollider other, Vector pointOfContact) {
        switch (other.getClass().getSimpleName()) {
            case "Ball":
                Ball ball = (Ball) other;
                Vector reflectionVector = ball.position.subtract(pointOfContact).normalize();
                Vector projectionVector = reflectionVector.multiply((velocityDirection.dot(reflectionVector))
                        /
                        (reflectionVector.dot(reflectionVector)));
                Vector newVelocityDirection = projectionVector.multiply(2).subtract(velocityDirection).multiply(-1);
                velocityDirection = newVelocityDirection;
                break;
        }
    }

    public ICollider SetVelocity(Vector velocity) {
        this.velocity = velocity.magnitude();
        this.velocityDirection = velocity.normalize();
        return this;
    }

    public float GetRadius() {
        return radius;
    }

    public Vector GetPosition() {
        return position;
    }
}
