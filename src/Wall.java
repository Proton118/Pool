import Utility.Vector;

/**
 * A class representing a wall in a 2D space.
 */
public class Wall implements ICollider {
    private Vector position;
    private float width;
    private float height;

    /**
     * Constructs a Wall.
     * 
     * @param position The center of the wall
     * @param width    The width of the wall
     * @param height   The height of the wall
     */
    public Wall(Vector position, float width, float height) {
        this.position = position;
        this.width = width;
        this.height = height;
    }

    /**
     * @return the center of the wall
     */
    public Vector GetPosition() {
        return position;
    }

    /**
     * @return the width of the wall
     */
    public float GetWidth() {
        return width;
    }

    /**
     * @return the height of the wall
     */
    public float GetHeight() {
        return height;
    }
}
