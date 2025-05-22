import java.util.ArrayList;

import Utility.Vector;

public class PoolTable {
    public static final float PIXELS_PER_INCH = 1125 / 78;
    private ArrayList<ICollider> walls;   


    public PoolTable(float width, float height) {
        walls = new ArrayList<>();
        walls.add(new Wall(new Vector(width / 2, height / 2 + 300 + 25), 1125, 50));
        walls.add(new Wall(new Vector(width / 2, height / 2 - 300 - 25), 1125, 50));
        walls.add(new Wall(new Vector(width / 2 + 600 + 25, height / 2), 50, 500));
        walls.add(new Wall(new Vector(width / 2 - 600 - 25, height / 2), 50, 500));

    }

    public ArrayList<ICollider> GetWalls() {
        return walls;
    }
}