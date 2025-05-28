package Utility;
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
    public float distance(Vector other) {
        return (float) Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }
    public Vector projectOnto(Vector other) {
        float scalar = this.dot(other) / other.dot(other);
        return other.multiply(scalar);
    }
    public boolean directionOf(Vector other) {
        return Math.signum(x) == Math.signum(other.x) && Math.signum(y) == Math.signum(other.y);
    }
    public boolean equals(Vector other, float tolerance) {
        return Math.abs(this.x - other.x) < tolerance && Math.abs(this.y - other.y) < tolerance;
    }
    public Vector divide(float scalar) {
        if (scalar == 0) throw new IllegalArgumentException("Cannot divide by zero");
        return new Vector(this.x / scalar, this.y / scalar);
    }
    public float angle() {
        return (float) Math.atan2(y, x);
    }

    @Override
    public String toString() {
        return "Vector(" + x + ", " + y + ")";
    }
}
