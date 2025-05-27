package Utility;

public class CollisionData {
    private boolean collided;
    private Vector pointOfContact;
    private Vector wallNormal;

    public CollisionData(boolean collided, Vector pointOfContact, Vector wallNormal){
        this.collided = collided;
        this.pointOfContact = pointOfContact;
        this.wallNormal = wallNormal;
    }
    
    public CollisionData(boolean collided, Vector pointOfContact){
        this.collided = collided;
        this.pointOfContact = pointOfContact;
        this.wallNormal = null;
    }
    public CollisionData(boolean collided){
        this.collided = collided;
        this.pointOfContact = new Vector();
        this.wallNormal = null;
    }
    
    public boolean GetCollided(){
        return collided;
    }
    public Vector GetPointOfContact(){
        return pointOfContact;
    } 

    public Vector GetWallNormal(){
        return wallNormal;
    }
}