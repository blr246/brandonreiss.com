/*
 * Hw01.java
 *
 * A flamingo rests alone in the gently lapping water against the starry night.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
import java.awt.*;
import java.util.Random;

/// <summary> Displays a Flamingo under the starry night sky. </summary>
public class Hw01 extends BufferedApplet {
  /// <summary> Helper to rotate stars different speeds. </summary>
  private class StarRotationGroup {
    private Graphics2.Star[][] groups = null;
    private float [] speeds = null;
    private int groupSize = 0;
    private int numGroups = 0;

    StarRotationGroup(Starfield sf, int numGroups_) {
      final int numStars = sf.stars.length;
      numGroups = numGroups_;
      groupSize = (numStars + numGroups - 1) / numGroups;
      groups = new Graphics2.Star[numGroups][groupSize];
      speeds = new float[numGroups];
      for (int starIdx = 0; starIdx < numStars; ++starIdx) {
        int groupIdx = starIdx % numGroups;
        int starPos = starIdx / numGroups;
        groups[groupIdx][starPos] = sf.stars[starIdx];
        speeds[groupIdx] = 1.0f + (float)rng.nextGaussian();
      }
      for (int starIdx = numStars; starIdx < (numGroups * groupSize); ++starIdx) {
        int groupIdx = starIdx % numGroups;
        int starPos = starIdx / numGroups;
        groups[groupIdx][starPos] = null;
      }
    }

    public void animate(double time) {
      for (int grpIdx = 0; grpIdx < numGroups; ++grpIdx) {
        float speed = speeds[grpIdx];
        float starSpin = (float)Math.cos(speed * time);
        for (int starIdx = 0; starIdx < groupSize; ++starIdx) {
          Graphics2.Star s = groups[grpIdx][starIdx];
          if (null != s) {
            s.scale.x = s.scale.y * starSpin;
          }
        }
      }
    }
  }

  // Graphics members.
  private final double startTime = System.currentTimeMillis() / 1000.0;
  private Starfield starfield = new Starfield();
  private Flamingo flamingo = new Flamingo();
  private Random rng = new Random((long)startTime);
  private double timeNextMoveLegs = 0;
  private double timeNextMoveHead = 0;
  private Color colorSky = Color.blue.darker().darker().darker().darker();
  private Color colorStars = Color.yellow;
  private Color colorWater = new Color(1.0f/255.0f, 10.0f/255.0f, 35.0f/255.0f);
  private Color colorFlamingo = new Color(98.0f/255.0f, 65.0f/255.0f, 65.0f/255.0f);
  private int[] appletSizeCache = new int[] {0, 0};
  private StarRotationGroup starRotationGroup = null;
  private final int NUM_STAR_ROTATION_GROUPS = 20;

  public void render(Graphics g) {
    final double time = getTime();
    final int w = getWidth();
    final int h = getHeight();

    float flamingoScale = (float)h * 0.8f;
    flamingo.scale.set(flamingoScale, flamingoScale, 1.0f);
    flamingo.translation.set((w / 2.0f) + (w / 6.0f), h / 1.9f, 1.0f);

    // Random leg animations.
    final double kLegAnimDelayBase = 5.0;
    double timeUntilMoveLegs = timeNextMoveLegs - time;
    if (!flamingo.animatingLegs() && (timeUntilMoveLegs <= 0)) {
      // Either shift or switch legs.
      final float kShiftProbability = 0.8f;
      if (rng.nextFloat() < kShiftProbability) {
        // Shift.
        if (flamingo.standingLeft()) {
          flamingo.standLeft(time);
        }
        else {
          flamingo.standRight(time);
        }
      }
      else {
        // Switch.
        if (flamingo.standingLeft()) {
          flamingo.standRight(time);
        }
        else {
          flamingo.standLeft(time);
        }
      }
      // Randomize next move.
      timeNextMoveLegs = time + (kLegAnimDelayBase * (1.0 + (rng.nextGaussian() * 0.5)));
    }

    // Random head animations.
    final double kHeadAnimDelayBase = 3.0;
    double timeUntilMoveHead = timeNextMoveHead - time;
    if (timeUntilMoveHead <= 0) {
      // Select head rotation.
      final float kHeadRotBase = 0.8f;
      final float kHeadRotSpeedBase = 0.3f;
      float angleRad = kHeadRotBase * (1.0f + (float)(rng.nextGaussian() * 0.2));
      float speed = kHeadRotSpeedBase * (1.0f + (float)(rng.nextGaussian() * 0.1));
      flamingo.animateHead(time, speed, angleRad);
      // Randomize next move.
      timeNextMoveHead = time + (kHeadAnimDelayBase * (1.0 + (rng.nextGaussian() * 0.5)));
    }

    g.setColor(colorSky);
    g.fillRect(0, 0, w, h);

    // Render front water.
    float waterRipple = (float)Math.cos(time * 1.75);
    g.setColor(colorWater);
    int horizonStart = (int)(h * 0.4);
    int waterHt = h - horizonStart;
    int waterHtFront = (int)(waterHt * (0.3 + (0.035 * waterRipple)));
    int waterHtBack = waterHt - waterHtFront;
    g.fillRect(0, horizonStart, w, waterHtBack);

    // Update stars?
    starfield.scale.set((float)w, (float)w, 1.0f);
    if ((appletSizeCache[0] != w) || (appletSizeCache[1] != h)) {
      // Initialize star field.
      float aspect = (float)h / (float)w;
      starfield.setup(500, 0.004f, 0.007f, aspect * 0.38f);
      appletSizeCache[0] = w; appletSizeCache[1] = h;
      starRotationGroup = new StarRotationGroup(starfield, NUM_STAR_ROTATION_GROUPS);
    }
    // Spin stars.
    starRotationGroup.animate(time);
    // Render stars.
    g.setColor(colorStars);
    starfield.render(time, g);

    // Render birdie.
    g.setColor(colorFlamingo);
    flamingo.updateAnimation(time);
    flamingo.render(time, g);

    // Front water over bird.
    g.setColor(colorWater);
    g.fillRect(0, horizonStart + waterHtBack, w, waterHtFront);
  }

  /// <summary> Get time in seconds since this instance was created. </summary>
  public double getTime() {
    return (System.currentTimeMillis() / 1000.0) - startTime;
  }
}

