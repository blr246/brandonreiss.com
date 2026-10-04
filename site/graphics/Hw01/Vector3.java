/*
 * Vector3.java
 *
 * A 3d vector math libaray.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
public class Vector3 implements Cloneable {
  public float x;
  public float y;
  public float w;

  public Vector3() {}

  public Vector3(Vector3 rhs) {
    x = rhs.x; y = rhs.y; w = rhs.w;
  }

  public Vector3(float x_, float y_, float w_) {
    x = x_; y = y_; w = w_;
  }

  public static Vector3 zero() {
    return new Vector3(0, 0, 0);
  }

  public void set(float x_, float y_, float w_) {
    x = x_; y = y_; w = w_;
  }

  public void set(Vector3 rhs) {
    x = rhs.x; y = rhs.y; w = rhs.w;
  }

  public void replicate(float v) {
    x = v; y = v; w = v;
  }

  public Vector3 add(Vector3 rhs) {
    return new Vector3(x + rhs.x, y + rhs.y, w + rhs.w);
  }

  /// <summary> Replicate addition to all. </summary>
  public Vector3 add(float rhs) {
    return new Vector3(x + rhs, y + rhs, w + rhs);
  }

  public Vector3 sub(Vector3 rhs) {
    return new Vector3(x - rhs.x, y - rhs.y, w - rhs.w);
  }

  /// <summary> Replicate subtraction to all. </summary>
  public Vector3 sub(float rhs) {
    return new Vector3(x - rhs, y - rhs, w - rhs);
  }

  /// <summary> Element-wise multiplication. </summary>
  public Vector3 mul(Vector3 rhs) {
    return new Vector3(x * rhs.x, y * rhs.y, w * rhs.w);
  }

  public Vector3 mul(float rhs) {
    return new Vector3(x * rhs, y * rhs, w * rhs);
  }

  /// <summary> Element-wise division. </summary>
  public Vector3 div(Vector3 rhs) {
    return new Vector3(x / rhs.x, y / rhs.y, w / rhs.w);
  }

  public Vector3 div(float rhs) {
    return new Vector3(x / rhs, y / rhs, w / rhs);
  }

  public float min() {
    return Math.min(Math.min(x, y), w);
  }

  public float max() {
    return Math.max(Math.max(x, y), w);
  }

  public Vector3 min(Vector3 rhs) {
    return new Vector3(Math.min(x, rhs.x),
                       Math.min(y, rhs.y),
                       Math.min(w, rhs.w));
  }

  public Vector3 max(Vector3 rhs) {
    return new Vector3(Math.max(x, rhs.x),
                       Math.max(y, rhs.y),
                       Math.max(w, rhs.w));
  }

  public Vector3 transform(Matrix3x3 mat) {
    return new Vector3(mat.m[0][0] * x + mat.m[0][1] * y + mat.m[0][2] * w,
                       mat.m[1][0] * x + mat.m[1][1] * y + mat.m[1][2] * w,
                       mat.m[2][0] * x + mat.m[2][1] * y + mat.m[2][2] * w);
  }

  public Vector3 transformCoord(Matrix3x3 mat) {
    Vector3 tForm = transform(mat);
    return tForm.div(tForm.w);
  }

  public float lengthSq() {
    return x * x + y * y + w * w;
  }

  public float length() {
    return (float)Math.sqrt(lengthSq());
  }
  
  public Vector3 normalize() {
    return div(length());
  }

  public String toString() {
    return "{ " + x + ", " + y + ", " + w + " }";
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
    return (x == rhs.x) && (y == rhs.y) && (w == rhs.w);
  }
}

