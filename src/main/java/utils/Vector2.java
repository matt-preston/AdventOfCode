package utils;

public record Vector2(int x, int y) implements Comparable<Vector2> {

    public Vector2 translate(final Vector2 other) {
        return add(other);
    }

    public Vector2 add(final Vector2 other) {
        return new Vector2(x + other.x, y + other.y);
    }

    public Vector2 add(int otherX, int otherY) {
        return new Vector2(x + otherX, y + otherY);
    }

    public Vector2 subtract(final Vector2 other) {
        return new Vector2(x - other.x, y - other.y);
    }

    // Assumes the origin is top-left, for working with a 2D array (matrix)
    public Vector2 north() {
        return add(0, -1);
    }

    // Assumes the origin is top-left, for working with a 2D array (matrix)
    public Vector2 south() {
        return add(0, 1);
    }

    public Vector2 east() {
        return add(1, 0);
    }

    public Vector2 west() {
        return add(-1, 0);
    }

    @Override
    public String toString() {
        return x + "," + y;
    }


    @Override
    public int compareTo(Vector2 o) {
        var result = Integer.compare(x, o.x);
        if (result == 0) {
            return Integer.compare(y, o.y);
        }
        return result;
    }
}
