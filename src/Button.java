import Utility.Color;
import Utility.Vector;
import processing.core.PApplet;

/**
 * A class representing a button.
 */
public class Button {
    Vector position, size;
    String label;
    PApplet app;

    /**
     * Constructs a Button.
     *
     * @param position The position of the button's center
     * @param size     The size of the button (width, height)
     * @param label    The text label of the button
     * @param app      The PApplet instance for rendering
     */
    public Button(Vector position, Vector size, String label, PApplet app) {
        this.position = position;
        this.size = size;
        this.label = label;
        this.app = app;
    }

    /**
     * Draws the button with specified bounds color and label text.
     *
     * @param boundsColor The color of the button's bounds
     */
    public void draw(Color boundsColor) {
        drawBounds(boundsColor);
        drawText();
    }

    /**
     * Draws the button's bounds with the specified color.
     *
     * @param color The color to fill the button's bounds
     */
    public void drawBounds(Color color) {
        app.fill(color.r, color.g, color.b);
        app.noStroke();
        app.rect(position.x - size.x / 2, position.y - size.y / 2, size.x, size.y);
    }
    /**
     * Draws the button's label text.
     */
    public void drawText(){
        app.fill(0);
        app.textSize(40);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.text(label, position.x, position.y);
    }

    /**
     * Checks if the mouse is over the button.
     *
     * @return true if the mouse is over the button, false otherwise
     */
    public boolean isMouseOver() {
        return app.mouseX >= position.x - size.x / 2 && app.mouseX <= position.x + size.x / 2 &&
               app.mouseY >= position.y - size.y / 2 && app.mouseY <= position.y + size.y / 2;
    }
}
