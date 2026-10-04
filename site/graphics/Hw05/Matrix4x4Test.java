/*
 * Matrix4x4Test.java
 *
 * JUnit tests for Matrix4x4 class.
*/
import com.brandonreiss.math.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.Random;

public class Matrix4x4Test {
  Random randGen = new Random(System.currentTimeMillis());

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
      Matrix4x4 copyFrom = Matrix4x4.CreateMatrixIdentity();
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
      Matrix4x4 mat = Matrix4x4.CreateMatrixIdentity();
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
      Matrix4x4 mat = Matrix4x4.CreateMatrixIdentity().mul(Matrix4x4.CreateMatrixIdentity());
      assertMatrix4x4Equals(Matrix4x4.CreateMatrixIdentity(), mat, 0);
    }
    // Counter matrix.
    {
      Matrix4x4 countMat = new Matrix4x4( 0,  1,  2,  3,
                                          4,  5,  6,  7,
                                          8,  9, 10, 11,
                                         12, 13, 14, 15);
      Matrix4x4 id = Matrix4x4.CreateMatrixIdentity();
      {
        Matrix4x4 mulRes = countMat.clone().mul(id);
        assertEquals(countMat, mulRes);
      }
      {
        Matrix4x4 mulRes = id.clone().mul(countMat);
        assertEquals(countMat, mulRes);
      }
      {
        Matrix4x4 mulMat = Matrix4x4.CreateMatrixIdentity();
        mulMat.mul(id);
        mulMat.mul(countMat);
        mulMat.mul(id);
        assertEquals(countMat, mulMat);
      }
      {
        Matrix4x4 mulMat = Matrix4x4.CreateMatrixIdentity();
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
    Matrix4x4 mat = Matrix4x4.CreateMatrixTranslation(tVals[0], tVals[1], tVals[2]);
    assertEquals(mat.m[0][3], tVals[0], 0);
    assertEquals(mat.m[1][3], tVals[1], 0);
    assertEquals(mat.m[2][3], tVals[2], 0);
    mat.m[0][3] = 0;
    mat.m[1][3] = 0;
    mat.m[2][3] = 0;
    assertEquals(Matrix4x4.CreateMatrixIdentity(), mat);
  }

  @Test
  public void testInverse() {
    Matrix4x4 eye = Matrix4x4.CreateMatrixIdentity();
    // Simple invert identity.
    {
      Matrix4x4 inv = eye.clone().inverse();
      assertMatrix4x4Equals(eye, inv, 0);
    }
    // Rotation.
    for (int testIdx = 0; testIdx < 100; ++testIdx) {
      // X.
      {
        final double randAng = (2 * Math.PI) - (2 * Math.PI * randGen.nextDouble());
        Matrix4x4 mat =    Matrix4x4.CreateMatrixIdentity().rotateX( randAng);
        Matrix4x4 inv = mat.clone().inverse();
        Matrix4x4 check = mat.clone().mul(inv);
        assertMatrix4x4Equals(eye, check, 1.0e-5);
      }
      // Y.
      {
        final double randAng = (2 * Math.PI) - (2 * Math.PI * randGen.nextDouble());
        Matrix4x4 mat =    Matrix4x4.CreateMatrixIdentity().rotateY( randAng);
        Matrix4x4 inv = mat.clone().inverse();
        Matrix4x4 check = mat.clone().mul(inv);
        assertMatrix4x4Equals(eye, check, 1.0e-5);
      }
      // Z.
      {
        final double randAng = (2 * Math.PI) - (2 * Math.PI * randGen.nextDouble());
        Matrix4x4 mat =    Matrix4x4.CreateMatrixIdentity().rotateZ( randAng);
        Matrix4x4 inv = mat.clone().inverse();
        Matrix4x4 check = mat.clone().mul(inv);
        assertMatrix4x4Equals(eye, check, 1.0e-5);
      }
    }
    // Multiple transforms.
    for (int testIdx = 0; testIdx < 100; ++testIdx) {
      final double rX = (2 * Math.PI) - (2 * Math.PI * randGen.nextDouble());
      final double rY = (2 * Math.PI) - (2 * Math.PI * randGen.nextDouble());
      final double rZ = (2 * Math.PI) - (2 * Math.PI * randGen.nextDouble());
      final double tX = (2 * Math.PI) - (2 * Math.PI * randGen.nextDouble());
      final double tY = (2 * Math.PI) - (2 * Math.PI * randGen.nextDouble());
      final double tZ = (2 * Math.PI) - (2 * Math.PI * randGen.nextDouble());
      final double sX = (2 * Math.PI) - (2 * Math.PI * randGen.nextDouble());
      final double sY = (2 * Math.PI) - (2 * Math.PI * randGen.nextDouble());
      final double sZ = (2 * Math.PI) - (2 * Math.PI * randGen.nextDouble());
      Matrix4x4 mat =    Matrix4x4.CreateMatrixIdentity();
      mat.rotateX(rX).rotateY(rY).rotateZ(rZ).translate(tX, tY, tZ).scale(sX, sY, sZ);
      Matrix4x4 inv = mat.clone().inverse();
      Matrix4x4 check = mat.clone().mul(inv);
      assertMatrix4x4Equals(eye, check, 1.0e-5);
    }
  }

}
