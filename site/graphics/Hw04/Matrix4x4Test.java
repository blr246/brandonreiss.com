/*
 * Matrix4x4Test.java
 *
 * JUnit tests for Matrix4x4 class.
*/
import org.junit.*;
import static org.junit.Assert.*;
import com.brandonreiss.math.*;

public class Matrix4x4Test {

  private void assertMatrix4x4Equals(Matrix4x4 expected, Matrix4x4 actual, double delta) {
    for (int r = 0; r < 4; ++r) {
      for (int c = 0; c < 4; ++c) {
        assertEquals(expected.m[r][c], actual.m[r][c], delta);
      }
    }
  }

  private void assertMatrix4x4IsIdentity(Matrix4x4 mat) {
    assertEquals(4, mat.m.length);
    for (int r = 0; r < 4; ++r) {
      assertEquals(4, mat.m[r].length);
      for (int c = 0; c < 4; ++c) {
        if (c == r) {
          assertEquals(1, mat.m[r][c], 0);
        }
        else {
          assertEquals(0, mat.m[r][c], 0);
        }
      }
    }
  }

  @Test
  public void testCtor() {
    // Default.
    {
      Matrix4x4 mat = new Matrix4x4();
      assertEquals(4, mat.m.length);
      for (int r = 0; r < 4; ++r) {
        assertEquals(4, mat.m[r].length);
      }
    }
    // Copy.
    {
      Matrix4x4 copyFrom = Matrix4x4.matrixIdentity();
      Matrix4x4 mat = new Matrix4x4(copyFrom);
      assertNotSame(copyFrom, mat);
      assertEquals(copyFrom, mat);
    }
    // Value.
    {
      Matrix4x4 mat = new Matrix4x4( 0,  1,  2,  3,
                                     4,  5,  6,  7,
                                     8,  9, 10, 11,
                                    12, 13, 14, 15);
      for (int r = 0; r < 4; ++r) {
        for (int c = 0; c < 4; ++c) {
          assertEquals(r * 4 + c, mat.m[r][c], 0);
        }
      }
    }
  }

  @Test
  public void testIdentity() {
    {
      Matrix4x4 mat = Matrix4x4.matrixIdentity();
      assertMatrix4x4IsIdentity(mat);
    }
    {
      Matrix4x4 mat = new Matrix4x4( 0,  1,  2,  3,
                                     4,  5,  6,  7,
                                     8,  9, 10, 11,
                                    12, 13, 14, 15);
      mat.identity();
      assertMatrix4x4IsIdentity(mat);
    }
  }

  @Test
  public void testMul() {
    // Identity.
    {
      Matrix4x4 mat = Matrix4x4.mul(Matrix4x4.matrixIdentity(),
                                    Matrix4x4.matrixIdentity());
      assertMatrix4x4Equals(Matrix4x4.matrixIdentity(), mat, 0);
    }
    // Counter matrix.
    {
      Matrix4x4 countMat = new Matrix4x4( 0,  1,  2,  3,
                                          4,  5,  6,  7,
                                          8,  9, 10, 11,
                                         12, 13, 14, 15);
      Matrix4x4 id = Matrix4x4.matrixIdentity();
      {
        Matrix4x4 mulRes = Matrix4x4.mul(countMat, id);
        assertEquals(countMat, mulRes);
      }
      {
        Matrix4x4 mulRes = Matrix4x4.mul(id, countMat);
        assertEquals(countMat, mulRes);
      }
      {
        Matrix4x4 mulMat = Matrix4x4.matrixIdentity();
        mulMat.mul(id);
        mulMat.mul(countMat);
        mulMat.mul(id);
        assertEquals(countMat, mulMat);
      }
      {
        Matrix4x4 mulMat = Matrix4x4.matrixIdentity();
        mulMat.mul(countMat);
        mulMat.rotateZ( 5);
        mulMat.rotateX( 5);
        mulMat.translate( 5, 5, 5);
        mulMat.rotateY( 5);
        mulMat.rotateY(-5);
        mulMat.translate(-5,-5,-5);
        mulMat.rotateX(-5);
        mulMat.rotateZ(-5);
        assertMatrix4x4Equals(countMat, mulMat, 1.0e-6);
      }
    }
  }

  @Test
  public void testTranslation() {
    double[] tVals = new double[] { 1, 2, 3 };
    Matrix4x4 mat = Matrix4x4.matrixTranslation(tVals[0], tVals[1], tVals[2]);
    assertEquals(mat.m[0][3], tVals[0], 0);
    assertEquals(mat.m[1][3], tVals[1], 0);
    assertEquals(mat.m[2][3], tVals[2], 0);
    mat.m[0][3] = 0;
    mat.m[1][3] = 0;
    mat.m[2][3] = 0;
    assertEquals(Matrix4x4.matrixIdentity(), mat);
  }

}
