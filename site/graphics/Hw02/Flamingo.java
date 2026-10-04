/*
 * Flamingo.java
 *
 * Flamingo geometry and animation class.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/

package com.brandonreiss.geometry;

import com.brandonreiss.math.*;
import java.awt.*;
import java.util.Random;
import java.util.Iterator;

/// <summary> An animatable flamingo surface. </summary>
public class Flamingo extends SceneObject {
  /// <summary> Interface to animate joints. </summary>
  private interface JointAnimationFunc {
    void animate(Flamingo.Leg leg, double angleRad);
    double getRotation(Flamingo.Leg leg);
  }

  /// <summary> Helper for standing generalization. </summary>
  private enum LegId {
    Left(), Right();

    public double getAnimValue(Vector2 legAnimCtrl) {
      return (Left == this) ? legAnimCtrl.x : legAnimCtrl.y;
    }
    public void setAnimValue(Vector2 legAnimCtrl, double v) {
      if (Left == this) {
        legAnimCtrl.x = v;
      }
      else {
        legAnimCtrl.y = v;
      }
    }
    public void addAnimValue(Vector2 legAnimCtrl, double v) {
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

  private class Body extends SceneObject {
    /// <summary> The lower body shape. </summary>
    private class Lower extends Surface {
      Lower() {
        vertices = new Vector4[] {new Vector4( 0.00, 0.00, 0.0, 1.0),
                                  new Vector4(-0.50, 0.00, 0.0, 1.0),
                                  new Vector4(-0.43,-0.30, 0.0, 1.0),
                                  new Vector4(-0.12,-0.33, 0.0, 1.0),
                                  new Vector4(-0.08,-0.40, 0.0, 1.0),
                                  new Vector4( 0.12,-0.40, 0.0, 1.0),
                                  new Vector4( 0.25,-0.33, 0.0, 1.0),
                                  new Vector4( 0.40,-0.30, 0.0, 1.0),
                                  new Vector4( 0.80,-0.38, 0.0, 1.0),
                                  new Vector4( 0.50, 0.00, 0.0, 1.0),
        };
        updateAllocations();
      }
    }

    private Lower lower = new Lower();
    private Arc hump = new Arc(0.0, Math.PI);

    Body() {
      lower.setParent(this);
      hump.setParent(this);
      hump.getTransformLocal().scale(1.0, 0.85, 1.0);
    }

    @Override
    public void render(Renderer r) {
      lower.render(r);
      hump.render(r);
    }
  }

  private class Neck extends Surface {
    Neck() {
      vertices = new Vector4[] {new Vector4( 0.000, 0.000, 0.0, 1.0),
                                new Vector4( 0.070,-0.300, 0.0, 1.0),
                                // Up.
                                new Vector4(-0.180,-0.200, 0.0, 1.0),
                                new Vector4(-0.270,-0.100, 0.0, 1.0),
                                new Vector4(-0.310, 0.000, 0.0, 1.0),
                                new Vector4(-0.280, 0.200, 0.0, 1.0),
                                new Vector4(-0.180, 0.400, 0.0, 1.0),
                                new Vector4(-0.070, 0.600, 0.0, 1.0),
                                new Vector4( 0.000, 0.800, 0.0, 1.0),
                                new Vector4( 0.050, 1.100, 0.0, 1.0),
                                new Vector4( 0.040, 1.300, 0.0, 1.0),
                                new Vector4( 0.000, 1.400, 0.0, 1.0),
                                // Back down.
                                new Vector4( 0.100, 1.400, 0.0, 1.0),
                                new Vector4( 0.140, 1.300, 0.0, 1.0),
                                new Vector4( 0.150, 1.100, 0.0, 1.0),
                                new Vector4( 0.130, 0.800, 0.0, 1.0),
                                new Vector4( 0.070, 0.600, 0.0, 1.0),
                                new Vector4(-0.020, 0.400, 0.0, 1.0),
                                new Vector4(-0.090, 0.200, 0.0, 1.0),
                                new Vector4(-0.100, 0.070, 0.0, 1.0),
                                new Vector4(-0.060,-0.020, 0.0, 1.0),
                                new Vector4(-0.015,-0.025, 0.0, 1.0),
                                new Vector4(-0.010,-0.020, 0.0, 1.0),
      };
      updateAllocations();
    }
  }

  private class Head extends SceneObject {
    private class Eye extends SceneObject {
      private Circle cornea = new Circle();

      private Color colorCornea = Color.yellow.darker().darker();

      Eye() {
        cornea.setParent(this);
      }

      @Override
      public void render(Renderer r) {
        Graphics g = r.g;
        Color colorPre = g.getColor();
        g.setColor(colorCornea);
        cornea.render(r);
        g.setColor(colorPre);
      }
    }

    private class Beak extends SceneObject {
      private class BeakTip extends Surface {
        BeakTip() {
          vertices = new Vector4[] {new Vector4(-0.50, 0.50, 0.0, 1.0),
                                    // Mouth.
                                    new Vector4(-0.40, 0.19, 0.0, 1.0),
                                    new Vector4(-0.10, 0.17, 0.0, 1.0),
                                    new Vector4( 0.20, 0.02, 0.0, 1.0),
                                    new Vector4( 0.20,-0.04, 0.0, 1.0),
                                    new Vector4(-0.10, 0.12, 0.0, 1.0),
                                    new Vector4(-0.40, 0.10, 0.0, 1.0),
                                    // Mouth end.
                                    new Vector4(-0.50,-0.50, 0.0, 1.0),
                                    new Vector4(-1.00,-0.50, 0.0, 1.0),
                                    new Vector4(-1.50,-0.80, 0.0, 1.0),
                                    new Vector4(-1.60,-0.85, 0.0, 1.0),
                                    new Vector4(-1.70,-0.65, 0.0, 1.0),
                                    new Vector4(-1.30, 0.35, 0.0, 1.0),
          };
          updateAllocations();
        }
      }

      private Box box = new Box();
      private BeakTip beakTip = new BeakTip();

      Beak() {
        box.setParent(this);
        {
          Matrix4x4 boxTform = box.getTransformLocal();
          boxTform.scale(0.8, 0.6, 1.0);
          boxTform.translate(-0.49,-0.10, 0.0);
        }

        beakTip.setParent(box);
      }

      @Override
      public void render(Renderer r) {
        box.render(r);
        // Beak tip is black.
        {
          Graphics g = r.g;
          Color c = g.getColor();
          g.setColor(Color.black);
          beakTip.render(r);
          g.setColor(c);
        }
      }
    }

    private Circle upperHead = new Circle();
    private Beak beak = new Beak();
    private Eye eye = new Eye();
    public double rotationRad = 0;

    Head() {
      upperHead.setParent(this);

      beak.setParent(upperHead);

      eye.setParent(upperHead);
      {
        Matrix4x4 eyeTform = eye.getTransformLocal();
        eyeTform.scale(0.15, 0.15, 1);
        eyeTform.translate(-0.1, 0.06, 0.0);
      }
    }

    @Override
    public void render(Renderer r) {
      upperHead.render(r);
      beak.render(r);
      eye.render(r);
    }
  }

  private class Leg extends SceneObject {
    private class LegBox extends Surface {
      LegBox() {
        vertices = new Vector4[] {new Vector4( 0.0125, 0.0, 0.0, 1.0),
                                  new Vector4(-0.0125, 0.0, 0.0, 1.0),
                                  new Vector4(-0.0125,-0.5, 0.0, 1.0),
                                  new Vector4( 0.0125,-0.5, 0.0, 1.0),
        };
        updateAllocations();
      }
    }
    private class Toes extends Surface {
      Toes() {
        vertices = new Vector4[] {new Vector4( 0.0, 0.0, 0.0, 1.0),
                                  new Vector4( 0.3, 0.5, 0.0, 1.0),
                                  new Vector4(-0.3, 0.5, 0.0, 1.0),
        };
        updateAllocations();
      }
    }
    private LegBox lowerLeg = new LegBox();
    private LegBox foot = new LegBox();
    private Toes toes = new Toes();
    private Circle ankle = new Circle();
    private Circle toeJoint = new Circle();
    private double rotationKnee = 0;
    private double rotationAnkle = 0;

    Leg() {
      lowerLeg.setParent(this);
      foot.setParent(lowerLeg);
      {
        Matrix4x4 footTform = foot.getTransformLocal();
        footTform.translate(0,-0.5, 0.0);
      }
      ankle.setParent(foot);
      {
        Matrix4x4 ankleTform = ankle.getTransformLocal();
        ankleTform.scale(0.05, 0.05, 1.0);
      }
      toes.setParent(foot);
      {
        Matrix4x4 toesTform = toes.getTransformLocal();
        toesTform.scale(0.2, 0.2, 1.0);
        toesTform.rotateZ(1.4);
        toesTform.translate(0,-0.5, 0.0);
      }
      toeJoint.setParent(foot);
      {
        Matrix4x4 toeJointTform = toeJoint.getTransformLocal();
        toeJointTform.scale(0.05, 0.05, 1.0);
        toeJointTform.translate(0.0,-0.5, 0.0);
      }
    }

    public void rotateKnee(double angleRad) {
      rotationKnee = angleRad;
      Matrix4x4 lowerLegTform = lowerLeg.getTransformLocal();
      lowerLegTform.identity();
      lowerLegTform.rotateZ(-angleRad);
    }

    public double getRotationKnee() {
      return rotationKnee;
    }

    public void rotateAnkle(double angleRad) {
      rotationAnkle = angleRad;
      Matrix4x4 footTform = foot.getTransformLocal();
      footTform.identity();
      footTform.rotateZ(angleRad);
      footTform.translate(0.0,-0.5, 0.0);
    }

    public double getRotationAnkle() {
      return rotationAnkle;
    }

    @Override
    public void render(Renderer r) {
      lowerLeg.render(r);
      foot.render(r);
      ankle.render(r);
      toes.render(r);
      toeJoint.render(r);
    }
  }

  private Body body = new Body();
  private Neck neck = new Neck();
  private Head head = new Head();
  private Leg leftLeg = new Leg();
  private Leg rightLeg = new Leg();

  /// <summary> Stand leg left knee angles (L, R, X). </summary>
  private Vector2 standLKneeAngles = new Vector2(-0.05,-0.4);
  /// <summary> Stand leg left ankle angles (L, R, X). </summary>
  private Vector2 standLAnkleAngles = new Vector2(-0.1,-Math.PI * 0.55);
  /// <summary> Stand leg right knee angles (L, R, X). </summary>
  private Vector2 standRKneeAngles = new Vector2(-0.4,-0.05);
  /// <summary> Stand leg right ankle angles (L, R, X). </summary>
  private Vector2 standRAnkleAngles = new Vector2(-Math.PI * 0.55,-0.1);
  /// <summary> Leg animation knee angle initial (L, R, X). </summary>
  private Vector2 kneeAnimationInit = standRKneeAngles.clone();
  /// <summary> Leg animation ankle angle initial (L, R, X). </summary>
  private Vector2 ankleAnimationInit = standRAnkleAngles.clone();
  /// <summary> Leg animation knee angle targets (L, R, X). </summary>
  private Vector2 kneeAnimationTargets = standRKneeAngles.clone();
  /// <summary> Leg animation ankle angle targets (L, R, X). </summary>
  private Vector2 ankleAnimationTargets = standRAnkleAngles.clone();

  // Animation controls.
  private boolean animatingLegs = false;
  private LegId standState = LegId.Left;
  private double kneeAnimSpeedBase = 0.5;
  private double ankleAnimSpeedBase = 2.0;
  private Vector2 kneeAnimSpeed = Vector2.zero();
  private Vector2 kneeAnimStartTimes = Vector2.zero();
  private Vector2 kneeAnimDueTimes = Vector2.zero();
  private Vector2 ankleAnimSpeed = Vector2.zero();
  private Vector2 ankleAnimStartTimes = Vector2.zero();
  private Vector2 ankleAnimDueTimes = Vector2.zero();
  private double legSwitchDelay = 1.0;

  // Head animation controls.
  private boolean animatingHead = false;
  private double headAnimInit = 0;
  private double headAnimTarget = 0;
  private double headAnimSpeed = 0;
  private double headAnimStartTime = 0;
  private double headAnimDueTime = 0;

  private Random rng = new Random(System.currentTimeMillis());

  public Flamingo() {
    body.setParent(this);
    {
      Matrix4x4 bodyTform = body.getTransformLocal();
      bodyTform.scale(0.30, 0.30, 1.0);
      bodyTform.translate(0.0, 0.125, 0.0);
    }

    neck.setParent(body);
    {
      Matrix4x4 neckTform = neck.getTransformLocal();
      neckTform.translate(-0.5, 0.0, 0.0);
    }

    head.setParent(neck);
    {
      Matrix4x4 headTform = head.getTransformLocal();
      headTform.scale(0.25, 0.25, 1.0);
      headTform.translate(-0.030, 1.380, 0.0);
    }

    leftLeg.setParent(this);
    {
      Matrix4x4 leftLegTform = leftLeg.getTransformLocal();
      leftLegTform.scale(0.55, 0.55, 1.0);
      leftLegTform.translate(-0.01, 0.02, 0.0);
    }
    leftLeg.rotateKnee(kneeAnimationTargets.x);
    leftLeg.rotateAnkle(ankleAnimationTargets.x);

    rightLeg.setParent(this);
    {
      Matrix4x4 rightLegTform = rightLeg.getTransformLocal();
      rightLegTform.scale(0.55, 0.55, 1.0);
      rightLegTform.translate(0.02, 0.02, 0.0);
    }
    rightLeg.rotateKnee(kneeAnimationTargets.y);
    rightLeg.rotateAnkle(ankleAnimationTargets.y);

    // Set standing invariants using function interface. Two calls
    // prevents triggering a leg switch.
    standRight(0);
    standRight(0);
  }

  private double sign(double v) {
    return (v >= 0.0) ? 1.0 : -1.0;
  }
  private double gaussScale(double var) {
    return 1.0 + (rng.nextGaussian() * var);
  }

  class KneeAnimator implements JointAnimationFunc {
    @Override public void animate(Leg leg, double angleRad) {
      leg.rotateKnee(angleRad);
    }
    @Override public double getRotation(Leg leg) {
      return leg.getRotationKnee();
    }
  }
  class AnkleAnimator implements JointAnimationFunc {
    @Override public void animate(Leg leg, double angleRad) {
      leg.rotateAnkle(angleRad);
    }
    @Override public double getRotation(Leg leg) {
      return leg.getRotationAnkle();
    }
  }

  /// <summary> Apply rotation to the given joint. </summary>
  public int animateJoint(double time,
                          Vector2 startTimes, Vector2 dueTimes,
                          Vector2 initial, Vector2 targets,
                          Vector2 speeds,
                          JointAnimationFunc f) {
    int doneCount = 0;
    Vector2 elapsed = new Vector2(time - startTimes.x,
                                  time - startTimes.y);
    Vector2 offset = speeds.mul(elapsed);
    Vector2 nowRaw = initial.add(offset);
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
  public void setLegSwitchDelay(double d) {
    legSwitchDelay = d;
  }

  public boolean animatingHead() {
    return animatingHead;
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
      double elapsed = (time - headAnimStartTime);
      double angleNow = headAnimInit + (headAnimSpeed * elapsed);
      Matrix4x4 headTform = head.getTransformLocal();
      headTform.identity();
      headTform.scale(0.25, 0.25, 1.0);
      if ((headAnimStartTime <= time) && (time < headAnimDueTime)) {
        headTform.rotateZ(angleNow);
      }
      else {
        headTform.rotateZ(headAnimTarget);
        animatingHead = false;
      }
      headTform.translate(-0.030, 1.380, 0.0);
    }
  }

  /// <summary> Generic method to setup animation tracks. </summary>
  private void prepareJointAnimation(double scaleVar, JointAnimationFunc f,
                                     Vector2 initial, Vector2 targets,
                                     Vector2 targetsBase, Vector2 speeds,
                                     double speedBase, Vector2 durations) {
    Vector2 scale = new Vector2(gaussScale(scaleVar), gaussScale(scaleVar));
    initial.set(f.getRotation(leftLeg), f.getRotation(rightLeg));
    targets.set(targetsBase.mul(scale));
    Vector2 animAmt = targets.sub(initial);
    speeds.set(speedBase * sign(animAmt.x) * gaussScale(scaleVar),
               speedBase * sign(animAmt.y) * gaussScale(scaleVar));
    durations.set(animAmt.div(speeds));
  }

  /// <summary> Generic method to switch legs or to shift current leg. </summary>
  private void stand(double time, LegId legId) {
    // Initialize.
    double kneeInit = legId.getAnimValue(kneeAnimationTargets);
    double ankleInit = legId.getAnimValue(ankleAnimationTargets);
    // Knee setup.
    prepareJointAnimation(0.1, new KneeAnimator(), kneeAnimationInit,
                          kneeAnimationTargets,
                          (LegId.Left == legId) ? standLKneeAngles : standRKneeAngles,
                          kneeAnimSpeed, kneeAnimSpeedBase, kneeAnimDueTimes);
    // Ankle setup.
    prepareJointAnimation(0.1, new AnkleAnimator(), ankleAnimationInit,
                          ankleAnimationTargets,
                          (LegId.Left == legId) ? standLAnkleAngles : standRAnkleAngles,
                          ankleAnimSpeed, ankleAnimSpeedBase, ankleAnimDueTimes);

    // Set animation controls.
    legId.setAnimValue(kneeAnimStartTimes, time);
    legId.setAnimValue(ankleAnimStartTimes, time);
    // Dont move when already on this leg.
    if (legId == standState) {
      legId.addAnimValue(kneeAnimDueTimes, time - 0.1);
      legId.addAnimValue(ankleAnimDueTimes, time - 0.1);
      legId.setAnimValue(kneeAnimationTargets, kneeInit);
      legId.setAnimValue(ankleAnimationTargets, ankleInit);
    }
    else {
      // Make this leg go down.
      legId.addAnimValue(kneeAnimDueTimes, legId.getAnimValue(kneeAnimStartTimes));
      legId.addAnimValue(ankleAnimDueTimes, legId.getAnimValue(ankleAnimStartTimes));
    }
    // Make other leg go up.
    double standLegEndTime = Math.max(legId.getAnimValue(kneeAnimDueTimes),
                                     legId.getAnimValue(ankleAnimDueTimes));
    LegId other = legId.other();
    {
      double startTime = standLegEndTime + legSwitchDelay * gaussScale(0.1);
      other.setAnimValue(kneeAnimStartTimes, startTime);
      other.addAnimValue(kneeAnimDueTimes, other.getAnimValue(kneeAnimStartTimes));
    }
    {
      double startTime = standLegEndTime + legSwitchDelay * gaussScale(0.1);
      other.setAnimValue(ankleAnimStartTimes, startTime);
      other.addAnimValue(ankleAnimDueTimes, other.getAnimValue(ankleAnimStartTimes));
    }

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

  public void animateHead(double time, double speed, double target) {
    headAnimStartTime = time;
    headAnimSpeed = speed;
    headAnimInit = head.rotationRad;
    headAnimTarget = target;
    double travelDist = headAnimTarget - headAnimInit;
    double duration = (double)(travelDist / headAnimSpeed);
    if (duration < 0) {
      duration *= -1;
      headAnimSpeed *= -1.0;
    }
    headAnimDueTime = time + duration;
    animatingHead = true;
    head.rotationRad = target;
  }

  @Override
  public void render(Renderer r) {
    body.render(r);
    neck.render(r);
    head.render(r);
    leftLeg.render(r);
    rightLeg.render(r);
  }
}

