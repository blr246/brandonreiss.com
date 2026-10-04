/*
 * Vector3.java
 *
 * A 3d vector math libaray.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/

package com.brandonreiss.math;

/// <summary> A 3d vector. </summary>
public class Vector3 implements Cloneable {
  public double x, y, z;

  /*
   * Constructors.
  */

  public Vector3() {}

  public Vector3(Vector3 rhs) {
    x = rhs.x; y = rhs.y; z = rhs.z;
  }

  public Vector3(double x_, double y_, double z_) {
    x = x_; y = y_; z = z_;
  }

  /*
   * Static methods.
  */

  /// <summary> The zero vector. </summary>
  public static Vector3 vectorZero() {
    return new Vector3(0, 0, 0);
  }

  /// <summary> Create a vector with the same value for each coordinate. </summary>
  public static Vector3 vectorReplicate(double v) {
    return new Vector3(v, v, v);
  }

  /// <summary> Vector addition. </summary>
  public static Vector3 vectorAdd(Vector3 lhs, Vector3 rhs) {
    return new Vector3(lhs.x + rhs.x, lhs.y + rhs.y, lhs.z + rhs.z);
  }

  /// <summary> Add scalar to all coordinates. </summary>
  public static Vector3 vectorAdd(Vector3 lhs, double rhs) {
    return new Vector3(lhs.x + rhs, lhs.y + rhs, lhs.z + rhs);
  }

  /// <summary> Vector subtraction. </summary>
  public static Vector3 vectorSub(Vector3 lhs, Vector3 rhs) {
    return new Vector3(lhs.x - rhs.x, lhs.y - rhs.y, lhs.z - rhs.z);
  }

  /// <summary> Subtract scalar from all coordinates. </summary>
  public static Vector3 vectorSub(Vector3 lhs, double rhs) {
    return new Vector3(lhs.x - rhs, lhs.y - rhs, lhs.z - rhs);
  }

  /// <summary> Element-wise multiplication. </summary>
  public static Vector3 vectorMul(Vector3 lhs, Vector3 rhs) {
    return new Vector3(lhs.x * rhs.x, lhs.y * rhs.y, lhs.z * rhs.z);
  }

  /// <summary> Scalar multiplication. </summary>
  public static Vector3 vectorMul(Vector3 lhs, double rhs) {
    return new Vector3(lhs.x * rhs, lhs.y * rhs, lhs.z * rhs);
  }

  /// <summary> Element-wise division. </summary>
  public static Vector3 vectorDiv(Vector3 lhs, Vector3 rhs) {
    return new Vector3(lhs.x / rhs.x, lhs.y / rhs.y, lhs.z / rhs.z);
  }

  /// <summary> Scalar division. </summary>
  public static Vector3 vectorDiv(Vector3 lhs, double rhs) {
    return new Vector3(lhs.x / rhs, lhs.y / rhs, lhs.z / rhs);
  }

  /// <summary> Return vector with the min coordinates from both inputs. </summary>
  public static Vector3 vectorMin(Vector3 lhs, Vector3 rhs) {
    return new Vector3(Math.min(lhs.x, rhs.x),
                       Math.min(lhs.y, rhs.y),
                       Math.min(lhs.z, rhs.z));
  }

  /// <summary> Return vector with the max coordinates from both inputs. </summary>
  public static Vector3 max(Vector3 lhs, Vector3 rhs) {
    return new Vector3(Math.max(lhs.x, rhs.x),
                       Math.max(lhs.y, rhs.y),
                       Math.max(lhs.z, rhs.z));
  }

  /// <summary> Return the minimum coordinate. </summary>
  public static double vectorMin(Vector3 v) {
    return Math.min(Math.min(v.x, v.y), v.z);
  }

  /// <summary> Return the maximum coordinate. </summary>
  public static double vectorMax(Vector3 v) {
    return Math.max(Math.max(v.x, v.y), v.z);
  }

  /// <summary> Vector dot product. </summary>
  public static double vectorDot(Vector3 lhs, Vector3 rhs) {
    return lhs.x * rhs.x + lhs.y * rhs.y + lhs.z * rhs.z;
  }

  /// <summary> Vector length squared. </summary>
  public static double vectorLengthSq(Vector3 v) {
    return vectorDot(v, v);
  }

  /// <summary> Vector length. </summary>
  public static double vectorLength(Vector3 v) {
    return Math.sqrt(vectorLengthSq(v));
  }
  
  /// <summary> Normalize vector such that its length is 1. </summary>
  public static Vector3 vectorNormalize(Vector3 v) {
    return vectorDiv(v, vectorLength(v));
  }

  /*
   * Local methods.
  */

  /// <summary> Set to zero vector. </summary>
  public Vector3 zero() {
    x = 0; y = 0; z = 0;
    return this;
  }

  /// <summary> Set the same value for each coordinate. </summary>
  public Vector3 replicate(double v) {
    x = v; y = v; z = v;
    return this;
  }

  /// <summary> Set the value of each coordinate. </summary>
  public Vector3 set(double x_, double y_, double z_) {
    x = x_; y = y_; z = z_;
    return this;
  }

  /// <summary> Assign values from another vector. </summary>
  public Vector3 set(Vector3 rhs) {
    x = rhs.x; y = rhs.y; z = rhs.z;
    return this;
  }

  /// <summary> Vector addition. </summary>
  public Vector3 add(Vector3 rhs) {
    x += rhs.x; y += rhs.y; z += rhs.z;
    return this;
  }

  /// <summary> Add scalar to all coordinates. </summary>
  public Vector3 add(double rhs) {
    x += rhs; y += rhs; z += rhs;
    return this;
  }

  /// <summary> Vector subtraction. </summary>
  public Vector3 sub(Vector3 rhs) {
    x -= rhs.x; y -= rhs.y; z -= rhs.z;
    return this;
  }

  /// <summary> Subtract scalar from all coordinates. </summary>
  public Vector3 sub(double rhs) {
    x -= rhs; y -= rhs; z -= rhs;
    return this;
  }

  /// <summary> Element-wise multiplication. </summary>
  public Vector3 mul(Vector3 rhs) {
    x *= rhs.x; y *= rhs.y; z *= rhs.z;
    return this;
  }

  /// <summary> Scalar multiplication. </summary>
  public Vector3 mul(double rhs) {
    x *= rhs; y *= rhs; z *= rhs;
    return this;
  }

  /// <summary> Element-wise division. </summary>
  public Vector3 div(Vector3 rhs) {
    x /= rhs.x; y /= rhs.y; z /= rhs.z;
    return this;
  }

  /// <summary> Scalar division. </summary>
  public Vector3 div(double rhs) {
    x /= rhs; y /= rhs; z /= rhs;
    return this;
  }

  /// <summary> Set to min coordinates from this and rhs. </summary>
  public Vector3 min(Vector3 rhs) {
    x = Math.min(x, rhs.x);
    y = Math.min(y, rhs.y);
    z = Math.min(z, rhs.z);
    return this;
  }

  /// <summary> Set to max coordinates from this and rhs. </summary>
  public Vector3 max(Vector3 rhs) {
    x = Math.max(x, rhs.x);
    y = Math.max(y, rhs.y);
    z = Math.max(z, rhs.z);
    return this;
  }

  /// <summary> Return the minimum coordinate. </summary>
  public double min() {
    return Math.min(Math.min(x, y), z);
  }

  /// <summary> Return the maximum coordinate. </summary>
  public double max() {
    return Math.max(Math.max(x, y), z);
  }

  /// <summary> Vector dot product. </summary>
  public double dot(Vector3 rhs) {
    return x * rhs.x + y * rhs.y + z * rhs.z;
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
  public Vector3 normalize() {
    return div(length());
  }

  /// <summary> Vector string conversion. </summary>
  public String toString() {
    return "[ " + x + ", " + y + ", " + z + " ]";
  }

  @Override public Vector3 clone() { return new Vector3(this); }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Vector3)) {
      return false;
    }
    Vector3 rhs = (Vector3)o;
    return (x == rhs.x) && (y == rhs.y) && (z == rhs.z);
  }
}

