/*
 * Starfield.java
 *
 * A randomized star field.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/

import java.awt.*;
import java.util.Iterator;
import java.util.Random;

/// <summary> Randomized field of stars without overlap. </summary>
public class Starfield extends Graphics2.SceneOject {
  public Graphics2.Star[] stars = null;

  /// <summary> Create stars in the specified area. </summary>
  public void setup(int count, float meanScale, float varScale, float height) {
    // Anonymous class to generate gauss-distributed sizes.
    final float mu = meanScale;
    final float sig = varScale;
    Iterator<Float> scaleGen = new Iterator<Float>() {
      Random r = new Random(System.currentTimeMillis());
      public boolean hasNext() {
        return true;
      }
      public Float next() {
        return ((float)r.nextGaussian() * sig) + mu;
      }
      public void remove() {
        assert(false);
      }
    };
    // Create stars.
    Random r = new Random(System.currentTimeMillis());
    stars = new Graphics2.Star[count];
    Graphics2.AxisAlignedBoundingBox[] starAabbs =
      new Graphics2.AxisAlignedBoundingBox[count];
    for (int starIdx = 0; starIdx < stars.length; ++starIdx) {
      for (;;) {
        Graphics2.Star star = new Graphics2.Star();
        star.fill = true;
        stars[starIdx] = star;
        // Random scale.
        final float scale = scaleGen.next();
        star.scale.x = scale;
        star.scale.y = scale;
        star.parent = this;
        // Random position.
        final float kIntersectScale = 1.25f;
        star.translation.x = r.nextFloat();
        star.translation.y = -height * r.nextFloat();
        star.rotationRad = (float)Math.PI + (float)(Math.PI * r.nextGaussian() * 0.15);
        // Check no overlap on scaled aabb.
        Graphics2.AxisAlignedBoundingBox starAabb = star.aabb(0).scale(kIntersectScale,
                                                                       kIntersectScale);
        starAabbs[starIdx] = starAabb;
        boolean overlaps = false;
        for (int chkIdx = 0; chkIdx < starIdx; ++chkIdx) {
          if (starAabb.intersects(starAabbs[chkIdx])) {
            overlaps = true;
            break;
          }
        }
        // Done when this star does not overlap.
        if (!overlaps) {
          break;
        }
      }
    }
  }

  @Override protected void onRender(double time, Graphics g) {
    for (Graphics2.Star s : stars) {
      s.render(time, g);
      //System.out.println("pt0: " + s.tformPoints[0]);
    }
  }
}

