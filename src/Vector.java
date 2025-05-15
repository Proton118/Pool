public class Vector {
    public float x;
    public float y;

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public Vector() {
        this.x = 0;
        this.y = 0;
    }

    public Vector add(Vector other) {
        return new Vector(this.x + other.x, this.y + other.y);
    }

    public Vector subtract(Vector other) {
        return new Vector(this.x - other.x, this.y - other.y);
    }

    public Vector multiply(float scalar) {
        return new Vector(this.x * scalar, this.y * scalar);
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vector normalize() {
        float mag = magnitude();
        if (mag == 0) return new Vector(0, 0);
        return new Vector(x / mag, y / mag);
    }
    public float dot(Vector other) {
        return this.x * other.x + this.y * other.y;
    }

    @Override
    public String toString() {
        return "Vector(" + x + ", " + y + ")";
    }
}
