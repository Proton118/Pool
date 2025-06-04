import Utility.CollisionData;
import Utility.Vector;

public final class CollisionCalculator {
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

    public static CollisionData CalculateBallCollision(Ball a, Ball b) {
        if (AreBallsColliding(a, b)) {
            Vector pointOfContact = BallPointOfContact(a, b);
            return new CollisionData(true, pointOfContact);
        }
        return new CollisionData(false);
    }

    public static CollisionData CalculateWallCollision(Ball ball, Wall wall) {
        Vector circleDistance = new Vector(Math.abs(ball.GetPosition().x - wall.GetPosition().x),
                Math.abs(ball.GetPosition().y - wall.GetPosition().y));

        if (Math.abs(circleDistance.x) > (wall.GetWidth() / 2 + Ball.RADIUS)) {
            return new CollisionData(false);
        }
        if (Math.abs(circleDistance.y) > (wall.GetHeight() / 2 + Ball.RADIUS)) {
            return new CollisionData(false);
        }

        if (circleDistance.x <= wall.GetWidth() / 2) {
            int contactDirection = ball.GetPosition().y > wall.GetPosition().y ? 1 : -1;
            Vector pointOfContact = new Vector(ball.GetPosition().x,
                    wall.GetPosition().y + wall.GetHeight() / 2 * contactDirection);
            Vector wallNormal = new Vector(0, -contactDirection);
            return new CollisionData(true, pointOfContact, wallNormal);
        }
        if (circleDistance.y <= wall.GetHeight() / 2) {
            int contactDirection = ball.GetPosition().x > wall.GetPosition().x ? 1 : -1;
            Vector pointOfContact = new Vector(wall.GetPosition().x + wall.GetWidth() / 2 * contactDirection,
                    ball.GetPosition().y);
            Vector wallNormal = new Vector(-contactDirection, 0); 
            return new CollisionData(true, pointOfContact, wallNormal);
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
            return new CollisionData(true, pointOfContact, null);
        }
        return new CollisionData(false);
    }
}
