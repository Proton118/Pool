import java.util.ArrayList;

public class PhysicsEngine {
    private ArrayList<ICollider> colliders;

    public PhysicsEngine() {
        this.colliders = new ArrayList<>();   
    }
    
    public PhysicsEngine AddCollider(ICollider collider) {
        colliders.add(collider);
        return this;
    }
}
