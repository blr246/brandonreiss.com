/*
 * Vector4Test.java
 *
 * JUnit tests for Vector4 class.
*/
import org.junit.*;
import static org.junit.Assert.*;
import com.brandonreiss.math.*;
import java.util.Random;

public class Vector4Test {
  @Test
  public void testCtor() {
    // Copy.
    {
      Vector4 zero = Vector4.vectorZero();
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
    {
      Vector4 v = Vector4.vectorZero();
      assertEquals(0, v.x, 0);
      assertEquals(0, v.y, 0);
      assertEquals(0, v.z, 0);
      assertEquals(0, v.w, 0);
    }
    {
      Vector4 v = new Vector4(1, 2, 3, 4);
      v.zero();
      assertEquals(0, v.x, 0);
      assertEquals(0, v.y, 0);
      assertEquals(0, v.z, 0);
      assertEquals(0, v.w, 0);
    }
  }

  @Test
  public void testReplicate() {
    {
      Vector4 v = Vector4.vectorReplicate(0);
      assertEquals(Vector4.vectorZero(), v);
    }
    {
      Vector4 v = new Vector4(1, 2, 3, 4);
      v.replicate(0);
      assertEquals(Vector4.vectorZero(), v);
    }
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
      v.set(Vector4.vectorZero());
      assertEquals(Vector4.vectorZero(), v);
    }
  }

  @Test
  public void testAddSubMulDiv() {
    // Some identities.
    {
      Vector4 zero = Vector4.vectorZero();
      Vector4 one = new Vector4(1, 1, 1, 1);
      Vector4 v = new Vector4(1, 2, 3, 4);
      assertEquals(zero, v.clone().sub(v));
      assertEquals(zero, v.clone().mul(0));
      assertEquals(zero, v.clone().mul(zero));
      assertEquals(one, v.clone().div(v));
      assertEquals(v, v.clone().mul(1));
      assertEquals(v, v.clone().mul(one));
      assertEquals(v, v.clone().div(1));
      assertEquals(v, v.clone().div(one));
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

  @Test
  public void testLengthAndNormalize() {
    final int NUM_LEN_TESTS = 1000;
    Random rng = new Random(System.currentTimeMillis());
    for (int testNum = 0; testNum < NUM_LEN_TESTS; ++testNum) {
      Vector4 randVec = new Vector4(rng.nextDouble(), rng.nextDouble(),
                                    rng.nextDouble(), rng.nextDouble());
      final double len = randVec.length();
      // Divide by length.
      Vector4 divLengthClone = randVec.clone().div(len);
      // Normalize it.
      randVec.normalize();
      // All should be length 1 and equal.
      assertEquals(1, randVec.length(), 1.0e-8);
      assertEquals(1, randVec.lengthSq(), 1.0e-8);
      assertEquals(0, divLengthClone.clone().sub(randVec).lengthSq(), 1.0e-8);
    }
  }
}

