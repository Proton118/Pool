/**
 * An interface representing a collider in a game.
 */
public interface ICollider {
    /**
     * Updates the position of the collider.
     *
     * @param deltaTime The time elapsed since the last update, in seconds.
     */
    public default void Update(float deltaTime) {
        // Default implementation does nothing
    }
}