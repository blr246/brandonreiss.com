/*
 * Vector3.java
 *
 * A 3d vector math libaray.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.math;

///<summary>A 3d vector.</summary>
public class Vector3 implements Cloneable {
  public double x, y, z;

  /*
   * Constructors.
  */

  public Vector3() {}

  public Vector3(Vector4 rhs) {
    set(rhs);
  }

  public Vector3(Vector3 rhs) {
    set(rhs);
  }

  public Vector3(Vector2 rhs) {
    set(rhs);
  }

  public Vector3(double x_, double y_, double z_) {
    x = x_; y = y_; z = z_;
  }

  /*
   * Static methods.
  */

  ///<summary>The zero vector.</summary>
  public static Vector3 vectorZero() {
    return new Vector3(0, 0, 0);
  }

  ///<summary>Create a vector with the same value for each coordinate.</summary>
  public static Vector3 vectorReplicate(double v) {
    return new Vector3(v, v, v);
  }


  /*
   * Local methods.
  */

  ///<summary>Set to zero vector.</summary>
  public Vector3 zero() {
    x = 0; y = 0; z = 0;
    return this;
  }

  ///<summary>Set the same value for each coordinate.</summary>
  public Vector3 replicate(double v) {
    x = v; y = v; z = v;
    return this;
  }

  ///<summary>Set the value of each coordinate.</summary>
  public Vector3 set(double x_, double y_, double z_) {
    x = x_; y = y_; z = z_;
    return this;
  }

  ///<summary>Assign values from another vector.</summary>
  public Vector3 set(Vector4 rhs) {
    x = rhs.x; y = rhs.y; z = rhs.z;
    return this;
  }

  ///<summary>Assign values from another vector.</summary>
  public Vector3 set(Vector3 rhs) {
    x = rhs.x; y = rhs.y; z = rhs.z;
    return this;
  }

  ///<summary>Assign values from another vector.</summary>
  public Vector3 set(Vector2 rhs) {
    x = rhs.x; y = rhs.y; z = 0;
    return this;
  }

  ///<summary>Vector addition.</summary>
  public Vector3 add(Vector4 rhs) {
    x += rhs.x; y += rhs.y; z += rhs.z;
    return this;
  }

  ///<summary>Vector addition.</summary>
  public Vector3 add(Vector3 rhs) {
    x += rhs.x; y += rhs.y; z += rhs.z;
    return this;
  }

  ///<summary>Vector addition.</summary>
  public Vector3 add(Vector2 rhs) {
    x += rhs.x; y += rhs.y;
    return this;
  }

  ///<summary>Add scalar to all coordinates.</summary>
  public Vector3 add(double rhs) {
    x += rhs; y += rhs; z += rhs;
    return this;
  }

  ///<summary>Vector subtraction.</summary>
  public Vector3 sub(Vector4 rhs) {
    x -= rhs.x; y -= rhs.y; z -= rhs.z;
    return this;
  }

  ///<summary>Vector subtraction.</summary>
  public Vector3 sub(Vector3 rhs) {
    x -= rhs.x; y -= rhs.y; z -= rhs.z;
    return this;
  }

  ///<summary>Vector subtraction.</summary>
  public Vector3 sub(Vector2 rhs) {
    x -= rhs.x; y -= rhs.y;
    return this;
  }

  ///<summary>Subtract scalar from all coordinates.</summary>
  public Vector3 sub(double rhs) {
    x -= rhs; y -= rhs; z -= rhs;
    return this;
  }

  ///<summary>Element-wise multiplication.</summary>
  public Vector3 mul(Vector4 rhs) {
    x *= rhs.x; y *= rhs.y; z *= rhs.z;
    return this;
  }

  ///<summary>Element-wise multiplication.</summary>
  public Vector3 mul(Vector3 rhs) {
    x *= rhs.x; y *= rhs.y; z *= rhs.z;
    return this;
  }

  ///<summary>Element-wise multiplication.</summary>
  public Vector3 mul(Vector2 rhs) {
    x *= rhs.x; y *= rhs.y;
    return this;
  }

  ///<summary>Scalar multiplication.</summary>
  public Vector3 mul(double rhs) {
    x *= rhs; y *= rhs; z *= rhs;
    return this;
  }

  ///<summary>Element-wise division.</summary>
  public Vector3 div(Vector4 rhs) {
    x /= rhs.x; y /= rhs.y; z /= rhs.z;
    return this;
  }

  ///<summary>Element-wise division.</summary>
  public Vector3 div(Vector3 rhs) {
    x /= rhs.x; y /= rhs.y; z /= rhs.z;
    return this;
  }

  ///<summary>Element-wise division.</summary>
  public Vector3 div(Vector2 rhs) {
    x /= rhs.x; y /= rhs.y;
    return this;
  }

  ///<summary>Scalar division.</summary>
  public Vector3 div(double rhs) {
    x /= rhs; y /= rhs; z /= rhs;
    return this;
  }

  ///<summary>Set to min coordinates from this and rhs.</summary>
  public Vector3 min(Vector3 rhs) {
    x = Math.min(x, rhs.x);
    y = Math.min(y, rhs.y);
    z = Math.min(z, rhs.z);
    return this;
  }

  ///<summary>Set to max coordinates from this and rhs.</summary>
  public Vector3 max(Vector3 rhs) {
    x = Math.max(x, rhs.x);
    y = Math.max(y, rhs.y);
    z = Math.max(z, rhs.z);
    return this;
  }

  ///<summary>Return the minimum coordinate.</summary>
  public double min() {
    return Math.min(Math.min(x, y), z);
  }

  ///<summary>Return the maximum coordinate.</summary>
  public double max() {
    return Math.max(Math.max(x, y), z);
  }

  ///<summary>Vector dot product.</summary>
  public double dot(Vector4 rhs) {
    return x * rhs.x + y * rhs.y + z * rhs.z;
  }

  ///<summary>Vector dot product.</summary>
  public double dot(Vector3 rhs) {
    return x * rhs.x + y * rhs.y + z * rhs.z;
  }

  ///<summary>Vector dot product.</summary>
  public double dot(Vector2 rhs) {
    return x * rhs.x + y * rhs.y;
  }

  ///<summary>Vector cross product.</summary>
  public Vector3 cross(Vector4 rhs) {
    return set((y * rhs.z) - (z * rhs.y),
               (z * rhs.x) - (x * rhs.z),
               (x * rhs.y) - (y * rhs.x));
  }

  ///<summary>Vector cross product.</summary>
  public Vector3 cross(Vector3 rhs) {
    return set((y * rhs.z) - (z * rhs.y),
               (z * rhs.x) - (x * rhs.z),
               (x * rhs.y) - (y * rhs.x));
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
  public Vector3 normalize() {
    return div(length());
  }

  ///<summary>Matrix multiplication with column vector.</summary>
  public Vector3 mul(Matrix4x4 mat) {
    final double vx = x;
    final double vy = y;
    final double vz = z;
    x = mat.m[0][0] * vx + mat.m[0][1] * vy + mat.m[0][2] * vz + mat.m[0][3];
    y = mat.m[1][0] * vx + mat.m[1][1] * vy + mat.m[1][2] * vz + mat.m[1][3];
    z = mat.m[2][0] * vx + mat.m[2][1] * vy + mat.m[2][2] * vz + mat.m[2][3];
    return this;
  }

  ///<summary>Transform a vector by a matrix.</summary>
  ///<remarks>
  ///<para>Ignore incoming w and set w = 1, then multiply.</para>
  ///</remarks>
  public Vector3 transform(Matrix4x4 mat) {
    return mul(mat);
  }

  ///<summary>Transform a vector by a matrix.</summary>
  ///<remarks>
  ///<para>Multiply the vector and then scale so that w = 1.</para>
  ///</remarks>
  public Vector3 transformCoord(Matrix4x4 mat) {
    final double w = mat.m[3][0] * x + mat.m[3][1] * y + mat.m[3][2] * z + mat.m[3][3];
    return transform(mat).div(w);
  }

  ///<summary>Transform a normal by a matrix.</summary>
  ///<remarks>
  ///<para>Ignore row 3 of transformation.</para>
  ///</remarks>
  public Vector3 transformNormal(Matrix4x4 mat) {
    final double vx = x;
    final double vy = y;
    final double vz = z;
    x = mat.m[0][0] * vx + mat.m[0][1] * vy + mat.m[0][2] * vz;
    y = mat.m[1][0] * vx + mat.m[1][1] * vy + mat.m[1][2] * vz;
    z = mat.m[2][0] * vx + mat.m[2][1] * vy + mat.m[2][2] * vz;
    return this;
  }

  ///<summary>Vector string conversion.</summary>
  public String toString() {
    return "[ " + x + ", " + y + ", " + z + " ]";
  }

  ///<summary>Get vector coordinate by index.</summary>
  public double getByIndex(int idx) {
    switch(idx) {
      case 0:
        return x;
      case 1:
        return y;
      case 2:
        return z;
      default:
        assert(false); // Bad index!
        return 0;
    }
  }

  ///<summary>Get vector coordinate by index.</summary>
  public Vector3 setByIndex(int idx, double v) {
    switch(idx) {
      case 0:
        x = v;
        break;
      case 1:
        y = v;
        break;
      case 2:
        z = v;
        break;
      default:
        assert(false); // Bad index!
    }
    return this;
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

