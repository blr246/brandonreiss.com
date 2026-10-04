/*
 * Vector4.java
 *
 * A 4d vector math libaray.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.math;

///<summary>A 3d vector.</summary>
public class Vector4 implements Cloneable {
  public double x, y, z, w;

  /*
   * Constructors.
  */

  public Vector4() {}

  public Vector4(Vector4 rhs) {
    set(rhs);
  }

  public Vector4(Vector3 rhs) {
    set(rhs);
  }

  public Vector4(Vector2 rhs) {
    set(rhs);
  }

  public Vector4(double x_, double y_, double z_, double w_) {
    x = x_; y = y_; z = z_; w = w_;
  }

  /*
   * Static methods.
  */

  ///<summary>The zero vector.</summary>
  public static Vector4 vectorZero() {
    return new Vector4(0, 0, 0, 0);
  }

  ///<summary>Create a vector with the same value for each coordinate.</summary>
  public static Vector4 vectorReplicate(double v) {
    return new Vector4(v, v, v, v);
  }

  ///<summary>Vector dot product.</summary>
  public static double dot(double[] v0, double[] v1) {
    return (v0[0] * v1[0]) +
           (v0[1] * v1[1]) +
           (v0[2] * v1[2]) +
           (v0[3] * v1[3]);
  }

  ///<summary>Vector dot product.</summary>
  public static double dot(double[] v0, double[] v1, int idx1) {
    return (v0[0] * v1[idx1 + 0]) +
           (v0[1] * v1[idx1 + 1]) +
           (v0[2] * v1[idx1 + 2]) +
           (v0[3] * v1[idx1 + 3]);
  }

  ///<summary>Vector dot product.</summary>
  public static double dot(double[] v0, int idx0, double[] v1) {
    return (v0[idx0 + 0] * v1[0]) +
           (v0[idx0 + 1] * v1[1]) +
           (v0[idx0 + 2] * v1[2]) +
           (v0[idx0 + 3] * v1[3]);
  }

  ///<summary>Vector dot product.</summary>
  public static double dot(double[] v0, int idx0, double[] v1, int idx1) {
    return (v0[idx0 + 0] * v1[idx1 + 0]) +
           (v0[idx0 + 1] * v1[idx1 + 1]) +
           (v0[idx0 + 2] * v1[idx1 + 2]) +
           (v0[idx0 + 3] * v1[idx1 + 3]);
  }

  ///<summary>Matrix multiplication with column vector.</summary>
  public static double[] mul(Matrix4x4 mat, double[] v) {
    final double vx = v[0];
    final double vy = v[1];
    final double vz = v[2];
    final double vw = v[3];
    v[0] = mat.m[0][0] * vx + mat.m[0][1] * vy + mat.m[0][2] * vz + mat.m[0][3] * vw;
    v[1] = mat.m[1][0] * vx + mat.m[1][1] * vy + mat.m[1][2] * vz + mat.m[1][3] * vw;
    v[2] = mat.m[2][0] * vx + mat.m[2][1] * vy + mat.m[2][2] * vz + mat.m[2][3] * vw;
    v[3] = mat.m[3][0] * vx + mat.m[3][1] * vy + mat.m[3][2] * vz + mat.m[3][3] * vw;
    return v;
  }

  ///<summary>Matrix multiplication with column vector.</summary>
  ///<param name="idx">The start index of the vector in array v</param>
  public static double[] mul(Matrix4x4 mat, double[] v, int idx) {
    final double vx = v[idx + 0];
    final double vy = v[idx + 1];
    final double vz = v[idx + 2];
    final double vw = v[idx + 3];
    v[idx + 0] = mat.m[0][0] * vx + mat.m[0][1] * vy + mat.m[0][2] * vz + mat.m[0][3] * vw;
    v[idx + 1] = mat.m[1][0] * vx + mat.m[1][1] * vy + mat.m[1][2] * vz + mat.m[1][3] * vw;
    v[idx + 2] = mat.m[2][0] * vx + mat.m[2][1] * vy + mat.m[2][2] * vz + mat.m[2][3] * vw;
    v[idx + 3] = mat.m[3][0] * vx + mat.m[3][1] * vy + mat.m[3][2] * vz + mat.m[3][3] * vw;
    return v;
  }

  ///<summary>Transform a vector by a matrix.</summary>
  ///<remarks>
  ///<para>Ignore incoming w and set w = 1, then multiply.</para>
  ///</remarks>
  public static double[] transform(Matrix4x4 mat, double[] v) {
    v[3] = 1;
    return mul(mat, v);
  }

  ///<summary>Transform a vector by a matrix.</summary>
  ///<remarks>
  ///<para>Ignore incoming w and set w = 1, then multiply.</para>
  ///</remarks>
  public static double[] transform(Matrix4x4 mat, double[] v, int idx) {
    v[idx + 3] = 1;
    return mul(mat, v, idx);
  }

  ///<summary>Transform a vector by a matrix.</summary>
  ///<remarks>
  ///<para>Multiply the vector and then scale so that w = 1.</para>
  ///</remarks>
  public static double[] transformCoord(Matrix4x4 mat, double[] v) {
    transform(mat, v);
    final double v3 = v[3];
    v[0] /= v3;
    v[1] /= v3;
    v[2] /= v3;
    v[3] = 1;
    return v;
  }

  ///<summary>Transform a vector by a matrix.</summary>
  ///<remarks>
  ///<para>Multiply the vector and then scale so that w = 1.</para>
  ///</remarks>
  public static double[] transformCoord(Matrix4x4 mat, double[] v, int idx) {
    transform(mat, v, idx);
    final double v3 = v[idx + 3];
    v[idx + 0] /= v3;
    v[idx + 1] /= v3;
    v[idx + 2] /= v3;
    v[idx + 3] = 1;
    return v;
  }

  ///<summary>Transform a normal by a matrix.</summary>
  ///<remarks>
  ///<para>Ignore row 3 of transformation.</para>
  ///</remarks>
  public static double[] transformNormal(Matrix4x4 mat, double[] v) {
    final double vx = v[0];
    final double vy = v[1];
    final double vz = v[2];
    v[0] = mat.m[0][0] * vx + mat.m[0][1] * vy + mat.m[0][2] * vz;
    v[1] = mat.m[1][0] * vx + mat.m[1][1] * vy + mat.m[1][2] * vz;
    v[2] = mat.m[2][0] * vx + mat.m[2][1] * vy + mat.m[2][2] * vz;
    v[3] = mat.m[3][0] * vx + mat.m[3][1] * vy + mat.m[3][2] * vz;
    return v;
  }

  ///<summary>Transform a normal by a matrix.</summary>
  ///<remarks>
  ///<para>Ignore row 3 of transformation.</para>
  ///</remarks>
  public static double[] transformNormal(Matrix4x4 mat, double[] v, int idx) {
    final double vx = v[idx + 0];
    final double vy = v[idx + 1];
    final double vz = v[idx + 2];
    v[idx + 0] = mat.m[0][0] * vx + mat.m[0][1] * vy + mat.m[0][2] * vz;
    v[idx + 1] = mat.m[1][0] * vx + mat.m[1][1] * vy + mat.m[1][2] * vz;
    v[idx + 2] = mat.m[2][0] * vx + mat.m[2][1] * vy + mat.m[2][2] * vz;
    v[idx + 3] = mat.m[3][0] * vx + mat.m[3][1] * vy + mat.m[3][2] * vz;
    return v;
  }

  /*
   * Local methods.
  */

  ///<summary>Set to zero vector.</summary>
  public Vector4 zero() {
    x = 0; y = 0; z = 0; w = 0;
    return this;
  }

  ///<summary>Set the same value for each coordinate.</summary>
  public Vector4 replicate(double v) {
    x = v; y = v; z = v; w = v;
    return this;
  }

  ///<summary>Set the value of each coordinate.</summary>
  public Vector4 set(double x_, double y_, double z_, double w_) {
    x = x_; y = y_; z = z_; w = w_;
    return this;
  }

  ///<summary>Assign values from another vector.</summary>
  public Vector4 set(Vector4 rhs) {
    x = rhs.x; y = rhs.y; z = rhs.z; w = rhs.w;
    return this;
  }

  ///<summary>Assign values from another vector.</summary>
  public Vector4 set(Vector3 rhs) {
    x = rhs.x; y = rhs.y; z = rhs.z; w = 0;
    return this;
  }

  ///<summary>Assign values from another vector.</summary>
  public Vector4 set(Vector2 rhs) {
    x = rhs.x; y = rhs.y; z = 0; w = 0;
    return this;
  }

  ///<summary>Vector addition.</summary>
  public Vector4 add(Vector4 rhs) {
    x += rhs.x; y += rhs.y; z += rhs.z; w += rhs.w;
    return this;
  }

  ///<summary>Vector addition.</summary>
  public Vector4 add(Vector3 rhs) {
    x += rhs.x; y += rhs.y; z += rhs.z;
    return this;
  }

  ///<summary>Vector addition.</summary>
  public Vector4 add(Vector2 rhs) {
    x += rhs.x; y += rhs.y;
    return this;
  }

  ///<summary>Add scalar to all coordinates.</summary>
  public Vector4 add(double rhs) {
    x += rhs; y += rhs; z += rhs; w += rhs;
    return this;
  }

  ///<summary>Vector subtraction.</summary>
  public Vector4 sub(Vector4 rhs) {
    x -= rhs.x; y -= rhs.y; z -= rhs.z; w -= rhs.w;
    return this;
  }

  ///<summary>Vector subtraction.</summary>
  public Vector4 sub(Vector3 rhs) {
    x -= rhs.x; y -= rhs.y; z -= rhs.z;
    return this;
  }

  ///<summary>Vector subtraction.</summary>
  public Vector4 sub(Vector2 rhs) {
    x -= rhs.x; y -= rhs.y;
    return this;
  }

  ///<summary>Subtract scalar from all coordinates.</summary>
  public Vector4 sub(double rhs) {
    x -= rhs; y -= rhs; z -= rhs; w -= rhs;
    return this;
  }

  ///<summary>Element-wise multiplication.</summary>
  public Vector4 mul(Vector4 rhs) {
    x *= rhs.x; y *= rhs.y; z *= rhs.z; w *= rhs.w;
    return this;
  }

  ///<summary>Element-wise multiplication.</summary>
  public Vector4 mul(Vector3 rhs) {
    x *= rhs.x; y *= rhs.y; z *= rhs.z;
    return this;
  }

  ///<summary>Element-wise multiplication.</summary>
  public Vector4 mul(Vector2 rhs) {
    x *= rhs.x; y *= rhs.y;
    return this;
  }

  ///<summary>Scalar multiplication.</summary>
  public Vector4 mul(double rhs) {
    x *= rhs; y *= rhs; z *= rhs; w *= rhs;
    return this;
  }

  ///<summary>Element-wise division.</summary>
  public Vector4 div(Vector4 rhs) {
    x /= rhs.x; y /= rhs.y; z /= rhs.z; w /= rhs.w;
    return this;
  }

  ///<summary>Element-wise division.</summary>
  public Vector4 div(Vector3 rhs) {
    x /= rhs.x; y /= rhs.y; z /= rhs.z;
    return this;
  }

  ///<summary>Element-wise division.</summary>
  public Vector4 div(Vector2 rhs) {
    x /= rhs.x; y /= rhs.y;
    return this;
  }

  ///<summary>Scalar division.</summary>
  public Vector4 div(double rhs) {
    x /= rhs; y /= rhs; z /= rhs; w /= rhs;
    return this;
  }

  ///<summary>Set to min coordinates from this and rhs.</summary>
  public Vector4 min(Vector4 rhs) {
    x = Math.min(x, rhs.x);
    y = Math.min(y, rhs.y);
    z = Math.min(z, rhs.z);
    w = Math.min(w, rhs.w);
    return this;
  }

  ///<summary>Set to max coordinates from this and rhs.</summary>
  public Vector4 max(Vector4 rhs) {
    x = Math.max(x, rhs.x);
    y = Math.max(y, rhs.y);
    z = Math.max(z, rhs.z);
    w = Math.max(w, rhs.w);
    return this;
  }

  ///<summary>Return the minimum coordinate.</summary>
  public double min() {
    return Math.min(Math.min(Math.min(x, y), z), w);
  }

  ///<summary>Return the maximum coordinate.</summary>
  public double max() {
    return Math.max(Math.max(Math.max(x, y), z), w);
  }

  ///<summary>Return the sum of the coordinates.</summary>
  public double sum() {
    return x + y + z + w;
  }

  ///<summary>Vector dot product.</summary>
  public double dot(Vector4 rhs) {
    return x * rhs.x + y * rhs.y + z * rhs.z + w * rhs.w;
  }

  ///<summary>Vector dot product.</summary>
  public double dot(Vector3 rhs) {
    return x * rhs.x + y * rhs.y + z * rhs.z;
  }

  ///<summary>Vector dot product.</summary>
  public double dot(Vector2 rhs) {
    return x * rhs.x + y * rhs.y;
  }

  ///<summary>Vector dot product.</summary>
  public double dot(double[] v) {
    return x * v[0] + y * v[1] + z * v[2] + w * v[3];
  }

  ///<summary>Vector dot product.</summary>
  public double dot(double[] v, int idx) {
    return x * v[idx + 0] + y * v[idx + 1] + z * v[idx + 2] + w * v[idx + 3];
  }


  ///<summary>Vector cross product.</summary>
  public Vector4 cross(Vector4 rhs) {
    return set((y * rhs.z) - (z * rhs.y),
               (z * rhs.x) - (x * rhs.z),
               (x * rhs.y) - (y * rhs.x), 1);
  }

  ///<summary>Vector cross product.</summary>
  public Vector4 cross(Vector3 rhs) {
    return set((y * rhs.z) - (z * rhs.y),
               (z * rhs.x) - (x * rhs.z),
               (x * rhs.y) - (y * rhs.x), 1);
  }

  ///<summary>Vector length squared.</summary>
  public double lengthSq() {
    return dot(this);
  }

  ///<summary>Vector length.</summary>
  public double length() {
    return Math.sqrt(lengthSq());
  }
  
  ///<summary>Normalize vector such that its length is 1.</summary>
  public Vector4 normalize() {
    return div(length());
  }

  ///<summary>Matrix multiplication with column vector.</summary>
  public Vector4 mul(Matrix4x4 mat) {
    final double vx = x;
    final double vy = y;
    final double vz = z;
    final double vw = w;
    x = mat.m[0][0] * vx + mat.m[0][1] * vy + mat.m[0][2] * vz + mat.m[0][3] * vw;
    y = mat.m[1][0] * vx + mat.m[1][1] * vy + mat.m[1][2] * vz + mat.m[1][3] * vw;
    z = mat.m[2][0] * vx + mat.m[2][1] * vy + mat.m[2][2] * vz + mat.m[2][3] * vw;
    w = mat.m[3][0] * vx + mat.m[3][1] * vy + mat.m[3][2] * vz + mat.m[3][3] * vw;
    return this;
  }

  ///<summary>Transform a vector by a matrix.</summary>
  ///<remarks>
  ///<para>Ignore incoming w and set w = 1, then multiply.</para>
  ///</remarks>
  public Vector4 transform(Matrix4x4 mat) {
    w = 1;
    return mul(mat);
  }

  ///<summary>Transform a vector by a matrix.</summary>
  ///<remarks>
  ///<para>Multiply the vector and then scale so that w = 1.</para>
  ///</remarks>
  public Vector4 transformCoord(Matrix4x4 mat) {
    return transform(mat).div(w);
  }

  ///<summary>Transform a normal by a matrix.</summary>
  ///<remarks>
  ///<para>Ignore row 3 of transformation.</para>
  ///</remarks>
  public Vector4 transformNormal(Matrix4x4 mat) {
    final double vx = x;
    final double vy = y;
    final double vz = z;
    x = mat.m[0][0] * vx + mat.m[0][1] * vy + mat.m[0][2] * vz;
    y = mat.m[1][0] * vx + mat.m[1][1] * vy + mat.m[1][2] * vz;
    z = mat.m[2][0] * vx + mat.m[2][1] * vy + mat.m[2][2] * vz;
    w = mat.m[3][0] * vx + mat.m[3][1] * vy + mat.m[3][2] * vz;
    return this;
  }

  ///<summary>Vector string conversion.</summary>
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

