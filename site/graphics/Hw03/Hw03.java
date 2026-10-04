/*
 * Hw03.java
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import com.brandonreiss.geometry.*;
import com.brandonreiss.math.*;

public class Hw03 extends BufferedApplet {
  /// <summary> A shadow projection vertex program.
  private class ShadowProjector implements Renderer.VertexProgram {
    public Vector4 plane = new Vector4();
    public Vector4 lightSource = new Vector4();

    @Override
    public void run(Vector4[] vertices) {
      // Plane normal.
      assert(false);
      Vector4 normal = new Vector4(plane.x, plane.y, plane.z, 0.0);
      for (Vector4 v : vertices) {
        // Get light direction.
        Vector4 lightDir = Vector4.vectorSub(v, lightSource);
        // Solve for projection to the plane
        double nDotV = normal.dot(v);
        double nDotLightDir = normal.dot(lightDir);
        // Get parametric step along light direction to plane.
        double t = (-nDotV - plane.w) / nDotLightDir;
        // Add distance along light ray to projection.
        v.add(Vector4.vectorMul(lightDir, t));
      }
    }
  }

  private final double startTime = System.currentTimeMillis() / 1000.0;
  private Renderer renderer = new Renderer();
  private ShadowProjector shadowProjector = new ShadowProjector();
  private Random rng = new Random(System.currentTimeMillis());
  
  private interface Scene {
    public void render(Renderer r);
  }

  private class Scene0 implements Scene { 
    private Cube cube = new Cube();
    private Cube cubeShadowCam = new Cube();
    private Axes axesCube = new Axes();
    private Plane shadowPlane = new Plane();
    private Plane floor = new Plane();
    private Sphere bigBall = new Sphere(3, 3, 3);

    private class DancingTrees extends SceneObject {
      Cube[] cubes = null;
      Sphere[] bubbles = null;
      private int count;
      private Vector2 angleRange = new Vector2(0, Math.PI * 2);
      private Vector2 axes = new Vector2(1, 1);

      DancingTrees(int count_,
                   double thetaStart, double thetaEnd,
                   double majRad, double minRad) {
        count = count_;
        angleRange.set(thetaStart, thetaEnd);
        axes.set(majRad, minRad);
        cubes = new Cube[count];
        bubbles = new Sphere[count];
        for (int cubeIdx = 0; cubeIdx < count; ++cubeIdx) {
          cubes[cubeIdx] = new Cube();
          cubes[cubeIdx].setParent(this);
          bubbles[cubeIdx] = new Sphere(3, 3, 3);
          bubbles[cubeIdx].setParent(this);
        }
      }

      public void animate(double time) {
        assert(count > 1);
        final double range = angleRange.y - angleRange.x;
        final double angleStep = (range != 0) ? range / (count - 1) :
                                                Math.PI * 2 / count;
        for (int cubeIdx = 0; cubeIdx < count; ++cubeIdx) {
          final double angle = angleRange.x + (cubeIdx * angleStep);
          final double ax = axes.x * Math.cos(angle);
          final double ay = axes.y * Math.sin(angle);
          final double zScale = 1.4 +
                                (0.40 * Math.sin(1 * (time + (cubeIdx * 0.5)))) +
                                (0.20 * Math.sin(4 * (time + (cubeIdx * 0.3)))) +
                                (0.05 * Math.cos(8 * (time + (cubeIdx * 0.1))));
          // Position cube.
          {
            Cube c = cubes[cubeIdx];
            Matrix4x4 tform = c.getTransformLocal();
            tform.identity();
            tform.scale(1, 1, zScale);
            tform.translate(ax, ay, 1);
            tform.scale(0.1, 0.1, 1);
            tform.rotateZ(angle);
          }
          // Position sphere.
          {
            Sphere s = bubbles[cubeIdx];
            Matrix4x4 tform = s.getTransformLocal();
            tform.identity();
            tform.translate(ax, ay, (zScale * 0.3) + (zScale * 2));
            tform.scale(0.05, 0.05, 0.05 + (0.05 * zScale));
          }
        }
      }

      public void render(Renderer r) {
        {
          Vector4 plane = new Vector4(0, 0, 1, 0);
          Matrix4x4 planeTform = getTransformGlobal();
          plane.transform(planeTform);
          Vector4 point = new Vector4(planeTform.m[0][3],
                                      planeTform.m[1][3],
                                      planeTform.m[2][3], 0);
          plane.w = -point.dot(plane);
          shadowProjector.plane = plane;
        }
        Color cubesColor = new Color(255, 0, 0, 96);
        for (Cube c : cubes) {
          // Shadow.
          {
            r.vertexProgram = shadowProjector;
            r.g.setColor(cubesColor);
            c.setWireframe(false);
            c.render(r);
          }
          // Faces.
          {
            r.resetVertexProgram();
            r.g.setColor(cubesColor);
            c.setWireframe(false);
            c.render(r);
          }
          // Wires.
          {
            r.resetVertexProgram();
            r.g.setColor(Color.green);
            c.setWireframe(true);
            c.render(r);
          }
        }
        for (Sphere s : bubbles) {
          // Faces.
          {
            r.resetVertexProgram();
            r.g.setColor(cubesColor);
            s.setWireframe(false);
            s.render(r);
          }
          // Wires.
          {
            r.resetVertexProgram();
            r.g.setColor(Color.green);
            s.setWireframe(true);
            s.render(r);
          }
        }
      }
    }

    private DancingTrees trees = new DancingTrees(40, 0, 0, 3.1, 5);

    public void render(Renderer r) {
      // Collect some parameters.
      Graphics g = r.g;
      final double time = getTime();
      final double aspect = r.screenSize.x / r.screenSize.y;
      final int w = (int)r.screenSize.x;
      final int h = (int)r.screenSize.y;
      // Setup ground plane.
      shadowProjector.plane.set(0, 0, 1, 0);
      // Move the light.
      {
        double ellipseAng = -time * 4;
        double ptAng = (time + 3) * 8;
        double ang = ptAng + ellipseAng;
        double majAxis = 0.8;
        double minAxis = 0.5;
        double ax = majAxis * Math.cos(ang);
        double ay = minAxis * Math.sin(ang);
        shadowProjector.lightSource.set(ax, ay, 25, 1);
      }
      // Camera setup.
      final double cameraTheta = time * 0.2;
      Matrix4x4 cameraMat = Matrix4x4.matrixIdentity();
      {
        Matrix4x4 cameraInv = Matrix4x4.matrixIdentity();
        cameraInv.translate(0, 0, -6);
        cameraInv.rotateX(-Math.PI * 0.30);
        cameraInv.rotateZ(cameraTheta);
        r.viewMatInv = cameraInv;
        // Inverse is cameraMat.
        cameraMat.rotateZ(cameraTheta);
        cameraMat.rotateX(-Math.PI * 0.30);
        cameraMat.translate(0, 0, -6);
      }
      // Projection.
      {
        final double fovH = Math.PI * 0.35;
        r.projMat = Matrix4x4.matrixProjectionFov(fovH, aspect, 4, 20);
      }

      // Clear screen.
      Color bgColor = Color.white;
      g.setColor(bgColor);
      g.fillRect(0, 0, w, h);

      // Draw floor.
      Color floorColor = new Color(196, 196, 255);
      {
        {
          Matrix4x4 floorTform = floor.getTransformLocal();
          floorTform.identity();
          floorTform.scale(6, 6, 1);
          floorTform.translate(0, 0, -2);
        }
        r.resetVertexProgram();
        floor.setWireframe(false);
        g.setColor(floorColor);
        floor.render(r);
        //floor.setWireframe(true);
        //g.setColor(new Color(255, 0, 255));
        //floor.render(r);
      }

      // Rolling ball in bg.
      {
        final int bigBallCopies = 3;
        final double bigBallSpacing = 2.0;
        for (int i = -bigBallCopies; i <= bigBallCopies; ++i)
        {
          {
            final double scale = 0.6 + (i * 0.15);
            Matrix4x4 tform = bigBall.getTransformLocal();
            tform.identity();
            tform.rotateZ(-cameraTheta + (scale * scale * bigBallSpacing) - 1.4);
            tform.translate(0, 8, -2 + scale);
            tform.rotateY((2.0 / scale) * cameraTheta);
            tform.scale(scale, scale, scale);
          }
          r.resetVertexProgram();
          // Faces.
          {
            bigBall.setWireframe(false);
            g.setColor(Color.blue);
            r.render(bigBall);
          }
          // Wires.
          {
            bigBall.setWireframe(true);
            g.setColor(Color.black);
            r.render(bigBall);
          }
        }
      }

      // Trees.
      {
        Matrix4x4 treesTform = trees.getTransformLocal();
        treesTform.identity();
        treesTform.translate(0, 0, -2);
      }
      trees.animate(time);
      trees.render(r);

      // Animate windowed cube.
      axesCube.setParent(cube);
      {
        Color cubeColor = Color.orange;
        Matrix4x4 cubeTform = cube.getTransformLocal();
        cubeTform.identity();
        cubeTform.translate(-Math.sqrt(2) * 0.5, 0, 0.5);
        cubeTform.rotateZ(Math.PI * 0.25);
        cubeTform.scale(0.5, 0.5, 0.5);
        // Global transformations.
        final double ang = (Math.PI * 0.5) - Math.atan(1 / Math.sqrt(2));
        cubeTform.leftMul(Matrix4x4.matrixRotationY(ang));
        cubeTform.leftMul(Matrix4x4.matrixRotationZ(time * 0.5));
      }
      // Animate shadow plane.
      {
        // Move it.
        {
          Matrix4x4 shadowPlaneTform = shadowPlane.getTransformLocal();
          shadowPlaneTform.identity();
          shadowPlaneTform.translate(0, 0,-1.0);
          final double rotateScale = 0.5;
          final double ax = Math.cos(time);
          final double ay = Math.sin(time);
          shadowPlaneTform.rotateX(0.2 + (ax * rotateScale));
          shadowPlaneTform.rotateY(ay * rotateScale);
        }
      }
      // Setup shadow plane in projector.
      {
        Vector4 plane = new Vector4(0, 0, 1, 0);
        Matrix4x4 planeTform = shadowPlane.getTransformGlobal();
        plane.transform(planeTform);
        Vector4 point = new Vector4(planeTform.m[0][3],
                                    planeTform.m[1][3],
                                    planeTform.m[2][3], 0);
        plane.w = -point.dot(plane);
        shadowProjector.plane = plane;
        shadowProjector.lightSource.z = -3.0;
      }
      // Erase bg behind cube.
      {
        r.resetVertexProgram();
        cube.setWireframe(false);
        g.setColor(floorColor);
        cube.render(r);
      }
      // Render shadow cam.
      {
        // Move to light.
        {
          Matrix4x4 camTform = cubeShadowCam.getTransformLocal();
          camTform.identity();
          camTform.translate(shadowProjector.lightSource.x,
                             shadowProjector.lightSource.y,
                             shadowProjector.lightSource.z);
          final double camScale = 0.05;
          camTform.scale(camScale, camScale, camScale);
        }
        // Render.
        {
          r.resetVertexProgram();
          g.setColor(Color.black);
          cubeShadowCam.render(r);
        }
      }
      // Render shadow plane.
      {
        r.resetVertexProgram();
        g.setColor(new Color(228, 228, 228, 127));
        shadowPlane.render(r);
      }
      // Render windowed cube.
      {
        Color cubeColor = new Color(255, 255, 0, 96);
        Color cubeShadowColor = new Color(0, 0, 255, 96);
        // Project as shadow.
        {
          r.vertexProgram = shadowProjector;
          // Render cube axes.
          {
            axesCube.render(r);
          }
          // Shadow.
          {
            cube.setWireframe(false);
            g.setColor(cubeShadowColor);
            cube.render(r);
          }
          // Wires.
          {
            cube.setWireframe(true);
            g.setColor(Color.black);
            cube.render(r);
          }
        }
        // Project normally.
        r.resetVertexProgram();
        {
          // Render cube axes.
          {
            axesCube.render(r);
          }
          // Solid shape.
          {
            cube.setWireframe(false);
            g.setColor(cubeColor);
            cube.render(r);
          }
          // Wires.
          {
            r.resetVertexProgram();
            cube.setWireframe(true);
            g.setColor(Color.black);
            cube.render(r);
          }
        }
      }
    }
  }

  private Scene[] scenes = new Scene[] {new Scene0(), };

  public Hw03() {}

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

