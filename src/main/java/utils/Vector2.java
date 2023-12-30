package utils;

public record Vector2(int x, int y) {

  public Vector2 translate(final Vector2 other) {
    return new Vector2(x + other.x, y + other.y);
  }

  @Override
  public String toString() {
    return x + "," + y;
  }
}
