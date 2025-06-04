import Utility.Vector;

/**
 * A class representing a pocket on a pool table.
 */
public class TablePocket implements ICollider {
    private Vector position;
    private float radius;

    /**
     * Constructs a TablePocket.
     * 
     * @param position The position of the pocket
     * @param radius   The radius of the pocket
     */
    public TablePocket(Vector position, float radius) {
        this.position = position;
        this.radius = radius;
    }

    /**
     * @return the position of the pocket
     */
    public Vector GetPosition() {
        return position;
    }

    /**
     * @return the radius of the pocket
     */
    public float GetRadius() {
        return radius;
    }
}
