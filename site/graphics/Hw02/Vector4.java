/*
 * Vector4.java
 *
 * A 4d vector math libaray.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/

package com.brandonreiss.math;

/// <summary> A 4d vector. </summary>
public class Vector4 implements Cloneable {
  /// <summary> Vector coordinate values. </summary>
  public double x, y, z, w;

  public Vector4() {}

  public Vector4(Vector4 rhs) {
    x = rhs.x; y = rhs.y; z = rhs.z; w = rhs.w;
  }

  public Vector4(double x_, double y_, double z_, double w_) {
    x = x_; y = y_; z = z_; w = w_;
  }

  /// <summary> The zero vector. </summary>
  public static Vector4 zero() {
    return new Vector4(0, 0, 0, 0);
  }

  /// <summary> Create a vector with the same value for each coordinate. </summary>
  public static Vector4 replicate(double v) {
    return new Vector4(v, v, v, v);
  }

  public void set(double x_, double y_, double z_, double w_) {
    x = x_; y = y_; z = z_; w = w_;
  }

  public void set(Vector4 rhs) {
    x = rhs.x; y = rhs.y; z = rhs.z; w = rhs.w;
  }

  /// <summary> Vector addition. </summary>
  public Vector4 add(Vector4 rhs) {
    return new Vector4(x + rhs.x, y + rhs.y, z + rhs.z, w + rhs.w);
  }

  /// <summary> Add scalar to all coordinates. </summary>
  public Vector4 add(double rhs) {
    return new Vector4(x + rhs, y + rhs, z + rhs, w + rhs);
  }

  /// <summary> Vector subtraction. </summary>
  public Vector4 sub(Vector4 rhs) {
    return new Vector4(x - rhs.x, y - rhs.y, z - rhs.z, w - rhs.w);
  }

  /// <summary> Subtract scalar from all coordinates. </summary>
  public Vector4 sub(double rhs) {
    return new Vector4(x - rhs, y - rhs, z - rhs, w - rhs);
  }

  /// <summary> Element-wise multiplication. </summary>
  public Vector4 mul(Vector4 rhs) {
    return new Vector4(x * rhs.x, y * rhs.y, z * rhs.z, w * rhs.w);
  }

  /// <summary> Scalar multiplication. </summary>
  public Vector4 mul(double rhs) {
    return new Vector4(x * rhs, y * rhs, z * rhs, w * rhs);
  }

  /// <summary> Element-wise division. </summary>
  public Vector4 div(Vector4 rhs) {
    return new Vector4(x / rhs.x, y / rhs.y, z / rhs.z, w / rhs.w);
  }

  /// <summary> Scalar division. </summary>
  public Vector4 div(double rhs) {
    return new Vector4(x / rhs, y / rhs, z / rhs, w / rhs);
  }

  /// <summary> Return the minimum coordinate. </summary>
  public double min() {
    return Math.min(Math.min(Math.min(x, y), z), w);
  }

  /// <summary> Return the maximum coordinate. </summary>
  public double max() {
    return Math.max(Math.max(Math.max(x, y), z), w);
  }

  /// <summary> Return vector with the min coordinates from both inputs. </summary>
  public Vector4 min(Vector4 rhs) {
    return new Vector4(Math.min(x, rhs.x),
                       Math.min(y, rhs.y),
                       Math.min(z, rhs.z),
                       Math.min(w, rhs.w));
  }

  /// <summary> Return vector with the max coordinates from both inputs. </summary>
  public Vector4 max(Vector4 rhs) {
    return new Vector4(Math.max(x, rhs.x),
                       Math.max(y, rhs.y),
                       Math.max(z, rhs.z),
                       Math.max(w, rhs.w));
  }

  /// <summary> Matrix multiplication with column vector. </summary>
  public Vector4 transform(Matrix4x4 mat) {
    return new Vector4(mat.m[0][0] * x + mat.m[0][1] * y + mat.m[0][2] * z + mat.m[0][3] * w,
                       mat.m[1][0] * x + mat.m[1][1] * y + mat.m[1][2] * z + mat.m[1][3] * w,
                       mat.m[2][0] * x + mat.m[2][1] * y + mat.m[2][2] * z + mat.m[2][3] * w,
                       mat.m[3][0] * x + mat.m[3][1] * y + mat.m[3][2] * z + mat.m[3][3] * w);
  }

  /// <summary> Matrix multiplication with column vector. </summary>
  /// <remarks>
  ///   <para> Multiply the vector and then scale so that w = 1. </para>
  /// </remarks>
  public Vector4 transformCoord(Matrix4x4 mat) {
    Vector4 tForm = transform(mat);
    return tForm.div(tForm.w);
  }

  /// <summary> Vector dot product. </summary>
  public double dot(Vector4 rhs) {
    return x * rhs.x + y * rhs.y + z * rhs.z + w * rhs.w;
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
  public Vector4 normalize() {
    return div(length());
  }

  /// <summary> Vector string conversion. </summary>
  public String toString() {
    return "[ " + x + ", " + y + ", " + z + ", " + w + " ]";
  }

  @Override public Vector4 clone() { return new Vector4(this); }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Vector4)) {
      return false;
    }
    Vector4 rhs = (Vector4)o;
    return (x == rhs.x) && (y == rhs.y) && (z == rhs.z) && (w == rhs.w);
  }
}

