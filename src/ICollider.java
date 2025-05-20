import Utility.Vector;

public interface ICollider {
    public default void OnCollision(ICollider other, Vector pointOfContact){
        // Default implementation does nothing
    }
    public default void Update(float deltaTime){
        // Default implementation does nothing
    }
}
