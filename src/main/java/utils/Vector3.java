package utils;

public record Vector3(long x, long y, long z) {

  public Vector3 translate(final Vector3 other) {
    return new Vector3(x + other.x, y + other.y, z + other.z);
  }

  @Override
  public String toString() {
    return x + "," + y + "," + z;
  }
}
