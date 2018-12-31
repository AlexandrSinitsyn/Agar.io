package agar.io;

public class Vec {
    private static double rotationDirection = 1.0D;
    public double x;
    public double y;

    public Vec(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec() {
        this(0, 0);
    }

    public int x() {
        return (int) x;
    }

    public int y() {
        return (int) y;
    }

    public Vec add(@NotNull Vec v) {
        return new Vec(x + v.x, y + v.y);
    }

    public static Vec add(@NotNull Vec v1, @NotNull Vec v2) {
        return v1.add(v2);
    }

    public Vec mull(@NotNull Vec v) {
        return new Vec(x * v.x, y * v.y);
    }

    public static Vec mull(@NotNull Vec v1, @NotNull Vec v2) {
        return v1.mull(v2);
    }

    public Vec sub(@NotNull Vec v) {
        return new Vec(x - v.x, y - v.y);
    }

    public static Vec sub(@NotNull Vec v1, @NotNull Vec v2) {
        return v1.sub(v2);
    }

    public Vec div(@NotNull Vec v) {
        return new Vec(v.x == 0.0D ? 0.0D : x / v.x, v.y == 0.0D ? 0.0D : y / v.y);
    }

    public static Vec div(@NotNull Vec v1, @NotNull Vec v2) {
        return v1.div(v2);
    }

    public Vec scale(double d) {
        return new Vec(x * d, y * d);
    }

    public static Vec scale(@NotNull Vec v, double d) {
        return v.scale(d);
    }

    public Vec scale_div(double d) {
        return new Vec(x / d, y / d);
    }

    public static Vec scale_div(@NotNull Vec v, double d) {
        return v.scale_div(d);
    }

    public Vec opposite() {
        return scale(-1.0D);
    }

    public static Vec opposite(@NotNull Vec v) {
        return v.scale(-1.0D);
    }

    public Vec rotate(double a) {
        a *= rotationDirection;
        return new Vec(x * Math.cos(a) + y * Math.sin(a), x * -Math.sin(a) + y * Math.cos(a));
    }

    public static Vec rotate(@NotNull Vec v, double a) {
        return v.rotate(a);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public static double length(@NotNull Vec v) {
        return v.length();
    }

    public void setZero() {
        x = 0;
        y = 0;
    }

    public static void setZero(@NotNull Vec v) {
        v.setZero();
    }

    public Vec normalize() {
        return (length() == 0.0D) ? new Vec(0.0D, 0.0D) : scale(1.0D / length());
    }

    public static Vec normalize(@NotNull Vec v) {
        return v.normalize();
    }

    public Vec sign() {
        return new Vec((x < 0) ? -1 : ((x > 0) ? 1 : 0), (y < 0) ? -1 : ((y > 0) ? 1 : 0));
    }

    public static Vec sign(@NotNull Vec v) {
        return v.normalize();
    }

    public double scalarProduct(@NotNull Vec v) {
        return x * v.x + y * v.y;
    }

    public static double scalarProduct(@NotNull Vec v1, @NotNull Vec v2) {
        return v1.scalarProduct(v2);
    }

    public double angle(@NotNull Vec v) {
        return Math.atan2(v.y, v.x) - Math.atan2(y, x);
    }

    public static double angle(@NotNull Vec v1, @NotNull Vec v2) {
        return v1.angle(v2);
    }

    protected static double getRotationDirection() {
        return rotationDirection;
    }

    protected static void setRotationDirection(double rotationDirection) {
        Vec.rotationDirection = rotationDirection;
    }

    public String toString() {
        return "{" + x + ", " + y + '}';
    }
}

@interface NotNull {}