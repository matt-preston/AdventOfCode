package utils;

import java.util.Objects;

public class Vector3 {

  public double x;
  public double y;
  public double z;

  public Vector3(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public double x() {
    return x;
  }

  public double y() {
    return y;
  }

  public double z() {
    return z;
  }

  public void x(double x) {
    this.x = x;
  }

  public void y(double y) {
    this.y = y;
  }

  public void z(double z) {
    this.z = z;
  }

  public void plusEquals(Vector3 other) {
    this.x += other.x;
    this.y += other.y;
    this.z += other.z;
  }

  public void minusEquals(Vector3 other) {
    this.x -= other.x;
    this.y -= other.y;
    this.z -= other.z;
  }

  public Vector3 translate(final Vector3 other) {
    return new Vector3(x + other.x, y + other.y, z + other.z);
  }

  public Vector3 copy() {
    return new Vector3(x, y, z);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Vector3 vector3 = (Vector3) o;
    return Double.compare(x, vector3.x) == 0 && Double.compare(y, vector3.y) == 0 && Double.compare(z, vector3.z) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, z);
  }

  @Override
  public String toString() {
    return x + "," + y + "," + z;
  }
}
