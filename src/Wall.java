import Utility.Vector;

public class Wall implements ICollider{
    private Vector position;
    private float width;
    private float height;
    private int wallNumber;

    public Wall(Vector position, float width, float height, int wallNumber) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.wallNumber = wallNumber;
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
    public int GetWallNumber() {
        return wallNumber;
    }
}
