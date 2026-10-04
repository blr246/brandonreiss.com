/*
 * Robot.java
 *
 * A robot character composed of primitive shapes.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
import com.brandonreiss.geometry.*;
import com.brandonreiss.math.*;

///<summary>A robot character.</summary>
public class Robot extends SceneObject {
  private class Arm extends SceneObject {
    private Dummy jointControl = new Dummy();
    private Dummy armControl = new Dummy();
    private Geometry joint = null;
    private Geometry arm = null;
    
    public Arm(double length, double armRadius, int lengthSegs,
               double jointRadius, int jointSegs) {
      arm = Geometry.CreateCylinder(8, lengthSegs);
      joint = Geometry.CreateSphere(jointSegs, jointSegs, jointSegs);
      // Model joint.
      jointControl.setParent(this);
        joint.setParent(jointControl);
        joint.transformLocal.scale(jointRadius, jointRadius, jointRadius);
        // Model arm.
        armControl.setParent(jointControl);
        armControl.transformLocal.translate(0, 0, jointRadius);
          arm.setParent(armControl);
          arm.transformLocal.scale(armRadius, armRadius, length / 2);
          arm.transformLocal.translate(0, 0, 1);
    }

    @Override public void render(Renderer r) {}
  }

  // Torso.
  private Dummy root = new Dummy();
  private Geometry torso = Geometry.CreateCube(2, 2, 2);
  // Head.
  private Geometry head = Geometry.CreateTorus(10, 6, 0.4);
  private Geometry headProbe = Geometry.CreateSphere(3, 3, 3);
  public Dummy headBindPose = new Dummy();
  public Dummy headProbeBindPose = new Dummy();
  public Dummy headControl = new Dummy();
  public Dummy headProbeControl = new Dummy();
  // Wheel constants.
  private final double kWheelBaseHalf = 1.0;
  private final double kSmallWheelsOffset = 1;
  private final double kLargeWheelScale = 0.8;
  private final double kSmallWheelScale = 0.4;
  // Wheel geometry.
  private Geometry[][] wheels = new Geometry[][] {{Geometry.CreateTorus( 8, 5, 0.25),
                                                   Geometry.CreateTorus(10, 7, 0.25),
                                                   Geometry.CreateTorus( 8, 5, 0.25)},
                                                  {Geometry.CreateTorus( 8, 5, 0.25),
                                                   Geometry.CreateTorus(10, 7, 0.25),
                                                   Geometry.CreateTorus( 8, 5, 0.25)}};
  // Wheel bind pose dummies.
  private Dummy wheelsL = new Dummy();
  private Dummy wheelsR = new Dummy();

  // Arm constants.
  private final double kUpperArmLength = 1.5;
  private final double kUpperArmRadius = 0.075;
  private final double kUpperArmJointRadius = kUpperArmRadius * 3;
  private final double kLowerArmLength = 1.0;
  private final double kLowerArmRadius = 0.05;
  private final double kLowerArmJointRadius = kLowerArmRadius * 3;
  // Arm geometry.
  private Arm upperArmR = new Arm(kUpperArmLength, kUpperArmRadius, 4,
                                  kUpperArmJointRadius, 3);
  private Arm lowerArmR = new Arm(kLowerArmLength, kLowerArmRadius, 3,
                                  kLowerArmJointRadius, 3);
  private Arm upperArmL = new Arm(kUpperArmLength, kUpperArmRadius, 4,
                                  kUpperArmJointRadius, 3);
  private Arm lowerArmL = new Arm(kLowerArmLength, kLowerArmRadius, 3,
                                  kLowerArmJointRadius, 3);
  // Arm bind pose dummies.
  public Dummy upperArmBindPoseR = new Dummy();
  public Dummy lowerArmBindPoseR = new Dummy();
  public Dummy upperArmBindPoseL = new Dummy();
  public Dummy lowerArmBindPoseL = new Dummy();
  public Dummy upperArmControlR = new Dummy();
  public Dummy lowerArmControlR = new Dummy();
  public Dummy upperArmControlL = new Dummy();
  public Dummy lowerArmControlL = new Dummy();

  //// Wheel animation.
  //private Vector3 positionLast = Vector3.vectorZero();
  //private Vector3 positionNow = Vector3.vectorZero();
  //private Vector3 velocity = Vector3.vectorZero();
  //// Head probe animation.
  //private double lastTime = getTime();

  public Robot() {
    // Torso.
    root.setParent(this);
      torso.setParent(root);
      torso.transformLocal.scale(0.7, 0.3, 1.3);
      torso.transformLocal.translate(0, 0, 1.25);
      // Head.
      headBindPose.setParent(root);
      headBindPose.transformLocal.translate(0, 0, 3.5);
        headControl.setParent(headBindPose);
          head.setParent(headControl);
          head.transformLocal.scale(0.5, 0.5, 1);
          headProbeBindPose.setParent(headControl);
            headProbeControl.setParent(headProbeBindPose);
              headProbe.setParent(headProbeControl);
              headProbe.transformLocal.translate(0.8, 0, 0);
              headProbe.transformLocal.scale(0.1, 0.1, 0.1);
      // Wheels.
      wheelsL.setParent(root);
      wheelsR.setParent(root);
      // Place to side.
      wheelsL.transformLocal.translate(-kWheelBaseHalf, 0, 0);
      wheelsL.transformLocal.rotateY(Math.PI * 0.5);
      wheelsR.transformLocal.translate( kWheelBaseHalf, 0, 0);
      wheelsR.transformLocal.rotateY(Math.PI * 0.5);
      // Model wheel sets.
      for (int setIdx = 0; setIdx < 2; ++setIdx) {
        SceneObject parent = (0 == setIdx) ? wheelsL : wheelsR;
        Geometry[] wheelSet = wheels[setIdx];
        // Turn to ground surface.
        {
          wheelSet[0].setParent(parent);
          wheelSet[0].transformLocal.translate(-kSmallWheelScale,-kSmallWheelsOffset, 0);
          wheelSet[0].transformLocal.scale(kSmallWheelScale, kSmallWheelScale, 1);
        }
        {
          wheelSet[1].setParent(parent);
          wheelSet[1].transformLocal.translate(-kLargeWheelScale,0, 0);
          wheelSet[1].transformLocal.scale(kLargeWheelScale, kLargeWheelScale, 1);
        }
        {
          wheelSet[2].setParent(parent);
          wheelSet[2].transformLocal.translate(-kSmallWheelScale, kSmallWheelsOffset, 0);
          wheelSet[2].transformLocal.scale(kSmallWheelScale, kSmallWheelScale, 1);
        }
      }
    // Arms.
    upperArmBindPoseR.setParent(root);
    upperArmBindPoseR.transformLocal.translate(-0.75, 0, 3);
    upperArmBindPoseR.transformLocal.rotateY(-Math.PI * 0.5);
      upperArmControlR.setParent(upperArmBindPoseR);
        upperArmR.setParent(upperArmControlR);
          lowerArmBindPoseR.setParent(upperArmControlR);
          lowerArmBindPoseR.transformLocal.translate(0, 0, kUpperArmLength + kUpperArmJointRadius);
            lowerArmControlR.setParent(lowerArmBindPoseR);
              lowerArmR.setParent(lowerArmControlR);
    upperArmBindPoseL.setParent(root);
    upperArmBindPoseL.transformLocal.translate( 0.75, 0, 3);
    upperArmBindPoseL.transformLocal.rotateY( Math.PI * 0.5);
      upperArmControlL.setParent(upperArmBindPoseL);
        upperArmL.setParent(upperArmControlL);
          lowerArmBindPoseL.setParent(upperArmControlL);
          lowerArmBindPoseL.transformLocal.translate(0, 0, kUpperArmLength + kUpperArmJointRadius);
            lowerArmControlL.setParent(lowerArmBindPoseL);
              lowerArmL.setParent(lowerArmControlL);
  }

  public void animate(double dt, Vector3 displacement) {
    // Compute robot motion offset to turn wheels.
    final double distance = displacement.length();
    final double smallWheelRot = distance / kSmallWheelScale;
    final double largeWheelRot = distance / kLargeWheelScale;
    for (int setIdx = 0; setIdx < 2; ++setIdx) {
      Geometry[] wheelSet = wheels[setIdx];
      wheelSet[0].transformLocal.rotateZ(smallWheelRot);
      wheelSet[1].transformLocal.rotateZ(largeWheelRot);
      wheelSet[2].transformLocal.rotateZ(smallWheelRot);
    }
    if (dt > 0) {
      final double idleAng = 2 * dt;
      final double speed = distance / dt;
      final double speedAng = speed * 5.0e-2;
      headProbeControl.transformLocal.rotateZ(idleAng + speedAng);
    }
  }
  
  @Override public void render(Renderer r) {}
}

