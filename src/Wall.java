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
