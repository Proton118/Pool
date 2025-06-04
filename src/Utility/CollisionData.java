package Utility;

/**
 * A class representing the data of a collision, whether or not it really
 * occurred and the point of contact.
 */
public class CollisionData {
    private boolean collided;
    private Vector pointOfContact;

    /**
     * Constructs a CollisionData
     * 
     * @param collided       Indicates if a collision occurred
     * @param pointOfContact The point where the collision occurred
     */
    public CollisionData(boolean collided, Vector pointOfContact) {
        this.collided = collided;
        this.pointOfContact = pointOfContact;
    }

    /**
     * Constructs a CollisionData with no collision
     * 
     * @param collided Indicates if a collision occurred
     */
    public CollisionData(boolean collided) {
        this.collided = collided;
        this.pointOfContact = new Vector();
    }

    /**
     * @return whether or not a collision occurred
     */
    public boolean GetCollided() {
        return collided;
    }

    /**
     * @return the point of contact of the collision
     */
    public Vector GetPointOfContact() {
        return pointOfContact;
    }
}