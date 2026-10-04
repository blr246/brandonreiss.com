import org.junit.*;
import static org.junit.Assert.*;
import com.brandonreiss.math.*;

public class Vector4Test {
  @Test
  public void testCtor() {
    // Copy.
    {
      Vector4 zero = Vector4.zero();
      Vector4 v = new Vector4(zero);
      assertEquals(zero, v);
    }
    // Value.
    {
      Vector4 v = new Vector4(0, 1, 2, 3);
      assertEquals(0, v.x, 0);
      assertEquals(1, v.y, 0);
      assertEquals(2, v.z, 0);
      assertEquals(3, v.w, 0);
    }
  }

  @Test
  public void testZero() {
    Vector4 v = Vector4.zero();
    assertEquals(0, v.x, 0);
    assertEquals(0, v.y, 0);
    assertEquals(0, v.z, 0);
    assertEquals(0, v.w, 0);
  }

  @Test
  public void testReplicate() {
    Vector4 v = Vector4.replicate(0);
    assertEquals(Vector4.zero(), v);
  }

  @Test
  public void testSet() {
    // Value.
    {
      Vector4 v = new Vector4();
      v.set(0, 1, 2, 3);
      assertEquals(0, v.x, 0);
      assertEquals(1, v.y, 0);
      assertEquals(2, v.z, 0);
      assertEquals(3, v.w, 0);
    }
    // Vector.
    {
      Vector4 v = new Vector4();
      v.set(Vector4.zero());
      assertEquals(Vector4.zero(), v);
    }
  }

  @Test
  public void testAddSubMulDiv() {
    // Some identities.
    {
      Vector4 zero = Vector4.zero();
      Vector4 one = new Vector4(1, 1, 1, 1);
      Vector4 v = new Vector4(1, 2, 3, 4);
      assertEquals(zero, v.sub(v));
      assertEquals(zero, v.mul(0));
      assertEquals(zero, v.mul(zero));
      assertEquals(one, v.div(v));
      assertEquals(v, v.mul(1));
      assertEquals(v, v.mul(one));
      assertEquals(v, v.div(1));
      assertEquals(v, v.div(one));
      assertEquals(v.mul(2), v.add(v));
      assertEquals(v.mul(2).div(2), v);
      assertEquals(v.mul(-1), zero.sub(v));
    }
  }

  @Test
  public void testDot() {
    // Ortho tests.
    {
      {
        Vector4 a = new Vector4(1, 0, 0, 0);
        Vector4 b = new Vector4(0, 0, 1, 0);
        assertEquals(0, a.dot(b), 0);
        assertEquals(0, b.dot(a), 0);
      }
      {
        Vector4 a = new Vector4( 0.5, 0.5, 0.5, 0.5);
        Vector4 b = new Vector4(-0.5, 0.5,-0.5, 0.5);
        assertEquals(0, a.dot(b), 0);
        assertEquals(0, b.dot(a), 0);
      }
    }
  }
}

