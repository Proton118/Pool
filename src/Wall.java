import Utility.Vector;

public class Wall implements ICollider{
    private Vector position;
    private float width;
    private float height;

    public Wall(Vector position, float width, float height) {
        this.position = position;
        this.width = width;
        this.height = height;
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
