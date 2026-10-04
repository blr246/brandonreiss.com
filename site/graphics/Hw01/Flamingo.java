/*
 * Flamingo.java
 *
 * Flamingo geometry and animation class.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
import java.awt.*;
import java.util.Random;
import java.util.Iterator;


/// <summary> A nighttime flamingo. </summary>
public class Flamingo extends Graphics2.SceneOject {
  /// <summary> Interface to animate joints. </summary>
  private interface JointAnimationFunc {
    void animate(Flamingo.Leg leg, float angleRad);
    float getRotation(Flamingo.Leg leg);
  }

  /// <summary> Helper for standing generalization. </summary>
  private enum LegId {
    Left(), Right();

    public float getAnimValue(Vector3 legAnimCtrl) {
      return (Left == this) ? legAnimCtrl.x : legAnimCtrl.y;
    }
    public void setAnimValue(Vector3 legAnimCtrl, float v) {
      if (Left == this) {
        legAnimCtrl.x = v;
      }
      else {
        legAnimCtrl.y = v;
      }
    }
    public void addAnimValue(Vector3 legAnimCtrl, float v) {
      if (Left == this) {
        legAnimCtrl.x += v;
      }
      else {
        legAnimCtrl.y += v;
      }
    }
    public LegId other() {
      return (Left == this) ? Right : Left;
    }
  }

  private class Body extends Graphics2.SceneOject {
    /// <summary> The lower body shape. </summary>
    private class Lower extends Graphics2.Shape {
      Lower() {
        points = new Vector3[] {new Vector3( 0.00f, 0.00f, 1.0f),
                                new Vector3(-0.50f, 0.00f, 1.0f),
                                new Vector3(-0.43f,-0.30f, 1.0f),
                                new Vector3(-0.12f,-0.33f, 1.0f),
                                new Vector3(-0.08f,-0.40f, 1.0f),
                                new Vector3( 0.12f,-0.40f, 1.0f),
                                new Vector3( 0.25f,-0.33f, 1.0f),
                                new Vector3( 0.40f,-0.30f, 1.0f),
                                new Vector3( 0.80f,-0.38f, 1.0f),
                                new Vector3( 0.50f, 0.00f, 1.0f),
        };
        fill = true;
      }
    }

    private Lower lower = new Lower();

    Body() {
      lower.parent = this;
    }

    @Override protected void onRender(double time, Graphics g) {
      // Render the bird hump. The hump the hump the hump.
      {
        Vector3 centerLcl = new Vector3(0, 0, 1);
        Vector3 centerScreen = centerLcl.transformCoord(fullMatrix);
        Vector3 leftLcl = new Vector3(-0.5f, 0, 1);
        Vector3 leftScreen = leftLcl.transformCoord(fullMatrix);
        Vector3 rightLcl = new Vector3(0.5f, 0, 1);
        Vector3 rightScreen = rightLcl.transformCoord(fullMatrix);
        float width = rightScreen.x - leftScreen.x;
        float height = 0.85f * width;
        g.fillArc((int)(centerScreen.x - (width / 2.0f)),
                  (int)(centerScreen.y - (height / 2.2f)),
                  (int)width, (int)height, 0, 180);
        g.drawOval((int)centerScreen.x, (int)centerScreen.y,
                   5, 5);
      }
      lower.render(time, g);
    }
  }

  private class Neck extends Graphics2.Shape {
    Neck() {
      points = new Vector3[] {new Vector3( 0.000f, 0.000f, 1.0f),
                              new Vector3( 0.070f,-0.300f, 1.0f),
                              // Up.
                              new Vector3(-0.180f,-0.200f, 1.0f),
                              new Vector3(-0.270f,-0.100f, 1.0f),
                              new Vector3(-0.310f, 0.000f, 1.0f),
                              new Vector3(-0.280f, 0.200f, 1.0f),
                              new Vector3(-0.180f, 0.400f, 1.0f),
                              new Vector3(-0.070f, 0.600f, 1.0f),
                              new Vector3( 0.000f, 0.800f, 1.0f),
                              new Vector3( 0.050f, 1.100f, 1.0f),
                              new Vector3( 0.040f, 1.300f, 1.0f),
                              new Vector3( 0.000f, 1.400f, 1.0f),
                              // Back down.
                              new Vector3( 0.100f, 1.400f, 1.0f),
                              new Vector3( 0.140f, 1.300f, 1.0f),
                              new Vector3( 0.150f, 1.100f, 1.0f),
                              new Vector3( 0.130f, 0.800f, 1.0f),
                              new Vector3( 0.070f, 0.600f, 1.0f),
                              new Vector3(-0.020f, 0.400f, 1.0f),
                              new Vector3(-0.090f, 0.200f, 1.0f),
                              new Vector3(-0.100f, 0.070f, 1.0f),
                              new Vector3(-0.060f,-0.020f, 1.0f),
                              new Vector3(-0.015f,-0.025f, 1.0f),
                              new Vector3(-0.010f,-0.020f, 1.0f),
      };
      fill = true;
    }
  }

  private class Head extends Graphics2.SceneOject {
    private class Eye extends Graphics2.SceneOject {
      private Graphics2.Circle cornea = new Graphics2.Circle();

      private Color colorCornea = Color.yellow.darker().darker();

      Eye() {
        cornea.parent = this;
        cornea.fill = true;
      }

      @Override protected void onRender(double time, Graphics g) {
        Color colorPre = g.getColor();
        g.setColor(colorCornea);
        cornea.render(time, g);
        g.setColor(colorPre);
      }
    }

    private class Beak extends Graphics2.SceneOject {
      private class BeakTip extends Graphics2.Shape {
        BeakTip() {
          points = new Vector3[] {new Vector3(-0.50f,-0.50f, 1.0f),
                                  // Mouth.
                                  new Vector3(-0.40f,-0.19f, 1.0f),
                                  new Vector3(-0.10f,-0.17f, 1.0f),
                                  new Vector3( 0.20f,-0.02f, 1.0f),
                                  new Vector3( 0.20f, 0.04f, 1.0f),
                                  new Vector3(-0.10f,-0.12f, 1.0f),
                                  new Vector3(-0.40f,-0.10f, 1.0f),
                                  // Mouth end.
                                  new Vector3(-0.50f, 0.50f, 1.0f),
                                  new Vector3(-1.00f, 0.50f, 1.0f),
                                  new Vector3(-1.50f, 0.80f, 1.0f),
                                  new Vector3(-1.60f, 0.85f, 1.0f),
                                  new Vector3(-1.70f, 0.65f, 1.0f),
                                  new Vector3(-1.30f,-0.35f, 1.0f),
          };
          fill = true;
        }
      }

      private Graphics2.Box box = new Graphics2.Box();
      private BeakTip beakTip = new BeakTip();

      Beak() {
        box.parent = this;
        box.scale.set(0.8f, 0.6f, 1.0f);
        box.translation.set(-0.5f, 0.10f, 1.0f);
        box.fill = true;

        beakTip.parent = box;
        beakTip.fill = true;
      }

      @Override protected void onRender(double time, Graphics g) {
        box.render(time, g);
        // Beak tip is black.
        Color c = g.getColor();
        g.setColor(Color.black);
        beakTip.render(time, g);
        g.setColor(c);
      }
    }

    private Graphics2.Circle upperHead = new Graphics2.Circle();
    private Beak beak = new Beak();
    private Eye eye = new Eye();

    Head() {
      upperHead.parent = this;
      upperHead.fill = true;

      beak.parent = upperHead;

      eye.parent = upperHead;
      eye.scale.set(0.15f, 0.15f, 1);
      eye.translation.set(-0.1f, 0.06f, 1.0f);
    }

    @Override protected void onRender(double time, Graphics g) {
      upperHead.render(time, g);
      beak.render(time, g);
      eye.render(time, g);
    }
  }

  private class Leg extends Graphics2.SceneOject {
    private class LegBox extends Graphics2.Shape {
      LegBox() {
        points = new Vector3[] {new Vector3( 0.0125f, 0.0f, 1.0f),
                                new Vector3(-0.0125f, 0.0f, 1.0f),
                                new Vector3(-0.0125f, -0.5f, 1.0f),
                                new Vector3( 0.0125f, -0.5f, 1.0f),
        };
      }
    }
    private class Toes extends Graphics2.Shape {
      Toes() {
        points = new Vector3[] {new Vector3( 0.0f, 0.0f, 1.0f),
                                new Vector3( 0.3f, 0.5f, 1.0f),
                                new Vector3(-0.3f, 0.5f, 1.0f),
        };
      }
    }
    private LegBox lowerLeg = new LegBox();
    private LegBox foot = new LegBox();
    private Toes toes = new Toes();
    private Graphics2.Circle ankle = new Graphics2.Circle();
    private Graphics2.Circle toeJoint = new Graphics2.Circle();
    private float rotationKnee = 0;
    private float rotationAnkle = 0;
    Leg() {
      lowerLeg.parent = this;
      lowerLeg.fill = true;
      foot.parent = lowerLeg;
      foot.translation.set(0, -0.5f, 1.0f);
      foot.rotationRad = (float)Math.PI;
      foot.fill = true;
      ankle.parent = foot;
      ankle.scale.set(0.05f, 0.05f, 1.0f);
      ankle.fill = true;
      toes.parent = foot;
      toes.fill = true;
      toes.translation.set(0, -0.5f, 1.0f);
      toes.scale.set(0.2f, 0.2f, 1.0f);
      toes.rotationRad = 0.7f;
      toeJoint.parent = foot;
      toeJoint.scale.set(0.05f, 0.05f, 1.0f);
      toeJoint.translation.set(0.0f,-0.5f, 1.0f);
      toeJoint.fill = true;
    }
    public void rotateKnee(float angleRad) {
      rotationKnee = angleRad;
      lowerLeg.rotationRad = -angleRad;
    }
    public float getRotationKnee() {
      return rotationKnee;
    }
    public void rotateAnkle(float angleRad) {
      rotationAnkle = angleRad;
      foot.rotationRad = (float)Math.PI + angleRad;
    }
    public float getRotationAnkle() {
      return rotationAnkle;
    }
    @Override protected void onRender(double time, Graphics g) {
      lowerLeg.render(time, g);
      foot.render(time, g);
      ankle.render(time, g);
      toes.render(time, g);
      toeJoint.render(time, g);
    }

  }

  private Body body = new Body();
  private Neck neck = new Neck();
  private Head head = new Head();
  private Leg leftLeg = new Leg();
  private Leg rightLeg = new Leg();

  /// <summary> Stand leg left knee angles (L, R, X). </summary>
  private Vector3 standLKneeAngles = new Vector3(0.14f, 0.39f, 0.0f);
  /// <summary> Stand leg left ankle angles (L, R, X). </summary>
  private Vector3 standLAnkleAngles = new Vector3(-0.24f, (float)-(Math.PI * 0.51), 0.0f);
  /// <summary> Stand leg right knee angles (L, R, X). </summary>
  private Vector3 standRKneeAngles = new Vector3(0.3f, 0.07f, 0.0f);
  /// <summary> Stand leg right ankle angles (L, R, X). </summary>
  private Vector3 standRAnkleAngles = new Vector3((float)-(Math.PI * 0.5), -0.3f, 0.0f);
  /// <summary> Leg animation knee angle initial (L, R, X). </summary>
  private Vector3 kneeAnimationInit = standRKneeAngles.clone();
  /// <summary> Leg animation ankle angle initial (L, R, X). </summary>
  private Vector3 ankleAnimationInit = standRAnkleAngles.clone();
  /// <summary> Leg animation knee angle targets (L, R, X). </summary>
  private Vector3 kneeAnimationTargets = standRKneeAngles.clone();
  /// <summary> Leg animation ankle angle targets (L, R, X). </summary>
  private Vector3 ankleAnimationTargets = standRAnkleAngles.clone();

  // Animation controls.
  private boolean animatingLegs = false;
  private LegId standState = LegId.Left;
  private float kneeAnimSpeedBase = 0.5f;
  private float ankleAnimSpeedBase = 2.0f;
  private Vector3 kneeAnimSpeed = Vector3.zero();
  private Vector3 kneeAnimStartTimes = Vector3.zero();
  private Vector3 kneeAnimDueTimes = Vector3.zero();
  private Vector3 ankleAnimSpeed = Vector3.zero();
  private Vector3 ankleAnimStartTimes = Vector3.zero();
  private Vector3 ankleAnimDueTimes = Vector3.zero();

  // Head animation controls.
  private boolean animatingHead = false;
  private float headAnimInit = 0;
  private float headAnimTarget = 0;
  private float headAnimSpeed = 0;
  private double headAnimStartTime = 0;
  private double headAnimDueTime = 0;

  private Random rng = new Random(System.currentTimeMillis());

  Flamingo() {
    body.parent = this;
    body.scale.set(0.30f, 0.30f, 1.0f);
    body.translation.set(0.0f, 0.125f, 1.0f);

    neck.parent = body;
    neck.translation.set(-0.5f, 0.0f, 1.0f);

    head.parent = neck;
    head.scale.set(0.25f, 0.25f, 1);
    head.translation.set(-0.030f, 1.380f, 1.0f);

    leftLeg.parent = this;
    leftLeg.scale.set(0.55f, 0.55f, 1.0f);
    leftLeg.translation.set(-0.01f, 0.02f, 1.0f);
    leftLeg.rotateKnee(kneeAnimationTargets.x);
    leftLeg.rotateAnkle(ankleAnimationTargets.x);

    rightLeg.parent = this;
    rightLeg.scale.set(0.55f, 0.55f, 1.0f);
    rightLeg.translation.set(0.02f, 0.02f, 1.0f);
    rightLeg.rotateKnee(kneeAnimationTargets.y);
    rightLeg.rotateAnkle(ankleAnimationTargets.y);

    // Set standing invariants using function interface. Two calls
    // prevents triggering a leg switch.
    standRight(0);
    standRight(0);
  }

  public float sign(float v) {
    return (v >= 0.0f) ? 1.0f : -1.0f;
  }
  public float gaussScale(float var) {
    return 1.0f + ((float)rng.nextGaussian() * var);
  }

  class KneeAnimator implements JointAnimationFunc {
    @Override public void animate(Leg leg, float angleRad) {
      leg.rotateKnee(angleRad);
    }
    @Override public float getRotation(Leg leg) {
      return leg.getRotationKnee();
    }
  }
  class AnkleAnimator implements JointAnimationFunc {
    @Override public void animate(Leg leg, float angleRad) {
      leg.rotateAnkle(angleRad);
    }
    @Override public float getRotation(Leg leg) {
      return leg.getRotationAnkle();
    }
  }

  /// <summary> Apply rotation to the given joint. </summary>
  public int animateJoint(double time,
                          Vector3 startTimes, Vector3 dueTimes,
                          Vector3 initial, Vector3 targets,
                          Vector3 speeds,
                          JointAnimationFunc f) {
    int doneCount = 0;
    Vector3 elapsed = new Vector3((float)time - startTimes.x,
                                  (float)time - startTimes.y, 1.0f);
    Vector3 offset = speeds.mul(elapsed);
    Vector3 nowRaw = initial.add(offset);
    // Left.
    if (time < dueTimes.x) {
      if (startTimes.x < time) {
        f.animate(leftLeg, nowRaw.x);
      }
    }
    else {
      f.animate(leftLeg, targets.x);
      ++doneCount;
    }
    // Right.
    if (time < dueTimes.y) {
      if (startTimes.y < time) {
        f.animate(rightLeg, nowRaw.y);
      }
    }
    else {
      f.animate(rightLeg, targets.y);
      ++doneCount;
    }
    return doneCount;
  }

  public boolean animatingLegs() {
    return animatingLegs;
  }
  public boolean standingLeft() {
    return standState == LegId.Left;
  }
  public boolean standingRight() {
    return !standingLeft();
  }

  /// <summary> Update animated segments. </summary>
  public void updateAnimation(double time) {
    if (animatingLegs) {
      int doneCount = 0;
      doneCount += animateJoint(time,
                                kneeAnimStartTimes, kneeAnimDueTimes,
                                kneeAnimationInit, kneeAnimationTargets,
                                kneeAnimSpeed, new KneeAnimator());
      doneCount += animateJoint(time,
                                ankleAnimStartTimes, ankleAnimDueTimes,
                                ankleAnimationInit, ankleAnimationTargets,
                                ankleAnimSpeed, new AnkleAnimator());
      if (4 == doneCount) {
        animatingLegs = false;
      }
    }
    if (animatingHead) {
      float elapsed = (float)(time - headAnimStartTime);
      float angleNow = headAnimInit + (headAnimSpeed * elapsed);
      if ((headAnimStartTime <= time) && (time < headAnimDueTime)) {
        head.rotationRad = angleNow;
      }
      else {
        head.rotationRad = headAnimTarget;
        animatingHead = false;
      }
    }
  }

  /// <summary> Generic method to setup animation tracks. </summary>
  private void prepareJointAnimation(float scaleVar, JointAnimationFunc f,
                                     Vector3 initial, Vector3 targets,
                                     Vector3 targetsBase, Vector3 speeds,
                                     float speedBase, Vector3 durations) {
    Vector3 scale = new Vector3(gaussScale(scaleVar), gaussScale(scaleVar), 1.0f);
    initial.set(f.getRotation(leftLeg), f.getRotation(rightLeg), 1.0f);
    targets.set(targetsBase.mul(scale));
    Vector3 animAmt = targets.sub(initial);
    speeds.set(speedBase * sign(animAmt.x) * gaussScale(scaleVar),
               speedBase * sign(animAmt.y) * gaussScale(scaleVar),
               1.0f);
    durations.set(animAmt.div(speeds));
  }

  /// <summary> Generic method to switch legs or to shift current leg. </summary>
  private void stand(double time, LegId legId) {
    // Initialize.
    float kneeInit = legId.getAnimValue(kneeAnimationTargets);
    float ankleInit = legId.getAnimValue(ankleAnimationTargets);
    // Knee setup.
    prepareJointAnimation(0.1f, new KneeAnimator(), kneeAnimationInit,
                          kneeAnimationTargets,
                          (LegId.Left == legId) ? standLKneeAngles : standRKneeAngles,
                          kneeAnimSpeed, kneeAnimSpeedBase, kneeAnimDueTimes);
    // Ankle setup.
    prepareJointAnimation(0.1f, new AnkleAnimator(), ankleAnimationInit,
                          ankleAnimationTargets,
                          (LegId.Left == legId) ? standLAnkleAngles : standRAnkleAngles,
                          ankleAnimSpeed, ankleAnimSpeedBase, ankleAnimDueTimes);

    // Set animation controls.
    legId.setAnimValue(kneeAnimStartTimes, (float)time);
    legId.setAnimValue(ankleAnimStartTimes, (float)time);
    // Dont move when already on this leg.
    if (legId == standState) {
      legId.addAnimValue(kneeAnimDueTimes, (float)time - 0.1f);
      legId.addAnimValue(ankleAnimDueTimes, (float)time - 0.1f);
      legId.setAnimValue(kneeAnimationTargets, kneeInit);
      legId.setAnimValue(ankleAnimationTargets, ankleInit);
    }
    else {
      // Make this leg go down.
      legId.addAnimValue(kneeAnimDueTimes, legId.getAnimValue(kneeAnimStartTimes));
      legId.addAnimValue(ankleAnimDueTimes, legId.getAnimValue(ankleAnimStartTimes));
    }
    // Make other leg go up.
    float standLegEndTime = Math.max(legId.getAnimValue(kneeAnimDueTimes),
                                     legId.getAnimValue(ankleAnimDueTimes));
    LegId other = legId.other();
    other.setAnimValue(kneeAnimStartTimes, standLegEndTime + 1.0f * gaussScale(0.1f));
    other.addAnimValue(kneeAnimDueTimes, other.getAnimValue(kneeAnimStartTimes));
    other.setAnimValue(ankleAnimStartTimes, standLegEndTime + 1.0f * gaussScale(0.1f));
    other.addAnimValue(ankleAnimDueTimes, other.getAnimValue(ankleAnimStartTimes));

    // Set flags.
    standState = legId;
    animatingLegs = true;
  }

  public void standLeft(double time) {
    stand(time, LegId.Left);
  }

  public void standRight(double time) {
    stand(time, LegId.Right);
  }

  public void animateHead(double time, float speed, float target) {
    headAnimStartTime = time;
    headAnimSpeed = speed;
    headAnimInit = head.rotationRad;
    headAnimTarget = target;
    float travelDist = headAnimTarget - headAnimInit;
    double duration = (double)(travelDist / headAnimSpeed);
    if (duration < 0) {
      duration *= -1;
      headAnimSpeed *= -1.0f;
    }
    headAnimDueTime = time + duration;
    animatingHead = true;
  }

  @Override protected void onRender(double time, Graphics g) {
    body.render(time, g);
    neck.render(time, g);
    head.render(time, g);
    leftLeg.render(time, g);
    rightLeg.render(time, g);
  }
}

