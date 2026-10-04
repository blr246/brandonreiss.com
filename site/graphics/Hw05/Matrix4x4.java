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

///<summary>A 4x4 matrix class.</summary>
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

  /*
   * Static methods.
   *
   * These are intended as initialization methods only as per the idiom
   * recommended by Effective Java.
  */

  public static Matrix4x4 CreateMatrixIdentity() {
    return new Matrix4x4( 1, 0, 0, 0,
                          0, 1, 0, 0,
                          0, 0, 1, 0,
                          0, 0, 0, 1);
  }

  public static Matrix4x4 CreateMatrixTranslation(double dx, double dy, double dz) {
    return new Matrix4x4( 1, 0, 0, dx,
                          0, 1, 0, dy,
                          0, 0, 1, dz,
                          0, 0, 0,  1);
  }

  public static Matrix4x4 CreateMatrixRotationX(double angleRad) {
    double ax = Math.cos(angleRad);
    double ay = Math.sin(angleRad);
    return new Matrix4x4( 1,  0,  0, 0,
                          0, ax,-ay, 0,
                          0, ay, ax, 0,
                          0,  0,  0, 1);
  }

  public static Matrix4x4 CreateMatrixRotationY(double angleRad) {
    double ax = Math.cos(angleRad);
    double ay = Math.sin(angleRad);
    return new Matrix4x4( ax,  0, ay, 0,
                           0,  1,  0, 0,
                         -ay,  0, ax, 0,
                           0,  0,  0, 1);
  }

  public static Matrix4x4 CreateMatrixRotationZ(double angleRad) {
    double ax = Math.cos(angleRad);
    double ay = Math.sin(angleRad);
    return new Matrix4x4( ax,-ay,  0, 0,
                          ay, ax,  0, 0,
                           0,  0,  1, 0,
                           0,  0,  0, 1);
  }

  public static Matrix4x4 CreateMatrixScale(double sx, double sy, double sz) {
    return new Matrix4x4( sx,  0,  0, 0,
                           0, sy,  0, 0,
                           0,  0, sz, 0,
                           0,  0,  0, 1);
  }

  ///<summary>Create projection matrix from horizontal field-of-view.</summary>
  public static Matrix4x4 CreateMatrixProjectionFovH(double fovH, double aspect,
                                                     double zNear, double zFar) {
    final double sx = 1 / Math.tan(fovH * 0.5);
    final double sy = sx * aspect;
    final double fSubN = zFar - zNear;
    final double A =     (zNear + zFar) / -fSubN;
    final double B = (2 * zFar * zNear) / -fSubN;
    return new Matrix4x4( sx,  0, 0, 0,
                           0, sy, 0, 0,
                           0,  0, A, B,
                           0,  0,-1, 0);
  }

  ///<summary>Create projection matrix from vertical field-of-view.</summary>
  public static Matrix4x4 CreateMatrixProjectionFovV(double fovV, double aspect,
                                                     double zNear, double zFar) {
    final double sy = 1 / Math.tan(fovV * 0.5);
    final double sx = sy / aspect;
    final double fSubN = zFar - zNear;
    final double A =     (zNear + zFar) / -fSubN;
    final double B = (2 * zFar * zNear) / -fSubN;
    return new Matrix4x4( sx,  0, 0, 0,
                           0, sy, 0, 0,
                           0,  0, A, B,
                           0,  0,-1, 0);
  }

  /*
   * Local methods.
  */

  ///<summary>Set all values to match the right-hand side.</summary>
  public Matrix4x4 set(Matrix4x4 rhs) {
    m[0][0] = rhs.m[0][0]; m[0][1] = rhs.m[0][1]; m[0][2] = rhs.m[0][2]; m[0][3] = rhs.m[0][3]; 
    m[1][0] = rhs.m[1][0]; m[1][1] = rhs.m[1][1]; m[1][2] = rhs.m[1][2]; m[1][3] = rhs.m[1][3]; 
    m[2][0] = rhs.m[2][0]; m[2][1] = rhs.m[2][1]; m[2][2] = rhs.m[2][2]; m[2][3] = rhs.m[2][3]; 
    m[3][0] = rhs.m[3][0]; m[3][1] = rhs.m[3][1]; m[3][2] = rhs.m[3][2]; m[3][3] = rhs.m[3][3]; 
    return this;
  }

  ///<summary>Set to the identity matrix.</summary>
  public Matrix4x4 identity() {
    m[0][0] = 1; m[0][1] = 0; m[0][2] = 0; m[0][3] = 0; 
    m[1][0] = 0; m[1][1] = 1; m[1][2] = 0; m[1][3] = 0; 
    m[2][0] = 0; m[2][1] = 0; m[2][2] = 1; m[2][3] = 0; 
    m[3][0] = 0; m[3][1] = 0; m[3][2] = 0; m[3][3] = 1; 
    return this;
  }

  ///<summary>Set to projection matrix using horizontal field-of-view.</summary>
  public Matrix4x4 projectionFovH(double fovH, double aspect, double zNear, double zFar) {
    final double sx = 1 / Math.tan(fovH * 0.5);
    final double sy = sx * aspect;
    final double fSubN = zFar - zNear;
    final double A =     (zNear + zFar) / -fSubN;
    final double B = (2 * zFar * zNear) / -fSubN;
    m[0][0] = sx; m[0][1] =  0; m[0][2] =  0; m[0][3] = 0; 
    m[1][0] =  0; m[1][1] = sy; m[1][2] =  0; m[1][3] = 0; 
    m[2][0] =  0; m[2][1] =  0; m[2][2] =  A; m[2][3] = B; 
    m[3][0] =  0; m[3][1] =  0; m[3][2] = -1; m[3][3] = 0; 
    return this;
  }

  ///<summary>Set to projection matrix using vertical field-of-view.</summary>
  public Matrix4x4 projectionFovV(double fovV, double aspect, double zNear, double zFar) {
    final double sy = 1 / Math.tan(fovV * 0.5);
    final double sx = sy / aspect;
    final double fSubN = zFar - zNear;
    final double A =     (zNear + zFar) / -fSubN;
    final double B = (2 * zFar * zNear) / -fSubN;
    m[0][0] = sx; m[0][1] =  0; m[0][2] =  0; m[0][3] = 0; 
    m[1][0] =  0; m[1][1] = sy; m[1][2] =  0; m[1][3] = 0; 
    m[2][0] =  0; m[2][1] =  0; m[2][2] =  A; m[2][3] = B; 
    m[3][0] =  0; m[3][1] =  0; m[3][2] = -1; m[3][3] = 0; 
    return this;
  }

  ///<summary>Multiply the matrix with the right-hand-side.</summary>
  public Matrix4x4 mul(Matrix4x4 rhs) {
    // Make a copy on the stack.
    final double _11 = m[0][0], _12 = m[0][1], _13 = m[0][2], _14 = m[0][3],
                 _21 = m[1][0], _22 = m[1][1], _23 = m[1][2], _24 = m[1][3],
                 _31 = m[2][0], _32 = m[2][1], _33 = m[2][2], _34 = m[2][3],
                 _41 = m[3][0], _42 = m[3][1], _43 = m[3][2], _44 = m[3][3];

    m[0][0] = _11 * rhs.m[0][0] + _12 * rhs.m[1][0] + _13 * rhs.m[2][0] + _14 * rhs.m[3][0];
    m[0][1] = _11 * rhs.m[0][1] + _12 * rhs.m[1][1] + _13 * rhs.m[2][1] + _14 * rhs.m[3][1];
    m[0][2] = _11 * rhs.m[0][2] + _12 * rhs.m[1][2] + _13 * rhs.m[2][2] + _14 * rhs.m[3][2];
    m[0][3] = _11 * rhs.m[0][3] + _12 * rhs.m[1][3] + _13 * rhs.m[2][3] + _14 * rhs.m[3][3];

    m[1][0] = _21 * rhs.m[0][0] + _22 * rhs.m[1][0] + _23 * rhs.m[2][0] + _24 * rhs.m[3][0];
    m[1][1] = _21 * rhs.m[0][1] + _22 * rhs.m[1][1] + _23 * rhs.m[2][1] + _24 * rhs.m[3][1];
    m[1][2] = _21 * rhs.m[0][2] + _22 * rhs.m[1][2] + _23 * rhs.m[2][2] + _24 * rhs.m[3][2];
    m[1][3] = _21 * rhs.m[0][3] + _22 * rhs.m[1][3] + _23 * rhs.m[2][3] + _24 * rhs.m[3][3];

    m[2][0] = _31 * rhs.m[0][0] + _32 * rhs.m[1][0] + _33 * rhs.m[2][0] + _34 * rhs.m[3][0];
    m[2][1] = _31 * rhs.m[0][1] + _32 * rhs.m[1][1] + _33 * rhs.m[2][1] + _34 * rhs.m[3][1];
    m[2][2] = _31 * rhs.m[0][2] + _32 * rhs.m[1][2] + _33 * rhs.m[2][2] + _34 * rhs.m[3][2];
    m[2][3] = _31 * rhs.m[0][3] + _32 * rhs.m[1][3] + _33 * rhs.m[2][3] + _34 * rhs.m[3][3];

    m[3][0] = _41 * rhs.m[0][0] + _42 * rhs.m[1][0] + _43 * rhs.m[2][0] + _44 * rhs.m[3][0];
    m[3][1] = _41 * rhs.m[0][1] + _42 * rhs.m[1][1] + _43 * rhs.m[2][1] + _44 * rhs.m[3][1];
    m[3][2] = _41 * rhs.m[0][2] + _42 * rhs.m[1][2] + _43 * rhs.m[2][2] + _44 * rhs.m[3][2];
    m[3][3] = _41 * rhs.m[0][3] + _42 * rhs.m[1][3] + _43 * rhs.m[2][3] + _44 * rhs.m[3][3];

    return this;
  }

  ///<summary>Multiply the matrix with the right-hand-side.</summary>
  public Matrix4x4 mul(double rhs_11, double rhs_12, double rhs_13, double rhs_14,
                       double rhs_21, double rhs_22, double rhs_23, double rhs_24,
                       double rhs_31, double rhs_32, double rhs_33, double rhs_34,
                       double rhs_41, double rhs_42, double rhs_43, double rhs_44) {
    // Make a copy on the stack.
    final double lhs_11 = m[0][0], lhs_12 = m[0][1], lhs_13 = m[0][2], lhs_14 = m[0][3],
                 lhs_21 = m[1][0], lhs_22 = m[1][1], lhs_23 = m[1][2], lhs_24 = m[1][3],
                 lhs_31 = m[2][0], lhs_32 = m[2][1], lhs_33 = m[2][2], lhs_34 = m[2][3],
                 lhs_41 = m[3][0], lhs_42 = m[3][1], lhs_43 = m[3][2], lhs_44 = m[3][3];

    m[0][0] = lhs_11 * rhs_11 + lhs_12 * rhs_21 + lhs_13 * rhs_31 + lhs_14 * rhs_41;
    m[0][1] = lhs_11 * rhs_12 + lhs_12 * rhs_22 + lhs_13 * rhs_32 + lhs_14 * rhs_42;
    m[0][2] = lhs_11 * rhs_13 + lhs_12 * rhs_23 + lhs_13 * rhs_33 + lhs_14 * rhs_43;
    m[0][3] = lhs_11 * rhs_14 + lhs_12 * rhs_24 + lhs_13 * rhs_34 + lhs_14 * rhs_44;

    m[1][0] = lhs_21 * rhs_11 + lhs_22 * rhs_21 + lhs_23 * rhs_31 + lhs_24 * rhs_41;
    m[1][1] = lhs_21 * rhs_12 + lhs_22 * rhs_22 + lhs_23 * rhs_32 + lhs_24 * rhs_42;
    m[1][2] = lhs_21 * rhs_13 + lhs_22 * rhs_23 + lhs_23 * rhs_33 + lhs_24 * rhs_43;
    m[1][3] = lhs_21 * rhs_14 + lhs_22 * rhs_24 + lhs_23 * rhs_34 + lhs_24 * rhs_44;

    m[2][0] = lhs_31 * rhs_11 + lhs_32 * rhs_21 + lhs_33 * rhs_31 + lhs_34 * rhs_41;
    m[2][1] = lhs_31 * rhs_12 + lhs_32 * rhs_22 + lhs_33 * rhs_32 + lhs_34 * rhs_42;
    m[2][2] = lhs_31 * rhs_13 + lhs_32 * rhs_23 + lhs_33 * rhs_33 + lhs_34 * rhs_43;
    m[2][3] = lhs_31 * rhs_14 + lhs_32 * rhs_24 + lhs_33 * rhs_34 + lhs_34 * rhs_44;

    m[3][0] = lhs_41 * rhs_11 + lhs_42 * rhs_21 + lhs_43 * rhs_31 + lhs_44 * rhs_41;
    m[3][1] = lhs_41 * rhs_12 + lhs_42 * rhs_22 + lhs_43 * rhs_32 + lhs_44 * rhs_42;
    m[3][2] = lhs_41 * rhs_13 + lhs_42 * rhs_23 + lhs_43 * rhs_33 + lhs_44 * rhs_43;
    m[3][3] = lhs_41 * rhs_14 + lhs_42 * rhs_24 + lhs_43 * rhs_34 + lhs_44 * rhs_44;

    return this;
  }

  ///<summary>Translate the matrix by the given offsets.</summary>
  ///<remarks>
  ///<para>This operation is cumulative as in M = M * M_trans.</para>
  ///</remarks>
  public Matrix4x4 translate(double dx, double dy, double dz) {
    return mul( 1, 0, 0, dx,
                0, 1, 0, dy,
                0, 0, 1, dz,
                0, 0, 0,  1);
  }

  ///<summary>Rotate the matrix by the given angle about its local X-axis.</summary>
  ///<remarks>
  ///<para>This operation is cumulative as in M = M * M_rot.</para>
  ///</remarks>
  public Matrix4x4 rotateX(double angleRad) {
    double ax = Math.cos(angleRad);
    double ay = Math.sin(angleRad);
    return mul( 1,  0,  0, 0,
                0, ax,-ay, 0,
                0, ay, ax, 0,
                0,  0,  0, 1);
  }

  ///<summary>Rotate the matrix by the given angle about its local Y-axis.</summary>
  ///<remarks>
  ///<para>This operation is cumulative as in M = M * M_rot.</para>
  ///</remarks>
  public Matrix4x4 rotateY(double angleRad) {
    double ax = Math.cos(angleRad);
    double ay = Math.sin(angleRad);
    return mul( ax,  0, ay, 0,
                 0,  1,  0, 0,
               -ay,  0, ax, 0,
                 0,  0,  0, 1);
  }

  ///<summary>Rotate the matrix by the given angle about its local Z-axis.</summary>
  ///<remarks>
  ///<para>This operation is cumulative as in M = M * M_rot.</para>
  ///</remarks>
  public Matrix4x4 rotateZ(double angleRad) {
    double ax = Math.cos(angleRad);
    double ay = Math.sin(angleRad);
    return mul( ax,-ay,  0, 0,
                ay, ax,  0, 0,
                 0,  0,  1, 0,
                 0,  0,  0, 1);
  }

  ///<summary>Scale the matrix by the given factors from its bases.</summary>
  ///<remarks>
  ///<para>This operation is cumulative as in M = M * M_scale.</para>
  ///</remarks>
  public Matrix4x4 scale(double sx, double sy, double sz) {
    return mul( sx,  0,  0, 0,
                 0, sy,  0, 0,
                 0,  0, sz, 0,
                 0,  0,  0, 1);
  }

  ///<summary>Reflect the matrix in-place about its main diagonal.</summary>
  public Matrix4x4 transpose() {
    // Make a copy on the stack.
    final double _11 = m[0][0], _12 = m[0][1], _13 = m[0][2], _14 = m[0][3],
                 _21 = m[1][0], _22 = m[1][1], _23 = m[1][2], _24 = m[1][3],
                 _31 = m[2][0], _32 = m[2][1], _33 = m[2][2], _34 = m[2][3],
                 _41 = m[3][0], _42 = m[3][1], _43 = m[3][2], _44 = m[3][3];

    m[0][0] = _11; m[0][1] = _21; m[0][2] = _31; m[0][3] = _41;
    m[1][0] = _12; m[1][1] = _22; m[1][2] = _32; m[1][3] = _42;
    m[2][0] = _13; m[2][1] = _23; m[2][2] = _33; m[2][3] = _43;
    m[3][0] = _14; m[3][1] = _24; m[3][2] = _34; m[3][3] = _44;
    return this;
  }

  public String toString() {
    return "[[" + m[0][0] + "," + m[0][1] + "," + m[0][2] + "," + m[0][3] + "],\n" +
           " [" + m[1][0] + "," + m[1][1] + "," + m[1][2] + "," + m[1][3] + "],\n" +
           " [" + m[2][0] + "," + m[2][1] + "," + m[2][2] + "," + m[2][3] + "],\n" +
           " [" + m[3][0] + "," + m[3][1] + "," + m[3][2] + "," + m[3][3] + "]]";
  }

  ///<summary>Invert the matrix.</summary>
  ///<remarks>
  ///<para>Method taken from
  ///http://www.cg.info.hiroshima-cu.ac.jp/~miyazaki/knowledge/teche23.html
  ///</para>
  ///</remarks>
  public Matrix4x4 inverse() {
    final double _11 = m[0][0], _12 = m[0][1], _13 = m[0][2], _14 = m[0][3],
                 _21 = m[1][0], _22 = m[1][1], _23 = m[1][2], _24 = m[1][3],
                 _31 = m[2][0], _32 = m[2][1], _33 = m[2][2], _34 = m[2][3],
                 _41 = m[3][0], _42 = m[3][1], _43 = m[3][2], _44 = m[3][3];
    final double det = (_11*_22*_33*_44) + (_11*_23*_34*_42) + (_11*_24*_32*_43) +
                       (_12*_21*_34*_43) + (_12*_23*_31*_44) + (_12*_24*_33*_41) +
                       (_13*_21*_32*_44) + (_13*_22*_34*_41) + (_13*_24*_31*_42) +
                       (_14*_21*_33*_42) + (_14*_22*_31*_43) + (_14*_23*_32*_41) -
                       (_11*_22*_34*_43) - (_11*_23*_32*_44) - (_11*_24*_33*_42) -
                       (_12*_21*_33*_44) - (_12*_23*_34*_41) - (_12*_24*_31*_43) -
                       (_13*_21*_34*_42) - (_13*_22*_31*_44) - (_13*_24*_32*_41) -
                       (_14*_21*_32*_43) - (_14*_22*_33*_41) - (_14*_23*_31*_42);
    assert(0 != det);
    final double detInv = 1 / det;
    m[0][0] = detInv * ((_22*_33*_44) + (_23*_34*_42) + (_24*_32*_43) - (_22*_34*_43) - (_23*_32*_44) - (_24*_33*_42));
    m[0][1] = detInv * ((_12*_34*_43) + (_13*_32*_44) + (_14*_33*_42) - (_12*_33*_44) - (_13*_34*_42) - (_14*_32*_43));
    m[0][2] = detInv * ((_12*_23*_44) + (_13*_24*_42) + (_14*_22*_43) - (_12*_24*_43) - (_13*_22*_44) - (_14*_23*_42));
    m[0][3] = detInv * ((_12*_24*_33) + (_13*_22*_34) + (_14*_23*_32) - (_12*_23*_34) - (_13*_24*_32) - (_14*_22*_33));
    m[1][0] = detInv * ((_21*_34*_43) + (_23*_31*_44) + (_24*_33*_41) - (_21*_33*_44) - (_23*_34*_41) - (_24*_31*_43));
    m[1][1] = detInv * ((_11*_33*_44) + (_13*_34*_41) + (_14*_31*_43) - (_11*_34*_43) - (_13*_31*_44) - (_14*_33*_41));
    m[1][2] = detInv * ((_11*_24*_43) + (_13*_21*_44) + (_14*_23*_41) - (_11*_23*_44) - (_13*_24*_41) - (_14*_21*_43));
    m[1][3] = detInv * ((_11*_23*_34) + (_13*_24*_31) + (_14*_21*_33) - (_11*_24*_33) - (_13*_21*_34) - (_14*_23*_31));
    m[2][0] = detInv * ((_21*_32*_44) + (_22*_34*_41) + (_24*_31*_42) - (_21*_34*_42) - (_22*_31*_44) - (_24*_32*_41));
    m[2][1] = detInv * ((_11*_34*_42) + (_12*_31*_44) + (_14*_32*_41) - (_11*_32*_44) - (_12*_34*_41) - (_14*_31*_42));
    m[2][2] = detInv * ((_11*_22*_44) + (_12*_24*_41) + (_14*_21*_42) - (_11*_24*_42) - (_12*_21*_44) - (_14*_22*_41));
    m[2][3] = detInv * ((_11*_24*_32) + (_12*_21*_34) + (_14*_22*_31) - (_11*_22*_34) - (_12*_24*_31) - (_14*_21*_32));
    m[3][0] = detInv * ((_21*_33*_42) + (_22*_31*_43) + (_23*_32*_41) - (_21*_32*_43) - (_22*_33*_41) - (_23*_31*_42));
    m[3][1] = detInv * ((_11*_32*_43) + (_12*_33*_41) + (_13*_31*_42) - (_11*_33*_42) - (_12*_31*_43) - (_13*_32*_41));
    m[3][2] = detInv * ((_11*_23*_42) + (_12*_21*_43) + (_13*_22*_41) - (_11*_22*_43) - (_12*_23*_41) - (_13*_21*_42));
    m[3][3] = detInv * ((_11*_22*_33) + (_12*_23*_31) + (_13*_21*_32) - (_11*_23*_32) - (_12*_21*_33) - (_13*_22*_31));
    return this;
  }

  @Override
  public Matrix4x4 clone() { return new Matrix4x4(this); }

  @Override
  public boolean equals(Object o) {
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

