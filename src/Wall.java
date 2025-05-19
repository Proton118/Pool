public class Wall implements ICollider{
    private Vector position;
    private float width;
    private float height;

    public Wall(Vector position, float width, float height) {
        this.position = position;
        this.width = width;
        this.height = height;
    }

    @Override
    public CollisionData IsCollidingWith(ICollider other) {
        switch(other.getClass().getSimpleName()){
            case "Ball":
                Ball ball = (Ball) other;
                return HandleWallCollision(ball);
        }
        return new CollisionData(false);
    }

    /**
     * Handle collision with a wall
     * 
     * @param wall The wall to check for collision
     * @return CollisionData containing collision information
     */
    public CollisionData HandleWallCollision(Ball ball) {
        Vector circleDistance = new Vector(Math.abs(ball.GetPosition().x - position.x),
                Math.abs(ball.GetPosition().y - position.y));

        if (Math.abs(circleDistance.x) > (width / 2 + ball.GetRadius())) {
            return new CollisionData(false);
        }
        if (Math.abs(circleDistance.y) > (height / 2 + ball.GetRadius())) {
            return new CollisionData(false);
        }

        if (circleDistance.x <= width / 2) {
            int contactDirection = ball.GetPosition().y > position.y ? 1 : -1;
            Vector pointOfContact = new Vector(ball.GetPosition().x,
                    position.y + height / 2 * contactDirection);
            return new CollisionData(true, pointOfContact);
        }
        if (circleDistance.y <= height / 2) {
            int contactDirection = ball.GetPosition().x > position.x ? 1 : -1;
            Vector pointOfContact = new Vector(position.x + width / 2 * contactDirection,
                    ball.GetPosition().y);
            return new CollisionData(true, pointOfContact);
        }

        float cornerDist_sq = (circleDistance.x - width / 2)
                * (circleDistance.x - width / 2) +
                (circleDistance.y - height / 2) * (circleDistance.y - height / 2);

        if (cornerDist_sq <= (ball.GetRadius() * ball.GetRadius())) {
            Vector pointOfContact = new Vector(ball.GetPosition().x > position.x ? 1 : -1,
                    ball.GetPosition().y > position.y ? 1 : -1);
            pointOfContact.x *= width / 2;
            pointOfContact.y *= height / 2;
            pointOfContact = position.add(pointOfContact);
            return new CollisionData(true, pointOfContact);
        }
        return new CollisionData(false);
    }

    public Vector GetPosition() {
        return position;
    }
    public float GetWidth() {
        return width;
    }
    public float GetHeight() {
        return height;
    }
}
