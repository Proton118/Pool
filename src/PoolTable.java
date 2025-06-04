import java.util.ArrayList;

import Utility.Vector;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * A class representing a pool table with walls and pockets.
 */
public class PoolTable {
    public static final float PIXELS_PER_INCH = 73 / 10.25f;
    public static final float TABLE_WIDTH = (876 - 500 / 2) * 2 / PIXELS_PER_INCH;
    public static final float TABLE_HEIGHT = (533 - 500 / 2) * 2 / PIXELS_PER_INCH;

    private ArrayList<ICollider> walls;
    private ArrayList<TablePocket> pockets;
    private PImage tableImage;

    /**
     * Constructs a PoolTable with specified width and height.
     *
     * @param width  The width of the pool table in pixels
     * @param height The height of the pool table in pixels
     * @param app    The PApplet instance for rendering
     */
    public PoolTable(float width, float height, PApplet app) {
        walls = new ArrayList<>();
        walls.addAll(GenerateWalls(width, height));

        pockets = new ArrayList<>();
        pockets.addAll(GeneratePockets(width, height));

        tableImage = app.loadImage("images/Pool Table Png.png");
    }

    private ArrayList<TablePocket> GeneratePockets(float width, float height) {
        float PPI = PoolTable.PIXELS_PER_INCH;
        ArrayList<TablePocket> pockets = new ArrayList<>();
        pockets.add(new TablePocket(new Vector((width / 2) / PPI, (height / 2 - 315) / PPI), 30 / PPI));
        pockets.add(new TablePocket(new Vector((width / 2) / PPI, (height / 2 + 315) / PPI), 30 / PPI));
        pockets.add(new TablePocket(new Vector((width / 2 - 640) / PPI, (height / 2 - 300) / PPI), 45 / PPI));
        pockets.add(new TablePocket(new Vector((width / 2 - 640) / PPI, (height / 2 + 300) / PPI), 45 / PPI));
        pockets.add(new TablePocket(new Vector((width / 2 + 640) / PPI, (height / 2 - 300) / PPI), 45 / PPI));
        pockets.add(new TablePocket(new Vector((width / 2 + 640) / PPI, (height / 2 + 300) / PPI), 45 / PPI));
        return pockets;
    }

    private ArrayList<ICollider> GenerateWalls(float width, float height) {
        float PPI = PoolTable.PIXELS_PER_INCH;
        ArrayList<ICollider> walls = new ArrayList<>();
        walls.add(new Wall(new Vector((width / 2 - 306) / PPI, (height / 2 + 531) / PPI), 539 / PPI, 500 / PPI));
        walls.add(new Wall(new Vector((width / 2 + 306) / PPI, (height / 2 + 531) / PPI), 539 / PPI, 500 / PPI));

        walls.add(new Wall(new Vector((width / 2 - 306) / PPI, (height / 2 - 300 - 233) / PPI), 539 / PPI, 500 / PPI));
        walls.add(new Wall(new Vector((width / 2 + 306) / PPI, (height / 2 - 300 - 233) / PPI), 539 / PPI, 500 / PPI));

        walls.add(new Wall(new Vector((width / 2 + 600 + 276) / PPI, height / 2 / PPI), 500 / PPI, 480 / PPI));
        walls.add(new Wall(new Vector((width / 2 - 600 - 275) / PPI, height / 2 / PPI), 500 / PPI, 480 / PPI));

        return walls;
    }

    /**
     * @return an ArrayList of ICollider representing the walls of the pool table
     */
    public ArrayList<ICollider> GetWalls() {
        return walls;
    }

    /**
     * @return an ArrayList of TablePocket representing the pockets
     */
    public ArrayList<TablePocket> GetPockets() {
        return pockets;
    }

    /**
     * @return the PImage representing the pool table image
     */
    public PImage GetTableImage() {
        return tableImage;
    }
}