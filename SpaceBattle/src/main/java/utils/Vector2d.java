package utils;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.Random;

public class Vector2d {
    // of course, also require the methods for adding
    // to these vectors

    public static void main(String[] args) {
        // main method for convenient testing
        Vector2d v = new Vector2d(10, 10);
        System.out.println(v.mag());
        v.normalise();
        System.out.println(v.mag());
    }

    // fields
    @XmlAttribute(name = "x")
    public double x;
    @XmlAttribute(name = "y")
    public double y;

    // default is to make them immutable.
    private boolean mutable = false;

    private static final Random random = new Random();

    /**
     * Create a new immutable vector of size (0,0).
     */
    public Vector2d() {
        x = 0;
        y = 0;
    }

    /**
     * Create a new vector of size (0,0).
     *
     * @param mutable true if vector can be modified
     */
    public Vector2d(boolean mutable) {
        x = 0;
        y = 0;
        this.mutable = mutable;
    }

    /**
     * Create a new immutable vector of a user defined size.
     *
     * @param x the x component
     * @param y the y component
     */
    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Create a new vector of a user defined size.
     *
     * @param x       the x component
     * @param y       the y component
     * @param mutable true if vector can be modified
     */
    public Vector2d(double x, double y, boolean mutable) {
        this(x, y);
        this.mutable = mutable;
    }

    /**
     * Create an immutable copy of vector which is a copy of another vector.
     *
     * @param v the vector to copy
     */
    public Vector2d(Vector2d v) {
        this.x = v.getX();
        this.y = v.getY();
    }

    /**
     * Create an immutable copy of vector which is a copy of another vector.
     *
     * @param v       the vector to copy
     * @param mutable true if vector can be modified
     */
    public Vector2d(Vector2d v, boolean mutable) {
        this(v);
        this.mutable = mutable;
    }

    private void setToMutable() {
        this.mutable = true;
    }

    /**
     * Update the position of this vector.
     *
     * @param x the new x position of this vector
     * @param y the new y position of this vector
     * @throws IllegalArgumentException is this is an immutable vector
     */
    public void set(double x, double y) {
        if (mutable) {
            this.x = x;
            this.y = y;
        } else {
            throw new IllegalAccessError("This Vector2d is immutable");
        }
    }

    /**
     * Update the position of this vector.
     *
     * @param v the vector to copy from
     * @throws IllegalArgumentException is this is an immutable vector
     */
    public void set(Vector2d v) {
        if (mutable) {
            set(v.getX(), v.getY());
        } else {
            throw new IllegalAccessError("This Vector2d is immutable");
        }
    }

    // compare for equality (needs to allow for Object type argument...)

    /**
     * Returns whether a provided object is equal to this one
     *
     * @param o the object to compare against
     * @return boolean
     * whether or not o was the same as this
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Vector2d vector2D = (Vector2d) o;

        if (Double.compare(vector2D.x, x) != 0)
            return false;
        if (Double.compare(vector2D.y, y) != 0)
            return false;

        return true;
    }

    /**
     * Returns whether the provided object is at least as close to this
     * as provided
     *
     * @param o   the object to compare against
     * @param eps The provided error rate epsilon
     * @return boolean
     * The result - true if they are ~equal
     */
    public boolean roughlyEquals(Object o, float eps) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Vector2d vector2D = (Vector2d) o;

        if (Math.abs(x - vector2D.x) > eps)
            return false;
        if (Math.abs(y - vector2D.y) > eps)
            return false;

        return true;
    }

    public void zero(){
        x = 0;
        y = 0;
    }

    /**
     * Returns the hashcode for this object
     *
     * @return int
     * The hashcode result
     */
    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = x != +0.0d ? Double.doubleToLongBits(x) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = y != +0.0d ? Double.doubleToLongBits(y) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * Returns the mathematical magnitude of this
     *
     * @return double
     * The magnitude
     */
    public double mag() {
        return (Math.sqrt(x * x + y * y));
    }

    /**
     * Returns the angle of this vector in respect to (1,0)
     *
     * @return double
     * The angle - in Radians
     */
    public double theta() {
        return (Math.atan2(y, x));
    }

    /**
     * Returns the direction between the two vectors starting from this one
     *
     * @param other The other vector for the comparison
     * @return Vector2d
     * The result of the operation
     */
    public Vector2d getNormalDirectionBetween(Vector2d other) {
        Vector2d direction = new Vector2d(true);
        direction.x = other.x - x;
        direction.y = other.y - y;
        direction.normalise();
        // System.out.println(direction);
        return direction;
    }

    /**
     * Returns the angle between the two Vectors in radians
     *
     * @param v The other vector
     * @return double
     * The angle - In radians
     */
    public double angleBetween(Vector2d v) {
        // return (float)
        // Math.toDegrees(Math.acos(Math.toRadians(scalarProduct(v) / (mag() *
        // v.mag()))));
        // return (float)
        // Math.toDegrees(Vector2d.toPolar(getNormalDirectionBetween(v)).getTheta());
        return Math.toDegrees(theta() - v.theta());
    }

    // String for displaying vector as text
    @Override
    public String toString() {
        return "Vector2d{" + "x=" + x + ", y=" + y + '}';
    }

    // add argument vector

    /**
     * Mathematical add operation
     * @param v
     *          The Vector to add to this one
     */
    public void add(Vector2d v) {
        add(v.getX(), v.getY());
    }

    /**
     * Static mathematical add operation, returning a new Vector2d
     * @param first
     *          The first argument
     * @param second
     *          The second argument
     * @return Vector2d
     *          A unique Vector2d that represents the addition of
     *          the two inputs
     */
    public static Vector2d add(final Vector2d first, final Vector2d second) {
        Vector2d third = new Vector2d(first, true);
        third.add(second);
        return third;
    }

    // add coordinate values

    /**
     * Mathematical add operation with two inputs
     * @param x
     *          The value to be added to X axis
     * @param y
     *          The value to be added to Y axis
     */
    public void add(double x, double y) {
        if (mutable) {
            this.x += x;
            this.y += y;
        } else {
            throw new IllegalAccessError("This Vector2d is immutable");
        }
    }

    /**
     * Static mathematical add operation with two inputs
     * @param first
     *          The starting point to add to
     * @param x
     *          The value to be added to the X axis
     * @param y
     *          The value to be added to the Y axis
     * @return
     *          The unique Vector2d that represents the result of the operation
     */
    public static Vector2d add(final Vector2d first, double x, double y) {
        Vector2d third = new Vector2d(first, true);
        third.add(x, y);
        return third;
    }

    // weighted add - frequently useful
    public void add(Vector2d v, double fac) {
        add(v.getX() * fac, v.getY() * fac);
    }

    public static Vector2d add(final Vector2d first, final Vector2d second,
                               double fac) {
        Vector2d third = new Vector2d(first, true);
        third.add(second.x * fac, second.y * fac);
        return third;
    }

    // subtract argument vector
    public void subtract(Vector2d v) {
        subtract(v.getX(), v.getY());
    }

    public static Vector2d subtract(final Vector2d first, final Vector2d second) {
        Vector2d third = new Vector2d(first, true);
        third.subtract(second);
        return third;
    }

    // subtract coordinate values
    public void subtract(double x, double y) {
        if (mutable) {
            this.x -= x;
            this.y -= y;
        } else {
            throw new IllegalAccessError("This Vector2d is immutable");
        }
    }

    public static Vector2d subtract(final Vector2d first, double x, double y) {
        Vector2d third = new Vector2d(first, true);
        third.subtract(x, y);
        return third;
    }

    // multiply with factor
    public void multiply(double fac) {
        if (mutable) {
            this.x *= fac;
            this.y *= fac;
        } else {
            throw new IllegalAccessError("This Vector2d is immutable");
        }
    }

    public static Vector2d multiply(Vector2d first) {
        return multiply(first, 1.0);
    }

    public static Vector2d multiply(Vector2d first, double fac) {
        Vector2d second = new Vector2d(first, true);
        second.multiply(fac);
        return second;
    }

    public void divide(double fac) {
        // TODO Check for zero division
        if (fac == 0)
            throw new IllegalArgumentException(
                    "Factor is 0 - Can't divide by 0");
        if (mutable) {
            this.x /= fac;
            this.y /= fac;
        } else {
            throw new IllegalAccessError("This Vector2d is immutable");
        }
    }

    public static Vector2d divide(Vector2d first, double fac) {
        Vector2d second = new Vector2d(first, true);
        second.divide(fac);
        second.mutable = first.mutable;
        return second;
    }

    // "wrap" vector with respect to given positive values w and h

    // method assumes that x >= -w and y >= -h

    public void wrap(double w, double h) {
        if (mutable) {
            if (x >= w) {
                x = x % w;
            }
            if (y >= h) {
                y = y % h;
            }
            if (x < 0) {
                x = (x + w) % w;
            }
            if (y < 0) {
                y = (y + h) % h;
            }
        } else {
            throw new IllegalAccessError("This Vector2d is immutable");
        }
    }

    // rotate by angle given in radians
    public void rotate(double theta) {
        set(x * Math.cos(theta) - y * Math.sin(theta), x * Math.sin(theta) + y
                * Math.cos(theta));
    }

    // scalar product with argument vector
    public double scalarProduct(Vector2d v) {
        return ((x * v.getX()) + (y * v.getY()));
    }

    public static double scalarProduct(Vector2d v1, Vector2d v2) {
        Vector2d vector = new Vector2d(v1, true);
        return vector.scalarProduct(v2);
    }

    public double dot(Vector2d v) { return scalarProduct(v); }

    public static double dot(Vector2d v1, Vector2d v2) { return Vector2d.scalarProduct(v1, v2); }

    // returns the magnitude of the cross product
    public double crossMag(Vector2d v1) {
        return (x*v1.y) - (y*v1.x);
    }

    public static double crossMag(Vector2d v1, Vector2d v2) {
        return v1.crossMag(v2);
    }

    public static Vector2d toCartesian(Vector2d input) {
        double x = (input.getY() * Math.cos(input.getR()));
        double y = (input.getY() * Math.sin(input.getR()));
        return new Vector2d(x, y, input.mutable);
    }

    public static Vector2d toPolar(Vector2d input) {
        double r = Math.sqrt(input.getX() * input.getX() + input.getY()
                * input.getY());
        double theta = Math.tanh(input.getY() / input.getX());
        return new Vector2d(r, theta, input.mutable);
    }

    // distance to argument vector
    public double dist(Vector2d v) {
        double tempX = (x - v.getX()) * (x - v.getX());
        double tempY = (y - v.getY()) * (y - v.getY());

        return (Math.sqrt(tempX + tempY));
    }

    public void clone(Vector2d target) {
        if (mutable) {
            this.x = target.x;
            this.y = target.y;
        } else {
            throw new IllegalAccessError("This Vector2d is immutable");
        }
    }

    public static void clone(Vector2d target, Vector2d source) {
        if (source.mutable) {
            source.x = target.x;
            source.y = target.y;
        } else {
            throw new IllegalAccessError("This Vector2d is immutable");
        }
    }

    // normalise vector so that mag becomes 1

    // direction is unchanged

    public void normalise() {
        if (mutable) {
            if (x != 0 || y != 0) {
                multiply((1 / mag()));
            }
        } else {
            throw new IllegalAccessError("This Vector2d is immutable");
        }
    }

    public static Vector2d normalise(Vector2d first) {
        Vector2d second = new Vector2d(first, true);
        second.normalise();
        // if (first.mutable)
        //     second.setToMutable();
        return second;
    }

    public double getX() {
        return x;
    }

    public double getR() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getTheta() {
        return y;
    }

    public static Vector2d getRandomCartesian(double xLimit, double yLimit,
                                              boolean mutable) {
        return new Vector2d(random.nextFloat() * xLimit, random.nextFloat()
                * yLimit, mutable);
    }

    public static Vector2d getRandomPolar(double angleRange, double speedMin,
                                          double speedMax, boolean mutable) {
        return new Vector2d(
                random.nextFloat() * angleRange - angleRange / 2,
                (speedMin != speedMax) ? (random.nextFloat() * speedMax - speedMin)
                        + speedMin
                        : speedMax, mutable);
    }
}
