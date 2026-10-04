/*
 * Vector4.java
 *
 * A 4d vector math libaray.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/

package com.brandonreiss.math;

/// <summary> A 3d vector. </summary>
public class Vector4 implements Cloneable {
  public double x, y, z, w;

  /*
   * Constructors.
  */

  public Vector4() {}

  public Vector4(Vector4 rhs) {
    x = rhs.x; y = rhs.y; z = rhs.z; w = rhs.w;
  }

  public Vector4(double x_, double y_, double z_, double w_) {
    x = x_; y = y_; z = z_; w = w_;
  }

  /*
   * Static methods.
  */

  /// <summary> The zero vector. </summary>
  public static Vector4 vectorZero() {
    return new Vector4(0, 0, 0, 0);
  }

  /// <summary> Create a vector with the same value for each coordinate. </summary>
  public static Vector4 vectorReplicate(double v) {
    return new Vector4(v, v, v, v);
  }

  /// <summary> Vector addition. </summary>
  public static Vector4 vectorAdd(Vector4 lhs, Vector4 rhs) {
    return new Vector4(lhs.x + rhs.x, lhs.y + rhs.y, lhs.z + rhs.z, lhs.w + rhs.w);
  }

  /// <summary> Add scalar to all coordinates. </summary>
  public static Vector4 vectorAdd(Vector4 lhs, double rhs) {
    return new Vector4(lhs.x + rhs, lhs.y + rhs, lhs.z + rhs, lhs.w + rhs);
  }

  /// <summary> Vector subtraction. </summary>
  public static Vector4 vectorSub(Vector4 lhs, Vector4 rhs) {
    return new Vector4(lhs.x - rhs.x, lhs.y - rhs.y, lhs.z - rhs.z, lhs.w - rhs.w);
  }

  /// <summary> Subtract scalar from all coordinates. </summary>
  public static Vector4 vectorSub(Vector4 lhs, double rhs) {
    return new Vector4(lhs.x - rhs, lhs.y - rhs, lhs.z - rhs, lhs.w - rhs);
  }

  /// <summary> Element-wise multiplication. </summary>
  public static Vector4 vectorMul(Vector4 lhs, Vector4 rhs) {
    return new Vector4(lhs.x * rhs.x, lhs.y * rhs.y, lhs.z * rhs.z, lhs.w * rhs.w);
  }

  /// <summary> Scalar multiplication. </summary>
  public static Vector4 vectorMul(Vector4 lhs, double rhs) {
    return new Vector4(lhs.x * rhs, lhs.y * rhs, lhs.z * rhs, lhs.w * rhs);
  }

  /// <summary> Element-wise division. </summary>
  public static Vector4 vectorDiv(Vector4 lhs, Vector4 rhs) {
    return new Vector4(lhs.x / rhs.x, lhs.y / rhs.y, lhs.z / rhs.z, lhs.w / rhs.w);
  }

  /// <summary> Scalar division. </summary>
  public static Vector4 vectorDiv(Vector4 lhs, double rhs) {
    return new Vector4(lhs.x / rhs, lhs.y / rhs, lhs.z / rhs, lhs.w / rhs);
  }

  /// <summary> Return vector with the min coordinates from both inputs. </summary>
  public static Vector4 vectorMin(Vector4 lhs, Vector4 rhs) {
    return new Vector4(Math.min(lhs.x, rhs.x),
                       Math.min(lhs.y, rhs.y),
                       Math.min(lhs.z, rhs.z),
                       Math.min(lhs.w, rhs.w));
  }

  /// <summary> Return vector with the max coordinates from both inputs. </summary>
  public static Vector4 max(Vector4 lhs, Vector4 rhs) {
    return new Vector4(Math.max(lhs.x, rhs.x),
                       Math.max(lhs.y, rhs.y),
                       Math.max(lhs.z, rhs.z),
                       Math.max(lhs.w, rhs.w));
  }

  /// <summary> Return the minimum coordinate. </summary>
  public static double vectorMin(Vector4 v) {
    return Math.min(Math.min(Math.min(v.x, v.y), v.z), v.w);
  }

  /// <summary> Return the maximum coordinate. </summary>
  public static double vectorMax(Vector4 v) {
    return Math.max(Math.max(Math.max(v.x, v.y), v.z), v.w);
  }

  /// <summary> Vector dot product. </summary>
  public static double vectorDot(Vector4 lhs, Vector4 rhs) {
    return lhs.x * rhs.x + lhs.y * rhs.y + lhs.z * rhs.z + lhs.w * rhs.w;
  }

  /// <summary> Vector length squared. </summary>
  public static double vectorLengthSq(Vector4 v) {
    return vectorDot(v, v);
  }

  /// <summary> Vector length. </summary>
  public static double vectorLength(Vector4 v) {
    return Math.sqrt(vectorLengthSq(v));
  }
  
  /// <summary> Normalize vector such that its length is 1. </summary>
  public static Vector4 vectorNormalize(Vector4 v) {
    return vectorDiv(v, vectorLength(v));
  }

  /// <summary> Matrix multiplication with column vector. </summary>
  public static Vector4 vectorTransform(Vector4 v, Matrix4x4 mat) {
    return new Vector4(mat.m[0][0] * v.x + mat.m[0][1] * v.y + mat.m[0][2] * v.z + mat.m[0][3] * v.w,
                       mat.m[1][0] * v.x + mat.m[1][1] * v.y + mat.m[1][2] * v.z + mat.m[1][3] * v.w,
                       mat.m[2][0] * v.x + mat.m[2][1] * v.y + mat.m[2][2] * v.z + mat.m[2][3] * v.w,
                       mat.m[3][0] * v.x + mat.m[3][1] * v.y + mat.m[3][2] * v.z + mat.m[3][3] * v.w);
  }

  /// <summary> Matrix multiplication with column vector. </summary>
  /// <remarks>
  ///   <para> Multiply the vector and then scale so that w = 1. </para>
  /// </remarks>
  public static Vector4 vectorTransformCoord(Vector4 v, Matrix4x4 mat) {
    Vector4 tForm = vectorTransform(v, mat);
    return tForm.div(tForm.w);
  }

  /*
   * Local methods.
  */

  /// <summary> Set to zero vector. </summary>
  public Vector4 zero() {
    x = 0; y = 0; z = 0; w = 0;
    return this;
  }

  /// <summary> Set the same value for each coordinate. </summary>
  public Vector4 replicate(double v) {
    x = v; y = v; z = v; w = v;
    return this;
  }

  /// <summary> Set the value of each coordinate. </summary>
  public Vector4 set(double x_, double y_, double z_, double w_) {
    x = x_; y = y_; z = z_; w = w_;
    return this;
  }

  /// <summary> Assign values from another vector. </summary>
  public Vector4 set(Vector4 rhs) {
    x = rhs.x; y = rhs.y; z = rhs.z; w = rhs.w;
    return this;
  }

  /// <summary> Vector addition. </summary>
  public Vector4 add(Vector4 rhs) {
    x += rhs.x; y += rhs.y; z += rhs.z; w += rhs.w;
    return this;
  }

  /// <summary> Add scalar to all coordinates. </summary>
  public Vector4 add(double rhs) {
    x += rhs; y += rhs; z += rhs; w += rhs;
    return this;
  }

  /// <summary> Vector subtraction. </summary>
  public Vector4 sub(Vector4 rhs) {
    x -= rhs.x; y -= rhs.y; z -= rhs.z; w -= rhs.w;
    return this;
  }

  /// <summary> Subtract scalar from all coordinates. </summary>
  public Vector4 sub(double rhs) {
    x -= rhs; y -= rhs; z -= rhs; w -= rhs;
    return this;
  }

  /// <summary> Element-wise multiplication. </summary>
  public Vector4 mul(Vector4 rhs) {
    x *= rhs.x; y *= rhs.y; z *= rhs.z; w *= rhs.w;
    return this;
  }

  /// <summary> Scalar multiplication. </summary>
  public Vector4 mul(double rhs) {
    x *= rhs; y *= rhs; z *= rhs; w *= rhs;
    return this;
  }

  /// <summary> Element-wise division. </summary>
  public Vector4 div(Vector4 rhs) {
    x /= rhs.x; y /= rhs.y; z /= rhs.z; w /= rhs.w;
    return this;
  }

  /// <summary> Scalar division. </summary>
  public Vector4 div(double rhs) {
    x /= rhs; y /= rhs; z /= rhs; w /= rhs;
    return this;
  }

  /// <summary> Set to min coordinates from this and rhs. </summary>
  public Vector4 min(Vector4 rhs) {
    x = Math.min(x, rhs.x);
    y = Math.min(y, rhs.y);
    z = Math.min(z, rhs.z);
    w = Math.min(w, rhs.w);
    return this;
  }

  /// <summary> Set to max coordinates from this and rhs. </summary>
  public Vector4 max(Vector4 rhs) {
    x = Math.max(x, rhs.x);
    y = Math.max(y, rhs.y);
    z = Math.max(z, rhs.z);
    w = Math.max(w, rhs.w);
    return this;
  }

  /// <summary> Return the minimum coordinate. </summary>
  public double min() {
    return Math.min(Math.min(Math.min(x, y), z), w);
  }

  /// <summary> Return the maximum coordinate. </summary>
  public double max() {
    return Math.max(Math.max(Math.max(x, y), z), w);
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

  /// <summary> Matrix multiplication with column vector. </summary>
  public Vector4 transform(Matrix4x4 mat) {
    return set(Vector4.vectorTransform(this, mat));
  }

  /// <summary> Matrix multiplication with column vector. </summary>
  /// <remarks>
  ///   <para> Multiply the vector and then scale so that w = 1. </para>
  /// </remarks>
  public Vector4 transformCoord(Matrix4x4 mat) {
    return transform(mat).div(w);
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

