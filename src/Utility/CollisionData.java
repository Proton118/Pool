package Utility;

public class CollisionData {
    private boolean collided;
    private Vector pointOfContact;
    
    public CollisionData(boolean collided, Vector pointOfContact){
        this.collided = collided;
        this.pointOfContact = pointOfContact;
    }
    public CollisionData(boolean collided){
        this.collided = collided;
        this.pointOfContact = new Vector();
    }
    
    public boolean GetCollided(){
        return collided;
    }
    public Vector GetPointOfContact(){
        return pointOfContact;
    } 
}