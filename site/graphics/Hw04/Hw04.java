/*
 * Hw04.java
 *
 * A 3d scene containing spinning shapes hovering over a tiled floor. A
 * robot drives in circles between the shapes flailing its arms.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
import com.brandonreiss.geometry.*;
import com.brandonreiss.math.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Hw04 extends BufferedApplet {
  /// <summary> A shadow projection vertex program.
  private class ShadowProjector implements Renderer.VertexProgram {
    public Vector4 plane = new Vector4();
    public Vector3 lightSource = new Vector3();

    private Vector4 planeTform = new Vector4();
    private Vector3 originTform = new Vector3();
    private Vector3 originOffsetTform = new Vector3();
    private Vector3 lightTform = new Vector3();
    private Vector3 lightDirTmp = new Vector3();

    public void updateViewMatrix(Matrix4x4 matView) {
      // Transform plane and light.
      planeTform.set(plane).transformNormal(matView);
      originTform.zero().transformCoord(matView);
      originTform.add(originOffsetTform.set(planeTform).mul(-plane.w));
      planeTform.w = -originTform.dot(planeTform);
      lightTform.set(lightSource).transformCoord(matView);
    }

    @Override
    public void run(Matrix4x4 modelViewMat,
                    Vector3[] in, Vector3[] out) {
      assert(out.length >= in.length);
      // Project vertices to plane.
      for (int vIdx = 0; vIdx < in.length; ++vIdx) {
        // Transform input coordinate.
        out[vIdx].set(in[vIdx]).transformCoord(modelViewMat);
        // Get light direction.
        lightDirTmp.set(out[vIdx]).sub(lightTform);
        // Project to plane.
        Util.PointProjectPlaneAlongDir(out[vIdx], planeTform, lightDirTmp);
      }
    }
  }

  private final double startTime = System.currentTimeMillis() / 1000.0;
  private Renderer renderer = new Renderer();
  private ShadowProjector shadowProjector = new ShadowProjector();
  private Random rng = new Random(System.currentTimeMillis());

  /// <summary> A renderable scene. </summary>
  private interface Scene {
    public void render(Renderer r);
  }

  /// <summary> The shape buffet. </summary>
  private class Robot extends SceneObject {
    private class Dummy extends SceneObject {
      @Override public void render(Renderer r) { }
    }

    private class Arm extends SceneObject {
      private Sphere joint = null;
      private Cube arm = null;
      
      public Arm(double length, double armRadius, int lengthSegs,
                 double jointRadius, int jointSegs) {
        arm = new Cube(1, 1, lengthSegs);
        joint = new Sphere(jointSegs, jointSegs, jointSegs);
        // Model joint.
        joint.setParent(this);
        {
          joint.modelTransformLocal.scale(jointRadius, jointRadius, jointRadius);
        }
        // Model arm.
        arm.setParent(joint);
        {
          arm.modelTransformLocal.scale(armRadius, armRadius, length / 2);
          arm.modelTransformLocal.translate(0, 0, 1);
        }
      }

      @Override public void render(Renderer r) { }
    }

    private Cube torso = new Cube(2, 2, 2);

    // Head.
    private Torus head = new Torus(10, 6, 0.4);
    private Sphere headProbe = new Sphere(3, 3, 3);
    private Dummy headBindPose = new Dummy();

    // Wheel constants.
    private final double kWheelBaseHalf = 1.0;
    private final double kSmallWheelsOffset = 1;
    private final double kLargeWheelScale = 0.8;
    private final double kSmallWheelScale = 0.4;
    // Wheel geometry.
    private Torus[][] wheels = new Torus[][] {{new Torus( 8, 5, 0.25),
                                               new Torus(10, 7, 0.25),
                                               new Torus( 8, 5, 0.25)},
                                              {new Torus( 8, 5, 0.25),
                                               new Torus(10, 7, 0.25),
                                               new Torus( 8, 5, 0.25)}};
    // Wheel bind pose dummies.
    private Dummy wheelsL = new Dummy();
    private Dummy wheelsR = new Dummy();

    // Arm constants.
    private final double kUpperArmLength = 1.5;
    private final double kUpperArmRadius = 0.075;
    private final double kLowerArmLength = 1.0;
    private final double kLowerArmRadius = 0.05;
    // Arm geometry.
    private Arm upperArmR = new Arm(kUpperArmLength, kUpperArmRadius, 4,
                                    kUpperArmRadius * 3, 3);
    private Arm lowerArmR = new Arm(kLowerArmLength, kLowerArmRadius, 3,
                                    kLowerArmRadius * 3, 3);
    private Arm upperArmL = new Arm(kUpperArmLength, kUpperArmRadius, 4,
                                    kUpperArmRadius * 3, 3);
    private Arm lowerArmL = new Arm(kLowerArmLength, kLowerArmRadius, 3,
                                    kLowerArmRadius * 3, 3);
    // Arm bind pose dummies.
    private Dummy upperArmPlacementR = new Dummy();
    private Dummy lowerArmPlacementR = new Dummy();
    private Dummy upperArmPlacementL = new Dummy();
    private Dummy lowerArmPlacementL = new Dummy();

    // Wheel animation.
    private Vector3 positionLast = Vector3.vectorZero();
    private Vector3 positionNow = Vector3.vectorZero();
    private Vector3 velocity = Vector3.vectorZero();

    // Head probe animation.
    private double lastTime = getTime();

    public Robot() {
      // Torso.
      torso.setParent(this);
      {
        torso.modelTransformLocal.scale(0.7, 0.3, 1.3);
        torso.modelTransformLocal.translate(0, 0, 1.25);
      }
      // Head.
      headBindPose.setParent(torso);
        head.setParent(headBindPose);
          headProbe.setParent(head);
      {
        headBindPose.animTransformLocal.translate(0, 0, 3.5);
        head.modelTransformLocal.scale(0.5, 0.5, 1);
          headProbe.modelTransformLocal.translate(0.8, 0, 0);
          headProbe.modelTransformLocal.scale(0.1, 0.1, 0.1);
      }
      // Wheels.
      wheelsL.setParent(this);
      wheelsR.setParent(this);
      {
        // Place to side.
        wheelsL.animTransformLocal.translate(-kWheelBaseHalf, 0, 0);
        wheelsL.animTransformLocal.rotateY(Math.PI * 0.5);
        wheelsR.animTransformLocal.translate( kWheelBaseHalf, 0, 0);
        wheelsR.animTransformLocal.rotateY(Math.PI * 0.5);
        // Model wheel sets.
        for (int setIdx = 0; setIdx < 2; ++setIdx) {
          SceneObject parent = (0 == setIdx) ? wheelsL : wheelsR;
          Torus[] wheelSet = wheels[setIdx];
          // Turn to ground surface.
          for (Torus w : wheelSet) {
            w.setParent(parent);
          }
          {
            wheelSet[0].animTransformLocal.translate(-kSmallWheelScale,-kSmallWheelsOffset, 0);
            wheelSet[0].modelTransformLocal.scale(kSmallWheelScale, kSmallWheelScale, 1);
          }
          {
            wheelSet[1].animTransformLocal.translate(-kLargeWheelScale,0, 0);
            wheelSet[1].modelTransformLocal.scale(kLargeWheelScale, kLargeWheelScale, 1);
          }
          {
            wheelSet[2].animTransformLocal.translate(-kSmallWheelScale, kSmallWheelsOffset, 0);
            wheelSet[2].modelTransformLocal.scale(kSmallWheelScale, kSmallWheelScale, 1);
          }
        }
      }
      // Arms.
      upperArmPlacementR.setParent(torso);
        upperArmR.setParent(upperArmPlacementR);
          lowerArmPlacementR.setParent(upperArmR);
            lowerArmR.setParent(lowerArmPlacementR);
      upperArmPlacementL.setParent(torso);
        upperArmL.setParent(upperArmPlacementL);
          lowerArmPlacementL.setParent(upperArmL);
            lowerArmL.setParent(lowerArmPlacementL);
      {
        upperArmPlacementR.animTransformLocal.translate(-0.75, 0, 3);
        upperArmPlacementR.animTransformLocal.rotateY(-Math.PI * 0.5);
          lowerArmPlacementR.animTransformLocal.translate(0, 0, kUpperArmLength);

        upperArmPlacementL.animTransformLocal.translate( 0.75, 0, 3);
        upperArmPlacementL.animTransformLocal.rotateY( Math.PI * 0.5);
          lowerArmPlacementL.animTransformLocal.translate(0, 0, kUpperArmLength);
      }
    }
    
    @Override
    public void render(Renderer r) {
      // Compute robot motion offset to turn wheels.
      positionNow.zero().transformCoord(animTransformLocal);
      velocity.set(positionNow).sub(positionLast);
      final double speed = velocity.length();
      final double smallWheelRot = speed / kSmallWheelScale;
      final double largeWheelRot = speed / kLargeWheelScale;
      for (int setIdx = 0; setIdx < 2; ++setIdx) {
        Torus[] wheelSet = wheels[setIdx];
        wheelSet[0].animTransformLocal.rotateZ(smallWheelRot);
        wheelSet[1].animTransformLocal.rotateZ(largeWheelRot);
        wheelSet[2].animTransformLocal.rotateZ(smallWheelRot);
      }
      final double time = getTime();
      final double elapsed = time - lastTime;
      final double idleAng = 0.05 * elapsed;
      headProbe.animTransformLocal.rotateZ(idleAng + speed);

      // Save last.
      positionLast.set(positionNow);
      lastTime = time;
    }
  }

  /// <summary> The shape buffet. </summary>
  private class Scene0 implements Scene {
    private SceneGraph sceneGraph = new SceneGraph();
    private Plane ground = new Plane(9, 3);
    private Plane ground2 = new Plane(9, 3);

    // Shape examples.
    private Cube c0 = new Cube();
    private Cube c1 = new Cube(5, 5, 5);
    private Sphere s0 = new Sphere(10, 16);
    private Sphere s1 = new Sphere(5, 5, 5);
    private Torus t0 = new Torus(16, 8, 0.4);
    private Torus t1 = new Torus(16, 8, 0.2);

    // Robots.
    private Robot bot0 = new Robot();

    // View matrices.
    private Matrix4x4 cameraMat = Matrix4x4.matrixIdentity();
    private Matrix4x4 cameraInv = Matrix4x4.matrixIdentity();

    public Scene0() {
      // The ground is the root element.
      sceneGraph.addChild(ground);
      sceneGraph.addChild(ground2);

      // Everything stays on the ground.
      ground.addChild(c0);
      ground.addChild(c1);
      ground.addChild(s0);
      ground.addChild(s1);
      ground.addChild(t0);
      ground.addChild(t1);
      ground.addChild(bot0);

      // Model ground plane.
      ground.modelTransformLocal.scale(15, 5, 1);
      ground.modelTransformLocal.scale(1.1, 1.1, 1);
      ground2.modelTransformLocal.rotateZ(Math.PI * 0.5);
      ground2.modelTransformLocal.scale(15, 5, 1);
      ground2.modelTransformLocal.scale(1.1, 1.1, 1);
    }

    public void render(Renderer r) {
      // Collect some parameters.
      Graphics g = r.g;
      final double time = getTime();
      final double aspect = r.screenSize.x / r.screenSize.y;
      final int w = (int)r.screenSize.x;
      final int h = (int)r.screenSize.y;
      // Camera setup.
      final double cameraTheta = time * 0.2;
      //final double cameraTheta = Math.PI * 0.25;
      {
        final double ax = Math.cos(cameraTheta);
        final double ay = Math.sin(cameraTheta);
        {
          cameraInv.identity();
          cameraInv.translate(0, 0, -18);
          cameraInv.rotateX((-Math.PI * 0.30) + (0.5 * ax));
          cameraInv.translate(0, 5 * ay * ay, 0);
          cameraInv.rotateZ(cameraTheta);
        }
        // Inverse is cameraMat.
        {
          cameraMat.identity();
          cameraMat.rotateZ(cameraTheta);
          cameraMat.translate(0, 8 * ay * ay, 0);
          cameraMat.rotateX((-Math.PI * 0.30) + (0.5 * ax));
          cameraMat.translate(0, 0, -16 + (-2 * ay * ay));
        }
        r.viewMatInv = cameraInv;
      }
      // Projection.
      {
        final double fovH = Math.PI * 0.25;
        //final double fovH = Math.PI * 0.35;
        r.projMat.projectionFov(fovH, aspect, 4, 20);
      }
      // Setup shadow.
      {
        shadowProjector.plane.set(0, 0, 1, 0);
        shadowProjector.lightSource.set(0,-4, 10);
        shadowProjector.updateViewMatrix(r.viewMatInv);
      }

      // Clear screen.
      Color bgColor = Color.white;
      g.setColor(bgColor);
      g.fillRect(0, 0, w, h);

      // Perform scene animation.
      {
        // Robot.
        {
          // Drive robot.
          {
            final double robotTheta = time * 0.5;
            final double ax = 6 * Math.cos(robotTheta);
            final double ay = 6 * Math.sin(robotTheta);
            bot0.animTransformLocal.identity();
            bot0.animTransformLocal.translate(ax, ay, 0);
            bot0.animTransformLocal.rotateZ(robotTheta + Math.PI);
          }
          // Bobble robot head.
          {
            final double ax = Math.cos(time * 2);
            final double ay = Math.sin(time * 2);
            bot0.head.animTransformLocal.identity();
            bot0.head.animTransformLocal.rotateX(0.2 * ax);
            bot0.head.animTransformLocal.rotateY(0.2 * ay);
          }
          // Move arms.
          {
            final double ax = Math.cos(time);
            final double ax2 = Math.cos(4 * time);
            final double ay = Math.cos(time);
            final double ay2 = Math.cos(2 * time);
            bot0.upperArmL.animTransformLocal.identity();
            bot0.upperArmL.animTransformLocal.rotateX( 0.5 * ax * ax);
            bot0.upperArmL.animTransformLocal.rotateY(ax);
              bot0.lowerArmL.animTransformLocal.identity();
              bot0.lowerArmL.animTransformLocal.rotateY(ay);
              bot0.lowerArmL.animTransformLocal.rotateX(ay2);
            bot0.upperArmR.animTransformLocal.identity();
            bot0.upperArmR.animTransformLocal.rotateX( 0.5 * ax * ax);
            bot0.upperArmR.animTransformLocal.rotateY(ay);
              bot0.lowerArmR.animTransformLocal.identity();
              bot0.lowerArmR.animTransformLocal.rotateY(ay);
              bot0.lowerArmR.animTransformLocal.rotateX(ay2);
          }
        }
        // DEBUG cube.
        {
          Matrix4x4 tform = c0.animTransformLocal;
          tform.identity();
          tform.translate(-2, 0, 2);
          tform.rotateZ(time);
        }
        // DEBUG cube.
        {
          Matrix4x4 tform = c1.animTransformLocal;
          tform.identity();
          tform.translate( 2, 0, 2);
          tform.rotateZ(time);
        }
        // DEBUG sphere.
        {
          Matrix4x4 tform = s0.animTransformLocal;
          tform.identity();
          tform.translate(-10, 0, 2);
          tform.rotateZ(time);
        }
        // DEBUG sphere.
        {
          Matrix4x4 tform = s1.animTransformLocal;
          tform.identity();
          tform.translate( 10, 0, 2);
          tform.rotateZ(time);
        }
        // DEBUG torus.
        {
          Matrix4x4 tform = t0.animTransformLocal;
          tform.identity();
          tform.translate(-14, 0, 2);
          tform.rotateZ(time);
        }
        // DEBUG torus.
        {
          Matrix4x4 tform = t1.animTransformLocal;
          tform.identity();
          tform.translate( 14, 0, 2);
          tform.rotateZ(time);
        }
      }
      // Render the scene. Two passes for shadows is less efficient but good
      // enough for now.
      sceneGraph.showAxes = false;
      // reissb -- 20120301 -- Color override for shadow hack.
      r.overrideColor = Color.gray;
      r.vertexProgram = shadowProjector;
      sceneGraph.render(r);
      r.overrideColor = null;
      r.resetVertexProgram();
      sceneGraph.render(r);
    }
  }

  private Scene[] scenes = new Scene[] {new Scene0()};

  @Override
  public void render(Graphics g) {
    final int w = getWidth();
    final int h = getHeight();
    // Setup renderer.
    renderer.g = g;
    renderer.screenSize.set(w, h);
    renderer.resetVertexProgram();
    // Render the current scene.
    scenes[0].render(renderer);
  }

  /// <summary> Get time in seconds since this instance was created. </summary>
  public double getTime() {
    return (System.currentTimeMillis() / 1000.0) - startTime;
  }
}

