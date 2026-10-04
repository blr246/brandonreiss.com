/*
 * Matrix3x3.java
 *
 * A 3x3 matrix library.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
public class Matrix3x3 {
  public float[][] m = new float[3][3];
  
  public Matrix3x3() {}

  public Matrix3x3(float _11, float _12, float _13, 
                   float _21, float _22, float _23, 
                   float _31, float _32, float _33) {
    m[0][0] = _11; m[0][1] = _12; m[0][2] = _13;
    m[1][0] = _21; m[1][1] = _22; m[1][2] = _23;
    m[2][0] = _31; m[2][1] = _32; m[2][2] = _33;
  }

  public static Matrix3x3 identity() {
    return new Matrix3x3(1, 0, 0,
                         0, 1, 0,
                         0, 0, 1);
  }

  public static Matrix3x3 matrixTranslation(float dx, float dy) {
    return new Matrix3x3(1, 0, dx,
                         0, 1, dy,
                         0, 0,  1);
  }

  public static Matrix3x3 matrixRotation(float angleRad) {
    float ax = (float)Math.cos(angleRad);
    float ay = (float)Math.sin(angleRad);
    return new Matrix3x3(ax, -ay, 0,
                         ay,  ax, 0,
                         0,    0, 1);
  }

  public static Matrix3x3 matrixScale(float sx, float sy) {
    return new Matrix3x3(sx,  0, 0,
                         0,  sy, 0,
                         0,   0, 1);
  }

  public Matrix3x3 transpose() {
    return new Matrix3x3(m[0][0], m[1][0], m[2][0],
                         m[0][1], m[1][1], m[2][1],
                         m[0][2], m[1][2], m[2][2]);
  }

  public Matrix3x3 mul(Matrix3x3 rhs) {
    Matrix3x3 res = new Matrix3x3();

    res.m[0][0] = m[0][0] * rhs.m[0][0] + m[0][1] * rhs.m[1][0] + m[0][2] * rhs.m[2][0];
    res.m[0][1] = m[0][0] * rhs.m[0][1] + m[0][1] * rhs.m[1][1] + m[0][2] * rhs.m[2][1];
    res.m[0][2] = m[0][0] * rhs.m[0][2] + m[0][1] * rhs.m[1][2] + m[0][2] * rhs.m[2][2];

    res.m[1][0] = m[1][0] * rhs.m[0][0] + m[1][1] * rhs.m[1][0] + m[1][2] * rhs.m[2][0];
    res.m[1][1] = m[1][0] * rhs.m[0][1] + m[1][1] * rhs.m[1][1] + m[1][2] * rhs.m[2][1];
    res.m[1][2] = m[1][0] * rhs.m[0][2] + m[1][1] * rhs.m[1][2] + m[1][2] * rhs.m[2][2];

    res.m[2][0] = m[2][0] * rhs.m[0][0] + m[2][1] * rhs.m[1][0] + m[2][2] * rhs.m[2][0];
    res.m[2][1] = m[2][0] * rhs.m[0][1] + m[2][1] * rhs.m[1][1] + m[2][2] * rhs.m[2][1];
    res.m[2][2] = m[2][0] * rhs.m[0][2] + m[2][1] * rhs.m[1][2] + m[2][2] * rhs.m[2][2];

    return res;
  }

  public String toString() {
    return "[" + m[0][0] + "," + m[0][1] + "," + m[0][2] + ",\n" +
                 m[1][0] + "," + m[1][1] + "," + m[1][2] + ",\n" +
                 m[2][0] + "," + m[2][1] + "," + m[2][2] + "]";
  }
}

