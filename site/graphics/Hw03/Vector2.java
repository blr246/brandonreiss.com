/*
 * Vector2.java
 *
 * A 2d vector math libaray.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/

package com.brandonreiss.math;

/// <summary> A 3d vector. </summary>
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

  /// <summary> The zero vector. </summary>
  public static Vector2 vectorZero() {
    return new Vector2(0, 0);
  }

  /// <summary> Create a vector with the same value for each coordinate. </summary>
  public static Vector2 vectorReplicate(double v) {
    return new Vector2(v, v);
  }

  /// <summary> Vector addition. </summary>
  public static Vector2 vectorAdd(Vector2 lhs, Vector2 rhs) {
    return new Vector2(lhs.x + rhs.x, lhs.y + rhs.y);
  }

  /// <summary> Add scalar to all coordinates. </summary>
  public static Vector2 vectorAdd(Vector2 lhs, double rhs) {
    return new Vector2(lhs.x + rhs, lhs.y + rhs);
  }

  /// <summary> Vector subtraction. </summary>
  public static Vector2 vectorSub(Vector2 lhs, Vector2 rhs) {
    return new Vector2(lhs.x - rhs.x, lhs.y - rhs.y);
  }

  /// <summary> Subtract scalar from all coordinates. </summary>
  public static Vector2 vectorSub(Vector2 lhs, double rhs) {
    return new Vector2(lhs.x - rhs, lhs.y - rhs);
  }

  /// <summary> Element-wise multiplication. </summary>
  public static Vector2 vectorMul(Vector2 lhs, Vector2 rhs) {
    return new Vector2(lhs.x * rhs.x, lhs.y * rhs.y);
  }

  /// <summary> Scalar multiplication. </summary>
  public static Vector2 vectorMul(Vector2 lhs, double rhs) {
    return new Vector2(lhs.x * rhs, lhs.y * rhs);
  }

  /// <summary> Element-wise division. </summary>
  public static Vector2 vectorDiv(Vector2 lhs, Vector2 rhs) {
    return new Vector2(lhs.x / rhs.x, lhs.y / rhs.y);
  }

  /// <summary> Scalar division. </summary>
  public static Vector2 vectorDiv(Vector2 lhs, double rhs) {
    return new Vector2(lhs.x / rhs, lhs.y / rhs);
  }

  /// <summary> Return vector with the min coordinates from both inputs. </summary>
  public static Vector2 vectorMin(Vector2 lhs, Vector2 rhs) {
    return new Vector2(Math.min(lhs.x, rhs.x),
                       Math.min(lhs.y, rhs.y));
  }

  /// <summary> Return vector with the max coordinates from both inputs. </summary>
  public static Vector2 max(Vector2 lhs, Vector2 rhs) {
    return new Vector2(Math.max(lhs.x, rhs.x),
                       Math.max(lhs.y, rhs.y));
  }

  /// <summary> Return the minimum coordinate. </summary>
  public static double vectorMin(Vector2 v) {
    return Math.min(v.x, v.y);
  }

  /// <summary> Return the maximum coordinate. </summary>
  public static double vectorMax(Vector2 v) {
    return Math.max(v.x, v.y);
  }

  /// <summary> Vector dot product. </summary>
  public static double vectorDot(Vector2 lhs, Vector2 rhs) {
    return lhs.x * rhs.x + lhs.y * rhs.y;
  }

  /// <summary> Vector length squared. </summary>
  public static double vectorLengthSq(Vector2 v) {
    return vectorDot(v, v);
  }

  /// <summary> Vector length. </summary>
  public static double vectorLength(Vector2 v) {
    return Math.sqrt(vectorLengthSq(v));
  }
  
  /// <summary> Normalize vector such that its length is 1. </summary>
  public static Vector2 vectorNormalize(Vector2 v) {
    return vectorDiv(v, vectorLength(v));
  }

  /*
   * Local methods.
  */

  /// <summary> Set to zero vector. </summary>
  public Vector2 zero() {
    x = 0; y = 0;
    return this;
  }

  /// <summary> Set the same value for each coordinate. </summary>
  public Vector2 replicate(double v) {
    x = v; y = v;
    return this;
  }

  /// <summary> Set the value of each coordinate. </summary>
  public Vector2 set(double x_, double y_) {
    x = x_; y = y_;
    return this;
  }

  /// <summary> Assign values from another vector. </summary>
  public Vector2 set(Vector2 rhs) {
    x = rhs.x; y = rhs.y;
    return this;
  }

  /// <summary> Vector addition. </summary>
  public Vector2 add(Vector2 rhs) {
    x += rhs.x; y += rhs.y;
    return this;
  }

  /// <summary> Add scalar to all coordinates. </summary>
  public Vector2 add(double rhs) {
    x += rhs; y += rhs;
    return this;
  }

  /// <summary> Vector subtraction. </summary>
  public Vector2 sub(Vector2 rhs) {
    x -= rhs.x; y -= rhs.y;
    return this;
  }

  /// <summary> Subtract scalar from all coordinates. </summary>
  public Vector2 sub(double rhs) {
    x -= rhs; y -= rhs;
    return this;
  }

  /// <summary> Element-wise multiplication. </summary>
  public Vector2 mul(Vector2 rhs) {
    x *= rhs.x; y *= rhs.y;
    return this;
  }

  /// <summary> Scalar multiplication. </summary>
  public Vector2 mul(double rhs) {
    x *= rhs; y *= rhs;
    return this;
  }

  /// <summary> Element-wise division. </summary>
  public Vector2 div(Vector2 rhs) {
    x /= rhs.x; y /= rhs.y;
    return this;
  }

  /// <summary> Scalar division. </summary>
  public Vector2 div(double rhs) {
    x /= rhs; y /= rhs;
    return this;
  }

  /// <summary> Set to min coordinates from this and rhs. </summary>
  public Vector2 min(Vector2 rhs) {
    x = Math.min(x, rhs.x);
    y = Math.min(y, rhs.y);
    return this;
  }

  /// <summary> Set to max coordinates from this and rhs. </summary>
  public Vector2 max(Vector2 rhs) {
    x = Math.max(x, rhs.x);
    y = Math.max(y, rhs.y);
    return this;
  }

  /// <summary> Return the minimum coordinate. </summary>
  public double min() {
    return Math.min(x, y);
  }

  /// <summary> Return the maximum coordinate. </summary>
  public double max() {
    return Math.max(x, y);
  }

  /// <summary> Vector dot product. </summary>
  public double dot(Vector2 rhs) {
    return x * rhs.x + y * rhs.y;
  }

  /// <summary> Vector length squared. </summary>
  public double lengthSq() {
    return dot(this);
  }

  /// <summary> Vector length. </summary>
  public double length() {
    return Math.sqrt(lengthSq());
  }
  
  /// <summary> Normalize vector such that its length is 1. </summary>
  public Vector2 normalize() {
    return div(length());
  }

  /// <summary> Vector string conversion. </summary>
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

