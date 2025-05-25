import Utility.Vector;

public class TablePocket implements ICollider {
    private Vector position;
    private float radius;

    public TablePocket(Vector position, float radius) {
        this.position = position;
        this.radius = radius;
    }

    public Vector GetPosition() {
        return position;
    }

    public float GetRadius() {
        return radius;
    }
}
