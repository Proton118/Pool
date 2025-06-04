package Utility;

/**
 * A class representing a 2d vector in component form.
 */
public class Vector {
    public float x;
    public float y;

    /**
     * Constructs a Vector with specified x and y components.
     *
     * @param x X component
     * @param y Y component
     */
    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a Vector with zero components.
     */
    public Vector() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Adds this vectors components to the corresponding components of another
     * vector.
     * 
     * @param other the other vector in the operation
     * @return the resulting vector from the addition
     */
    public Vector add(Vector other) {
        return new Vector(this.x + other.x, this.y + other.y);
    }

    /**
     * Subtracts this vector's components from the corresponding components of
     * another vector.
     * 
     * @param other the other vector in the operation
     * @return the resulting vector from the subtraction
     */
    public Vector subtract(Vector other) {
        return new Vector(this.x - other.x, this.y - other.y);
    }

    /**
     * Multiplies this vector's components by a scalar.
     * 
     * @param scalar the scalar to multiply by
     * @return the resulting vector from the multiplication
     */
    public Vector multiply(float scalar) {
        return new Vector(this.x * scalar, this.y * scalar);
    }

    /**
     * Calculates the length of this vector.
     * 
     * @return the length of the vector
     */
    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Normalizes this vector to have a magnitude of 1 whilst maintaining its
     * direction.
     * 
     * @return the normalized vector
     */
    public Vector normalize() {
        float mag = magnitude();
        if (mag == 0)
            return new Vector(0, 0);
        return new Vector(x / mag, y / mag);
    }

    /**
     * Calculates the dot product of this vector and another vector.
     * 
     * @param other the other vector in the operation
     * @return the dot product of the two vectors
     */
    public float dot(Vector other) {
        return this.x * other.x + this.y * other.y;
    }

    /**
     * Calculates the distance between this vector and another vector.
     * 
     * @param other the other vector in the operation
     * @return the distance between the two vectors
     */
    public float distance(Vector other) {
        return (float) Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    /**
     * Projects this vector onto another vector.
     * 
     * @param other the vector to project onto
     * @return the projection of this vector onto the other vector
     */
    public Vector projectOnto(Vector other) {
        float scalar = this.dot(other) / other.dot(other);
        return other.multiply(scalar);
    }

    /**
     * Checks if this vector and another vector have the same direction in terms of
     * signs.
     * 
     * @param other
     * @return
     */
    public boolean directionOf(Vector other) {
        return Math.signum(x) == Math.signum(other.x) && Math.signum(y) == Math.signum(other.y);
    }

    /**
     * Checks if this vector is equal to another vector within a specified
     * tolerance.
     * 
     * @param other     the other vector to compare with
     * @param tolerance the tolerance for comparison
     * @return true if the vectors are equal within the tolerance, false otherwise
     */
    public boolean equals(Vector other, float tolerance) {
        return Math.abs(this.x - other.x) < tolerance && Math.abs(this.y - other.y) < tolerance;
    }

    /**
     * Returns the angle of this vector in radians.
     * 
     * @return the angle of the vector in radians
     */
    public float angle() {
        return (float) Math.atan2(y, x);
    }

    /**
     * Returns a string representation of the vector.
     * 
     * @return a string in the format "Vector(x, y)"
     */
    @Override
    public String toString() {
        return "Vector(" + x + ", " + y + ")";
    }
}
