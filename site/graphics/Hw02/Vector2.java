/*
 * Vector2.java
 *
 * A 2d vector.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/

package com.brandonreiss.math;

public class Vector2 implements Cloneable {
  public double x, y;

  public Vector2(double x_, double y_) {
    x = x_; y = y_;
  }

  public Vector2(Vector2 rhs) {
    x = rhs.x; y = rhs.y;
  }

  public static Vector2 zero() {
    return new Vector2(0, 0);
  }
  
  public void set(double x_, double y_) {
    x = x_; y = y_;
  }
  
  public void set(Vector2 v) {
    x = v.x; y = v.y;
  }

  public Vector2 add(double rhs) {
    return new Vector2(x + rhs, y + rhs);
  }

  public Vector2 add(Vector2 rhs) {
    return new Vector2(x + rhs.x, y + rhs.y);
  }

  public Vector2 sub(double rhs) {
    return new Vector2(x - rhs, y - rhs);
  }

  public Vector2 sub(Vector2 rhs) {
    return new Vector2(x - rhs.x, y - rhs.y);
  }

  public Vector2 mul(double rhs) {
    return new Vector2(x * rhs, y * rhs);
  }

  public Vector2 mul(Vector2 rhs) {
    return new Vector2(x * rhs.x, y * rhs.y);
  }

  public Vector2 div(double rhs) {
    return new Vector2(x / rhs, y / rhs);
  }

  public Vector2 div(Vector2 rhs) {
    return new Vector2(x / rhs.x, y / rhs.y);
  }

  @Override public Vector2 clone() { return new Vector2(this); }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Vector2)) {
      return false;
    }
    Vector2 rhs = (Vector2)o;
    return (x == rhs.x) && (y == rhs.y);
  }
}
