/*
 * Hw02.java
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import com.brandonreiss.geometry.*;
import com.brandonreiss.math.*;

public class Hw02 extends BufferedApplet implements MouseMotionListener {
  /// <summary> A shadow projection vertex program.
  private class ShadowProjector implements Renderer.VertexProgram {
    public Vector4 plane = new Vector4();
    public Vector4 lightSource = new Vector4();

    @Override
    public void run(Vector4[] vertices) {
      // Plane normal.
      Vector4 normal = new Vector4(plane.x, plane.y, plane.z, 0.0);
      for (Vector4 v : vertices) {
        // Get light direction.
        Vector4 lightDir = v.sub(lightSource);
        // Solve for projection to the plane
        double nDotV = normal.dot(v);
        double nDotLightDir = normal.dot(lightDir);
        // Get parametric step along light direction to plane.
        double t = (-nDotV - plane.w) / nDotLightDir;
        // Set point.
        v.set(v.add(lightDir.mul(t)));
      }
    }
  }

  /// <summary> A line of stepping flamingos. </summary>
  private class FlamingoStepLine extends SceneObject {
    private Flamingo[] flamingos = null;
    private int count;
    private double scale;
    private double rotX;
    private Vector4 initPos;
    private double spacingX;
    private double[] headAnimDueTimes = null;
    final double STAND_UP_DELAY = 0.8;
    final double LEG_SWITCH_DELAY = 0.4;

    public FlamingoStepLine(int count_, double scale_, double rotX_,
                            Vector4 initPos_, double spacingX_) {
      // Collect properties.
      count = count_;
      scale = scale_;
      rotX = rotX_;
      initPos = initPos_.clone();
      spacingX = spacingX_;
      // Create flamingos.
      flamingos = new Flamingo[count];
      headAnimDueTimes = new double[count];
      for (int fIdx = 0; fIdx < count; ++fIdx) {
        flamingos[fIdx] = new Flamingo();
        flamingo.setLegSwitchDelay(LEG_SWITCH_DELAY);
        headAnimDueTimes[fIdx] = 0;
      }
    }

    public void updateAnimation(double time) {
      // Head animation constants.
      final double kHeadAnimDelayBase = 1.0;
      final double kHeadRotBase = 0.8;
      final double kHeadRotSpeedBase = 0.3;

      // Position flamingos.
      {
        Matrix4x4 baseTform = Matrix4x4.matrixIdentity();
        baseTform.scale(scale, scale, scale);
        baseTform.rotateX(rotX);
        baseTform.translate(initPos.x, initPos.y, initPos.z);
        for (int fIdx = 0; fIdx < count; ++fIdx) {
          Matrix4x4 flamingoTform = flamingos[fIdx].getTransformLocal();
          flamingoTform.set(baseTform);
          flamingoTform.translate(fIdx * spacingX, 0.0, 0.0);
        }
      }
      // Step flamingos.
      boolean stepNow = true;
      for (Flamingo f : flamingos) {
        if (f.animatingLegs()) {
          stepNow = false;
          break;
        }
      }
      if (stepNow) {
        for (Flamingo f : flamingos) {
          if (f.standingLeft()) {
            f.standRight(time + STAND_UP_DELAY);
          }
          else {
            f.standLeft(time + STAND_UP_DELAY);
          }
        }
      }
      // Random head animations.
      for (int fIdx = 0; fIdx < count; ++fIdx) {
        double timeUntilMoveHead = headAnimDueTimes[fIdx] - time;
        if ((timeUntilMoveHead <= 0) && !flamingos[fIdx].animatingHead()) {
          // Select head rotation.
          double angleRad = kHeadRotBase * (1.0 + (rng.nextGaussian() * 0.2));
          double speed = kHeadRotSpeedBase * (1.0 + (rng.nextGaussian() * 0.1));
          flamingos[fIdx].animateHead(time, speed, angleRad);
          // Randomize next move.
          double nextMoveDelay = (kHeadAnimDelayBase * (1.0 + (rng.nextGaussian() * 0.5)));
          headAnimDueTimes[fIdx] = time + nextMoveDelay;
        }
      }
      // Update animations.
      for (Flamingo f : flamingos) {
        f.updateAnimation(time);
      }
    }

    @Override
    public void render(Renderer r) { 
      for (Flamingo f : flamingos) {
        f.render(renderer);
      }
    }
  }

  private final double startTime = System.currentTimeMillis() / 1000.0;
  private Circle light = new Circle();
  private Cube cube = new Cube();
  private Renderer renderer = new Renderer();
  private ShadowProjector shadowProjector = new ShadowProjector();
  private Flamingo flamingo = new Flamingo();
  private Random rng = new Random(System.currentTimeMillis());
  private double timeNextMoveHead = 0;
  private FlamingoStepLine flamingoLine = null;
  private Vector2 mouseCoord = new Vector2(0, 0);

  public Hw02() {
    flamingoLine = new FlamingoStepLine(8, // count
                                        2.0, // scale
                                        Math.PI * 0.3, // rotX
                                        new Vector4(-5.0, 1.0, 0.9, 1.0), // initPos
                                        1.2); // spacingX
    addMouseMotionListener(this);
  }

  @Override
  public void render(Graphics g) {
    final double time = getTime();
    final int w = getWidth();
    final int h = getHeight();
    final double MOUSE_SCALE =  5;
    // Clear screen.
    g.setColor(new Color(239.0f / 255.0f, 241.0f / 255.0f, 188.0f / 255.0f));
    g.fillRect(0, 0, w, h);
    // Setup renderer.
    renderer.g = g;
    renderer.screenSize.set(w, h);
    renderer.fovScale = 0.25;
    renderer.resetVertexProgram();
    // Project shadows onto the ground with Z-axis up.
    shadowProjector.plane.set(0, 0, 1, 0);

    // Position cube light.
    shadowProjector.lightSource.set( 0,-2, 4, 1);
    //renderLight(shadowProjector.lightSource, 0.25, renderer);
    {
      Matrix4x4 cubeTform = cube.getTransformLocal();
      cubeTform.identity();
      cubeTform.scale(0.125, 0.125, 0.125);
      cubeTform.rotateZ(time);
      cubeTform.rotateX(Math.PI * 0.25);
      cubeTform.translate(-4.5,-0.1, 0.5);
      final int kTotalCubes = 20;
      for (int numCubes = 0; numCubes < kTotalCubes; ++numCubes) {
        cubeTform.translate(0.5, 0, 0.0);
        g.setColor(Color.black);
        renderer.resetVertexProgram();
        cube.render(renderer);
        g.setColor(Color.red);
        renderer.vertexProgram = shadowProjector;
        cube.render(renderer);
      }
    }
    shadowProjector.lightSource.set( 0, 2, 4, 1);
    {
      Matrix4x4 cubeTform = cube.getTransformLocal();
      cubeTform.identity();
      cubeTform.scale(0.17, 0.17, 0.17);
      cubeTform.rotateZ(-time);
      cubeTform.rotateY(Math.PI * 0.25);
      cubeTform.rotateX(Math.PI * 0.25);
      cubeTform.translate(-4.75,-0.4, 0.5);
      final int kTotalCubes = 20;
      for (int numCubes = 0; numCubes < kTotalCubes; ++numCubes) {
        cubeTform.translate(0.5, 0, 0.0);
        g.setColor(Color.black);
        renderer.resetVertexProgram();
        cube.render(renderer);
        g.setColor(Color.red);
        renderer.vertexProgram = shadowProjector;
        cube.render(renderer);
      }
    }

    // Position flamingo light.
    shadowProjector.lightSource.set(-3 + (MOUSE_SCALE * mouseCoord.x),
                                    -4 + (MOUSE_SCALE * mouseCoord.y), 5, 1);
    renderer.resetVertexProgram();
    //renderLight(shadowProjector.lightSource, 0.25, renderer);
    // Update flamingos.
    flamingoLine.updateAnimation(time);
    // Render shadow then mesh.
    g.setColor(Color.red);
    renderer.vertexProgram = shadowProjector;
    flamingoLine.render(renderer);
    g.setColor(Color.pink);
    renderer.resetVertexProgram();
    flamingoLine.render(renderer);
  }

  /// <summary> Get time in seconds since this instance was created. </summary>
  public double getTime() {
    return (System.currentTimeMillis() / 1000.0) - startTime;
  }

  private void renderLight(Vector4 lightSource, double scale, Renderer r) {
    Graphics g = r.g;
    g.setColor(Color.yellow);
    {
      Matrix4x4 lightTform = light.getTransformLocal();
      lightTform.identity();
      lightTform.scale(scale, scale, scale);
      lightTform.translate(lightSource.x, lightSource.y, lightSource.z);
      light.render(r);
    }
    {
      Matrix4x4 lightTform = light.getTransformLocal();
      lightTform.identity();
      lightTform.scale(scale, scale, scale);
      lightTform.rotateX(Math.PI * 0.5);
      lightTform.translate(lightSource.x, lightSource.y, lightSource.z);
      light.render(r);
    }
    {
      Matrix4x4 lightTform = light.getTransformLocal();
      lightTform.identity();
      lightTform.scale(scale, scale, scale);
      lightTform.rotateY(Math.PI * 0.5);
      lightTform.translate(lightSource.x, lightSource.y, lightSource.z);
      light.render(r);
    }
  }

  /// <summary> Mouse listener to move the light. </summary>
  @Override
  public void mouseMoved(MouseEvent e) {
    final double w = getWidth();
    mouseCoord.x = -2.0 * ((e.getX() / w) - 0.5);
    mouseCoord.y =  2.0 * ((e.getY() / w) - 0.2);
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    mouseMoved(e);
  }
}

