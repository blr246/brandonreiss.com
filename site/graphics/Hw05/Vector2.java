/*
 * Vector2.java
 *
 * A 2d vector math libaray.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.math;

///<summaryA 3d vector.</summary>
public class Vector2 implements Cloneable {
  public double x, y;

  /*
   * Constructors.
  */

  public Vector2() {}

  public Vector2(Vector2 rhs) {
    x = rhs.x; y = rhs.y;
  }

  public Vector2(double x_, double y_) {
    x = x_; y = y_;
  }

  /*
   * Static methods.
  */

  ///<summaryThe zero vector.</summary>
  public static Vector2 vectorZero() {
    return new Vector2(0, 0);
  }

  ///<summaryCreate a vector with the same value for each coordinate.</summary>
  public static Vector2 vectorReplicate(double v) {
    return new Vector2(v, v);
  }

  /*
   * Local methods.
  */

  ///<summarySet to zero vector.</summary>
  public Vector2 zero() {
    x = 0; y = 0;
    return this;
  }

  ///<summarySet the same value for each coordinate.</summary>
  public Vector2 replicate(double v) {
    x = v; y = v;
    return this;
  }

  ///<summarySet the value of each coordinate.</summary>
  public Vector2 set(double x_, double y_) {
    x = x_; y = y_;
    return this;
  }

  ///<summaryAssign values from another vector.</summary>
  public Vector2 set(Vector2 rhs) {
    x = rhs.x; y = rhs.y;
    return this;
  }

  ///<summaryVector addition.</summary>
  public Vector2 add(Vector2 rhs) {
    x += rhs.x; y += rhs.y;
    return this;
  }

  ///<summaryAdd scalar to all coordinates.</summary>
  public Vector2 add(double rhs) {
    x += rhs; y += rhs;
    return this;
  }

  ///<summaryVector subtraction.</summary>
  public Vector2 sub(Vector2 rhs) {
    x -= rhs.x; y -= rhs.y;
    return this;
  }

  ///<summarySubtract scalar from all coordinates.</summary>
  public Vector2 sub(double rhs) {
    x -= rhs; y -= rhs;
    return this;
  }

  ///<summaryElement-wise multiplication.</summary>
  public Vector2 mul(Vector2 rhs) {
    x *= rhs.x; y *= rhs.y;
    return this;
  }

  ///<summaryScalar multiplication.</summary>
  public Vector2 mul(double rhs) {
    x *= rhs; y *= rhs;
    return this;
  }

  ///<summaryElement-wise division.</summary>
  public Vector2 div(Vector2 rhs) {
    x /= rhs.x; y /= rhs.y;
    return this;
  }

  ///<summaryScalar division.</summary>
  public Vector2 div(double rhs) {
    x /= rhs; y /= rhs;
    return this;
  }

  ///<summarySet to min coordinates from this and rhs.</summary>
  public Vector2 min(Vector2 rhs) {
    x = Math.min(x, rhs.x);
    y = Math.min(y, rhs.y);
    return this;
  }

  ///<summarySet to max coordinates from this and rhs.</summary>
  public Vector2 max(Vector2 rhs) {
    x = Math.max(x, rhs.x);
    y = Math.max(y, rhs.y);
    return this;
  }

  ///<summaryReturn the minimum coordinate.</summary>
  public double min() {
    return Math.min(x, y);
  }

  ///<summaryReturn the maximum coordinate.</summary>
  public double max() {
    return Math.max(x, y);
  }

  ///<summaryVector dot product.</summary>
  public double dot(Vector4 rhs) {
    return x * rhs.x + y * rhs.y;
  }

  ///<summaryVector dot product.</summary>
  public double dot(Vector3 rhs) {
    return x * rhs.x + y * rhs.y;
  }

  ///<summaryVector dot product.</summary>
  public double dot(Vector2 rhs) {
    return x * rhs.x + y * rhs.y;
  }

  ///<summaryVector length squared.</summary>
  public double lengthSq() {
    return dot(this);
  }

  ///<summaryVector length.</summary>
  public double length() {
    return Math.sqrt(lengthSq());
  }
  
  ///<summaryNormalize vector such that its length is 1.</summary>
  public Vector2 normalize() {
    return div(length());
  }

  ///<summaryVector string conversion.</summary>
  public String toString() {
    return "[ " + x + ", " + y + " ]";
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

