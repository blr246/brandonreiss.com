/*
 * Matrix4x4.java
 *
 * A 4x4 matrix library.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/

package com.brandonreiss.math;

import java.util.Arrays;

public class Matrix4x4 implements Cloneable {
  public double[][] m = new double[4][4];
  
  public Matrix4x4() {}

  public Matrix4x4(double _11, double _12, double _13, double _14,
                   double _21, double _22, double _23, double _24,
                   double _31, double _32, double _33, double _34,
                   double _41, double _42, double _43, double _44) {
    m[0][0] = _11; m[0][1] = _12; m[0][2] = _13; m[0][3] = _14;
    m[1][0] = _21; m[1][1] = _22; m[1][2] = _23; m[1][3] = _24;
    m[2][0] = _31; m[2][1] = _32; m[2][2] = _33; m[2][3] = _34;
    m[3][0] = _41; m[3][1] = _42; m[3][2] = _43; m[3][3] = _44;
  }

  public Matrix4x4(Matrix4x4 rhs) {
    for (int r = 0; r < 4; ++r) {
      System.arraycopy(rhs.m[r], 0, m[r], 0, 4);
    }
  }

  public static Matrix4x4 matrixIdentity() {
    return new Matrix4x4(1, 0, 0, 0,
                         0, 1, 0, 0,
                         0, 0, 1, 0,
                         0, 0, 0, 1);
  }

  public static Matrix4x4 matrixTranslation(double dx, double dy, double dz) {
    return new Matrix4x4(1, 0, 0, dx,
                         0, 1, 0, dy,
                         0, 0, 1, dz,
                         0, 0, 0,  1);
  }

  public static Matrix4x4 matrixRotationX(double angleRad) {
    double ax = Math.cos(angleRad);
    double ay = Math.sin(angleRad);
    return new Matrix4x4(  1,  0,  0, 0,
                           0, ax,-ay, 0,
                           0, ay, ax, 0,
                           0,  0,  0, 1);
  }

  public static Matrix4x4 matrixRotationY(double angleRad) {
    double ax = Math.cos(angleRad);
    double ay = Math.sin(angleRad);
    return new Matrix4x4( ax,  0, ay, 0,
                           0,  1,  0, 0,
                         -ay,  0, ax, 0,
                           0,  0,  0, 1);
  }

  public static Matrix4x4 matrixRotationZ(double angleRad) {
    double ax = Math.cos(angleRad);
    double ay = Math.sin(angleRad);
    return new Matrix4x4( ax,-ay,  0, 0,
                          ay, ax,  0, 0,
                           0,  0,  1, 0,
                           0,  0,  0, 1);
  }

  public static Matrix4x4 matrixScale(double sx, double sy, double sz) {
    return new Matrix4x4(sx, 0,  0, 0,
                         0, sy,  0, 0,
                         0,  0, sz, 0,
                         0,  0,  0, 1);
  }

  public static Matrix4x4 matrixTranspose(Matrix4x4 mat) {
    return new Matrix4x4(mat.m[0][0], mat.m[1][0], mat.m[2][0], mat.m[3][0],
                         mat.m[0][1], mat.m[1][1], mat.m[2][1], mat.m[3][1],
                         mat.m[0][2], mat.m[1][2], mat.m[2][2], mat.m[3][2],
                         mat.m[0][3], mat.m[1][3], mat.m[2][3], mat.m[3][3]);
  }

  public static Matrix4x4 matrixProjectionFov(double fovH, double aspect, double zNear, double zFar) {
    final double sy = 1 / Math.tan(fovH * 0.5);
    final double sx = sy / aspect;
    final double q = zFar / (zNear - zFar);
    return new Matrix4x4(sx, 0, 0,         0,
                         0, sy, 0,         0,
                         0,  0, q, q * zNear,
                         0,  0,-1,         0);
  }

  public static Matrix4x4 mul(Matrix4x4 lhs, Matrix4x4 rhs) {
    Matrix4x4 res = new Matrix4x4();

    res.m[0][0] = lhs.m[0][0] * rhs.m[0][0] + lhs.m[0][1] * rhs.m[1][0] + lhs.m[0][2] * rhs.m[2][0] + lhs.m[0][3] * rhs.m[3][0];
    res.m[0][1] = lhs.m[0][0] * rhs.m[0][1] + lhs.m[0][1] * rhs.m[1][1] + lhs.m[0][2] * rhs.m[2][1] + lhs.m[0][3] * rhs.m[3][1];
    res.m[0][2] = lhs.m[0][0] * rhs.m[0][2] + lhs.m[0][1] * rhs.m[1][2] + lhs.m[0][2] * rhs.m[2][2] + lhs.m[0][3] * rhs.m[3][2];
    res.m[0][3] = lhs.m[0][0] * rhs.m[0][3] + lhs.m[0][1] * rhs.m[1][3] + lhs.m[0][2] * rhs.m[2][3] + lhs.m[0][3] * rhs.m[3][3];

    res.m[1][0] = lhs.m[1][0] * rhs.m[0][0] + lhs.m[1][1] * rhs.m[1][0] + lhs.m[1][2] * rhs.m[2][0] + lhs.m[1][3] * rhs.m[3][0];
    res.m[1][1] = lhs.m[1][0] * rhs.m[0][1] + lhs.m[1][1] * rhs.m[1][1] + lhs.m[1][2] * rhs.m[2][1] + lhs.m[1][3] * rhs.m[3][1];
    res.m[1][2] = lhs.m[1][0] * rhs.m[0][2] + lhs.m[1][1] * rhs.m[1][2] + lhs.m[1][2] * rhs.m[2][2] + lhs.m[1][3] * rhs.m[3][2];
    res.m[1][3] = lhs.m[1][0] * rhs.m[0][3] + lhs.m[1][1] * rhs.m[1][3] + lhs.m[1][2] * rhs.m[2][3] + lhs.m[1][3] * rhs.m[3][3];

    res.m[2][0] = lhs.m[2][0] * rhs.m[0][0] + lhs.m[2][1] * rhs.m[1][0] + lhs.m[2][2] * rhs.m[2][0] + lhs.m[2][3] * rhs.m[3][0];
    res.m[2][1] = lhs.m[2][0] * rhs.m[0][1] + lhs.m[2][1] * rhs.m[1][1] + lhs.m[2][2] * rhs.m[2][1] + lhs.m[2][3] * rhs.m[3][1];
    res.m[2][2] = lhs.m[2][0] * rhs.m[0][2] + lhs.m[2][1] * rhs.m[1][2] + lhs.m[2][2] * rhs.m[2][2] + lhs.m[2][3] * rhs.m[3][2];
    res.m[2][3] = lhs.m[2][0] * rhs.m[0][3] + lhs.m[2][1] * rhs.m[1][3] + lhs.m[2][2] * rhs.m[2][3] + lhs.m[2][3] * rhs.m[3][3];

    res.m[3][0] = lhs.m[3][0] * rhs.m[0][0] + lhs.m[3][1] * rhs.m[1][0] + lhs.m[3][2] * rhs.m[2][0] + lhs.m[3][3] * rhs.m[3][0];
    res.m[3][1] = lhs.m[3][0] * rhs.m[0][1] + lhs.m[3][1] * rhs.m[1][1] + lhs.m[3][2] * rhs.m[2][1] + lhs.m[3][3] * rhs.m[3][1];
    res.m[3][2] = lhs.m[3][0] * rhs.m[0][2] + lhs.m[3][1] * rhs.m[1][2] + lhs.m[3][2] * rhs.m[2][2] + lhs.m[3][3] * rhs.m[3][2];
    res.m[3][3] = lhs.m[3][0] * rhs.m[0][3] + lhs.m[3][1] * rhs.m[1][3] + lhs.m[3][2] * rhs.m[2][3] + lhs.m[3][3] * rhs.m[3][3];

    return res;
  }

  public Matrix4x4 set(Matrix4x4 rhs) {
    m[0][0] = rhs.m[0][0]; m[0][1] = rhs.m[0][1]; m[0][2] = rhs.m[0][2]; m[0][3] = rhs.m[0][3]; 
    m[1][0] = rhs.m[1][0]; m[1][1] = rhs.m[1][1]; m[1][2] = rhs.m[1][2]; m[1][3] = rhs.m[1][3]; 
    m[2][0] = rhs.m[2][0]; m[2][1] = rhs.m[2][1]; m[2][2] = rhs.m[2][2]; m[2][3] = rhs.m[2][3]; 
    m[3][0] = rhs.m[3][0]; m[3][1] = rhs.m[3][1]; m[3][2] = rhs.m[3][2]; m[3][3] = rhs.m[3][3]; 
    return this;
  }

  public Matrix4x4 identity() {
    Matrix4x4 id = Matrix4x4.matrixIdentity();
    m = id.m;
    return this;
  }

  public Matrix4x4 mul(Matrix4x4 rhs) {
    Matrix4x4 res = mul(this, rhs);
    m = res.m;
    return this;
  }

  public Matrix4x4 leftMul(Matrix4x4 lhs) {
    Matrix4x4 res = mul(lhs, this);
    m = res.m;
    return this;
  }

  public Matrix4x4 translate(double dx, double dy, double dz) {
    return mul(matrixTranslation(dx, dy, dz));
  }

  public Matrix4x4 rotateX(double angleRad) {
    return mul(matrixRotationX(angleRad));
  }

  public Matrix4x4 rotateY(double angleRad) {
    return mul(matrixRotationY(angleRad));
  }

  public Matrix4x4 rotateZ(double angleRad) {
    return mul(matrixRotationZ(angleRad));
  }

  public Matrix4x4 scale(double sx, double sy, double sz) {
    return mul(matrixScale(sx, sy, sz));
  }

  public Matrix4x4 transpose() {
    Matrix4x4 res = matrixTranspose(this);
    m = res.m;
    return this;
  }

  public String toString() {
    return "[[" + m[0][0] + "," + m[0][1] + "," + m[0][2] + "," + m[0][3] + "],\n" +
           " [" + m[1][0] + "," + m[1][1] + "," + m[1][2] + "," + m[1][3] + "],\n" +
           " [" + m[2][0] + "," + m[2][1] + "," + m[2][2] + "," + m[2][3] + "],\n" +
           " [" + m[3][0] + "," + m[3][1] + "," + m[3][2] + "," + m[3][3] + "]]";
  }

  @Override public Matrix4x4 clone() { return new Matrix4x4(this); }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Matrix4x4)) {
      return false;
    }
    Matrix4x4 rhs = (Matrix4x4)o;
    return Arrays.deepEquals(m, rhs.m);
  }
}

