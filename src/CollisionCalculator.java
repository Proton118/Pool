import Utility.CollisionData;
import Utility.Vector;

/**
 * A static class that calculates collisions between balls and walls.
 */
public final class CollisionCalculator {
    /**
     * Prevents instantiation of this utility class making it static.
     */
    private CollisionCalculator() {
    }

    private static boolean AreBallsColliding(Ball a, Ball b) {
        float distance = a.GetPosition().distance(b.GetPosition());
        return distance <= (Ball.RADIUS + Ball.RADIUS);
    }

    private static Vector BallPointOfContact(Ball a, Ball b) {
        Vector direction = a.GetPosition().subtract(b.GetPosition()).normalize();
        Vector pointOfContact = b.GetPosition().add(direction.multiply(Ball.RADIUS));

        return pointOfContact;
    }

    /**
     * Calculates the collision data between two balls.
     *
     * @param a The first ball.
     * @param b The second ball.
     * @return CollisionData containing collision status and point of contact if
     *         colliding.
     */
    public static CollisionData CalculateBallCollision(Ball a, Ball b) {
        if (AreBallsColliding(a, b)) {
            Vector pointOfContact = BallPointOfContact(a, b);
            return new CollisionData(true, pointOfContact);
        }
        return new CollisionData(false);
    }

    /**
     * Calculates the collision data between a ball and a wall.
     *
     * @param ball The ball involved in the collision.
     * @param wall The wall involved in the collision.
     * @return CollisionData containing collision status and point of contact if
     *         colliding.
     */
    public static CollisionData CalculateWallCollision(Ball ball, Wall wall) {
        Vector circleDistance = new Vector(Math.abs(ball.GetPosition().x - wall.GetPosition().x),
                Math.abs(ball.GetPosition().y - wall.GetPosition().y));

        boolean tooFarX = circleDistance.x > (wall.GetWidth() / 2 + Ball.RADIUS);
        boolean tooFarY = circleDistance.y > (wall.GetHeight() / 2 + Ball.RADIUS);
        if (tooFarX || tooFarY) {
            return new CollisionData(false);
        }

        if (circleDistance.x <= wall.GetWidth() / 2) {
            int contactDirection = ball.GetPosition().y > wall.GetPosition().y ? 1 : -1;
            Vector pointOfContact = new Vector(ball.GetPosition().x,
                    wall.GetPosition().y + wall.GetHeight() / 2 * contactDirection);
            return new CollisionData(true, pointOfContact);
        }

        if (circleDistance.y <= wall.GetHeight() / 2) {
            int contactDirection = ball.GetPosition().x > wall.GetPosition().x ? 1 : -1;
            Vector pointOfContact = new Vector(wall.GetPosition().x + wall.GetWidth() / 2 * contactDirection,
                    ball.GetPosition().y);
            return new CollisionData(true, pointOfContact);
        }

        float cornerDist_sq = (circleDistance.x - wall.GetWidth() / 2)
                * (circleDistance.x - wall.GetWidth() / 2) +
                (circleDistance.y - wall.GetHeight() / 2) * (circleDistance.y - wall.GetHeight() / 2);

        if (cornerDist_sq <= (Ball.RADIUS * Ball.RADIUS)) {
            Vector pointOfContact = new Vector(ball.GetPosition().x > wall.GetPosition().x ? 1 : -1,
                    ball.GetPosition().y > wall.GetPosition().y ? 1 : -1);
            pointOfContact.x *= wall.GetWidth() / 2;
            pointOfContact.y *= wall.GetHeight() / 2;
            pointOfContact = wall.GetPosition().add(pointOfContact);
            return new CollisionData(true, pointOfContact);
        }
        return new CollisionData(false);
    }
}
