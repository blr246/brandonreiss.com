/*
 * UtilTest.java
 *
 * JUnit tests for Util class.
*/
import com.brandonreiss.math.*;
import com.brandonreiss.geometry.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.Random;

public class UtilTest {
  Random randGen = new Random(System.currentTimeMillis());

  @Test
  public void testPointPlaneDistance() {
    final int MAX_COORD = 500;
    for (int testIdx = 0; testIdx < 100; ++testIdx) {
      // Face of unit cube along positive X-axis.
      final int planeOffset = MAX_COORD - (randGen.nextInt() % (MAX_COORD + 1));
      Vector4 plane = new Vector4( 1, 0, 0, planeOffset);
      final int ptX = MAX_COORD - (randGen.nextInt() % (MAX_COORD + 1));
      Vector3 pt = new Vector3(ptX, randGen.nextInt(), randGen.nextInt());
      final double distance = Util.PointPlaneDistance(pt, plane);
      assertEquals(ptX + planeOffset, distance, 0);
    }
  }

  @Test
  public void testPointProjectPlane() {
    final int MAX_COORD = 500;
    for (int testIdx = 0; testIdx < 100; ++testIdx) {
      Vector4 xyPlane = new Vector4( 0, 0, 1, 0);
      final int ptX = MAX_COORD - (randGen.nextInt() % (MAX_COORD + 1));
      final int ptY = MAX_COORD - (randGen.nextInt() % (MAX_COORD + 1));
      final int ptZ = MAX_COORD - (randGen.nextInt() % (MAX_COORD + 1));
      {
        Vector3 pt = new Vector3(ptX, ptY, ptZ);
        Util.PointProjectPlane(pt, xyPlane);
        assertEquals(ptX, pt.x, 0);
        assertEquals(ptY, pt.y, 0);
        assertEquals(  0, pt.z, 1.0e-5);
      }
      {
        Vector3 pt = new Vector3(ptX, ptY, ptZ);
        Vector3 res = new Vector3();
        Util.PointProjectPlane(pt, xyPlane, res);
        assertEquals(ptX, res.x, 0);
        assertEquals(ptY, res.y, 0);
        assertEquals(  0, res.z, 1.0e-5);
      }
    }
  }

  @Test
  public void testPointProjectPlaneAlongDir() {
    final int MAX_COORD = 500;
    for (int testIdx = 0; testIdx < 100; ++testIdx) {
      final double mx = randGen.nextDouble();
      final double my = randGen.nextDouble();
      final int z = MAX_COORD - (randGen.nextInt() % (MAX_COORD + 1));
      Vector4 xyPlane = new Vector4( 0, 0, 1, 0);
      Vector3 pt = new Vector3(0, 0, z);
      Vector3 dir = (new Vector3(mx, my, -1)).normalize();
      Util.PointProjectPlaneAlongDir(pt, xyPlane, dir);
      assertEquals(mx * z, pt.x, 1.0e-5);
      assertEquals(my * z, pt.y, 1.0e-5);
      assertEquals(     0, pt.z, 1.0e-5);
    }
  }

}

