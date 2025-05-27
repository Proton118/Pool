import java.util.ArrayList;

import Utility.Vector;
import processing.core.PApplet;
import processing.core.PImage;

public class PoolTable {
    public static final float PIXELS_PER_INCH = 73 / 10.25f;

    public static final float TABLE_WIDTH = (876 - 500 / 2) * 2 / PIXELS_PER_INCH;
    public static final float TABLE_HEIGHT = (533 - 500 / 2) * 2 / PIXELS_PER_INCH;

    private ArrayList<ICollider> walls; 
    private ArrayList<TablePocket> pockets;
    private PImage tableImage;  

    public PoolTable(float width, float height, PApplet app) {
        float PPI = PoolTable.PIXELS_PER_INCH;
        walls = new ArrayList<>();
        walls.add(new Wall(new Vector((width / 2 - 306) / PPI, (height / 2 + 531) / PPI), 539 / PPI, 500 / PPI, -2));
        walls.add(new Wall(new Vector((width / 2 + 306) / PPI, (height / 2 + 531) / PPI), 539 / PPI, 500 / PPI, -3));

        walls.add(new Wall(new Vector((width / 2 - 306) / PPI, (height / 2 - 300 - 233) / PPI), 539 / PPI, 500 / PPI, -4));
        walls.add(new Wall(new Vector((width / 2 + 306) / PPI, (height / 2 - 300 - 233) / PPI), 539 / PPI, 500 / PPI, -5));

        walls.add(new Wall(new Vector((width / 2 + 600 + 276) / PPI, height / 2 / PPI), 500 / PPI, 480 / PPI, -6));

        walls.add(new Wall(new Vector((width / 2 - 600 - 275) / PPI, height / 2 / PPI), 500 / PPI, 480 / PPI, -7));

        pockets = new ArrayList<>();
        pockets.add(new TablePocket(new Vector((width / 2) / PPI, (height / 2 - 315) / PPI), 30 / PPI));
        pockets.add(new TablePocket(new Vector((width / 2) / PPI, (height / 2 + 315) / PPI), 30 / PPI));
        pockets.add(new TablePocket(new Vector((width / 2 - 640) / PPI, (height / 2 - 300) / PPI), 45 / PPI));
        pockets.add(new TablePocket(new Vector((width / 2 - 640) / PPI, (height / 2 + 300) / PPI), 45 / PPI));
        pockets.add(new TablePocket(new Vector((width / 2 + 640) / PPI, (height / 2 - 300) / PPI), 45 / PPI));
        pockets.add(new TablePocket(new Vector((width / 2 + 640) / PPI, (height / 2 + 300) / PPI), 45 / PPI));

        tableImage = app.loadImage("images/Pool Table Png.png");
    }

    public ArrayList<ICollider> GetWalls() {
        return walls;
    }
    public ArrayList<TablePocket> GetPockets() {
        return pockets;
    }
    public PImage GetTableImage() {
        return tableImage;
    }
}