public interface ICollider {
    public abstract CollisionData IsCollidingWith(ICollider other);
    public default void OnCollision(ICollider other, Vector pointOfContact){
        // Default implementation does nothing
    }
    public default void Update(float deltaTime){
        // Default implementation does nothing
    }
}
