import Utility.Color;
import Utility.Vector;
import processing.core.PApplet;

public class Button {
    float x, y, width, height;
    String label;
    PApplet app;

    public Button(Vector position, Vector size, String label, PApplet app) {
        x = position.x;
        y = position.y;
        width = size.x;
        height = size.y;
        this.label = label;
        this.app = app;
    }

    public void draw(Color boundsColor) {
        drawBounds(boundsColor);
        drawText();
    }

    public void drawBounds(Color color) {
        app.fill(color.r, color.g, color.b);
        app.noStroke();
        app.rect(x - width / 2, y - height / 2, width, height);
    }
    public void drawText(){
        app.fill(0);
        app.textSize(40);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.text(label, x, y);
    }

    public boolean isMouseOver() {
        return app.mouseX >= x - width / 2 && app.mouseX <= x + width / 2 &&
               app.mouseY >= y - height / 2 && app.mouseY <= y + height / 2;
    }
}
