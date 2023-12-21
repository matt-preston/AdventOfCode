package utils;

public record Vector(int x, int y) {

  public Vector add(final Vector value) {
    return new Vector(x + value.x, y + value.y);
  }
}
