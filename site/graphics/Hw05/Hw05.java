/*
 * Hw05.java
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
import com.brandonreiss.math.*;
import com.brandonreiss.geometry.*;
import com.brandonreiss.interactive.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Arrays;

public class Hw05 extends BufferedApplet implements MouseMotionListener, MouseListener {

  private Renderer renderer = new Renderer();
  private ShadowProjector shadowProjector = new ShadowProjector();
  private Random rng = new Random(System.currentTimeMillis());
  private MouseMaster mouseMaster = new MouseMaster();
  ///<summary>Set true when nothing is selected by the mouse.</summary>
  private boolean nothingPicked = true;
  ///<summary>Time of last transition from nothing picked to picked.</summary>
  private double lastPickedTime = 0;
  ///<summary>Total time that something has been selected.</summary>
  private double totalTimeSelected = 0;

  ///<summary>The transform gizmo used to move objects.</summary>
  private TransformGizmo gizmo = new TransformGizmo(renderer);
  ///<summary>Last object that was picked.</summary>
  private Geometry prevPicked = null;
  ///<summary>Last object that was moused over.</summary>
  private Geometry prevMousedOver = null;

  ///<summary>Color change and transform pick action.</summary>
  private class PickAction implements PickableSceneObject.Callback {
    private Geometry me = null;
    public Color colorActive = Color.red;
    public Color colorInactive = Color.gray;

    public PickAction(Geometry o) {
      me = o;
    }

    ///<summary>When picked, set color and attach transform gizmo.</summary>
    public void onPicked() {
      gizmo.setActive(false);
      gizmo.hide();
      gizmo.breakParent();
      if (null != prevPicked) {
        prevPicked.color = colorInactive;
      }
      if (null != me) {
        me.color = colorActive;
        gizmo.setActive(true);
        gizmo.unhide();
        gizmo.setParent(me);
      }
      prevPicked = me;
    }

    ///<summary>When moused over, set solid.</summary>
    public void onMouseOver() {
      if (null != prevMousedOver) {
        prevMousedOver.wireframe = true;
      }
      if (null != me) {
        me.wireframe = false;
      }
      prevMousedOver = me;
    }
  }
  ///<summary>Reset colors when nobody is picked.</summary>
  private PickAction noPickAction = new PickAction(null);
  ///<summary>List of pickable objects.</summary>
  Pickable[] pickables = null;

  public Hw05() {
    mouseMaster.addMouseListener(this);
    mouseMaster.addMouseMotionListener(this);
    mouseMaster.addMouseListener(gizmo);
    mouseMaster.addMouseMotionListener(gizmo);
    addMouseListener(mouseMaster);
    addMouseMotionListener(mouseMaster);
  }

  ///<summary>A shadow projection vertex program.</summary>
  ///<remarks>
  ///<para>Takes a light direction and the view matrix in order to project
  ///light rays from the light through each mesh vertex and onto a ground
  ///surface.</para>
  ///</remarks>
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
      originTform.add(originOffsetTform.set(planeTform).mul(plane.w));
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

  ///<summary>Hermite interpolation in XYZ channels.</summary>
  public class HermiteSplineXYZ extends SceneObject {
    public int renderPathSteps = 80;
    public double[] tValues = null;
    public double[][] sampleValues = null;
    private Interpolation[] splines = null;
    private int samples = 0;
    private Vector3 projTmp = new Vector3();

    public HermiteSplineXYZ(int samples_) {
      samples = samples_;
      tValues = new double[samples];
      sampleValues = new double[3][];
      for (int i = 0; i < sampleValues.length; ++i) {
        sampleValues[i] = new double[samples * 2];
      }
      splines = new Interpolation[] {new HermiteInterpolation(tValues, sampleValues[0], true),
                                     new HermiteInterpolation(tValues, sampleValues[1], true),
                                     new HermiteInterpolation(tValues, sampleValues[2], true),
      };
    }

    ///<summary>Get interpolated value at t.</summary>
    public void value(double t, Vector3 interpolated) {
      interpolated.set(splines[0].value(t), splines[1].value(t), splines[2].value(t));
    }

    ///<summary>Get interpolated derivative value at t.</summary>
    public void ddt(double t, Vector3 interpolated) {
      interpolated.set(splines[0].ddt(t), splines[1].ddt(t), splines[2].ddt(t));
    }

    ///<summary>Get min t.</summary>
    public double minT() {
      return tValues[0];
    }

    ///<summary>Get max t.</summary>
    public double maxT() {
      return tValues[samples - 1];
    }

    ///<summary>Display the spline using discrete steps along t.</summary>
    @Override
    public void render(Renderer r) {
      final double tRange = maxT() - minT();
      final double tStep = tRange / renderPathSteps;
      double tStart = 0;
      double tEnd = tStep;
      final int halfScreenW = ((int)r.screenSize.x) >> 1;
      final int halfScreenH = ((int)r.screenSize.y) >> 1;
      int x1, y1, x2, y2;
      // Compute initial point.
      {
        projTmp.set(splines[0].value(0), splines[1].value(0), splines[2].value(0));
        projTmp.transformCoord(r.viewMat);
        projTmp.transformCoord(r.projMat);
        x1 = (int)(( projTmp.x * halfScreenW) + halfScreenW);
        y1 = (int)((-projTmp.y * halfScreenH) + halfScreenH);
      }
      for (int i = 0; i < renderPathSteps; ++i, tStart += tStep, tEnd += tStep) {
        // Compute end point.
        {
          projTmp.set(splines[0].value(tEnd), splines[1].value(tEnd), splines[2].value(tEnd));
          projTmp.transformCoord(r.viewMat);
          projTmp.transformCoord(r.projMat);
          x2 = (int)(( projTmp.x * halfScreenW) + halfScreenW);
          y2 = (int)((-projTmp.y * halfScreenH) + halfScreenH);
        }
        r.g.drawLine(x1, y1, x2, y2);
        x1 = x2;
        y1 = y2;
      }
    }
  }

  ///<summary>A renderable scene.</summary>
  private interface Scene {
    public void render(Renderer r);
  }

  ///<summary>Robot driving around an editable path.</summary>
  private class Scene0 implements Scene {
    private SceneGraph world = new SceneGraph();
    private Dummy ground = new Dummy();
    private Geometry groundPlane0 = Geometry.CreatePlane(9, 3);
    private Geometry groundPlane1 = Geometry.CreatePlane(9, 3);

    ///<summary>The camera angle in the XY plane.<summary>
    private double cameraTheta = 0;

    ///<summary>A path to move the robot.</summary>
    private HermiteSplineXYZ spline = new HermiteSplineXYZ(9);
    ///<summary>Control handles used to edit the path.</summary>
    private Geometry[] knotCubes = null;
    ///<summary>The robot character.</summary>
    private Robot robot = new Robot();

    // Robot animation variables.
    private Vector3 robotPosTmp = new Vector3();
    private Vector3 robotAngTmp = new Vector3();
    private Vector3 robotDisplaceTmp = new Vector3();
    private Vector3 robotPosLast = new Vector3();
    private final double ROBOT_PATH_TRAVERSAL_TIME_S = 20;
    private double frameTimeLast = 0;

    public Scene0() {
      // Setep spline as a circle.
      {
        final int samples = spline.tValues.length;
        final double thetaStep = (Math.PI * 2) / (samples - 1);
        final double tStep = ROBOT_PATH_TRAVERSAL_TIME_S / (samples - 1);
        final double radius = 8;
        double theta = 0;
        double t = 0;
        for (int i = 0; i < samples; ++i, theta += thetaStep, t += tStep) {
          spline.tValues[i] = t;
          final int dblI = i * 2;
          final double ax = Math.cos(theta);
          final double ay = Math.sin(theta);
          spline.sampleValues[0][dblI    ] = radius * ax;
          spline.sampleValues[0][dblI + 1] = radius *-ay;
          spline.sampleValues[1][dblI    ] = radius * ay;
          spline.sampleValues[1][dblI + 1] = radius * ax;
          spline.sampleValues[2][dblI    ] = 0;
          spline.sampleValues[2][dblI + 1] = 0;
        }
        // Place knot handles initially and setup as pickable.
        knotCubes = new Geometry[samples - 1];
        pickables = new Pickable[samples];
        for (int i = 0; i < (samples - 1); ++i) {
          knotCubes[i] = Geometry.CreateCube();
          knotCubes[i].transformLocal.translate(spline.sampleValues[0][i * 2],
                                                spline.sampleValues[1][i * 2],
                                                spline.sampleValues[2][i * 2]);
          knotCubes[i].transformLocal.scale(0.25, 0.25, 0.25);
          knotCubes[i].color = Color.gray;
          world.addChild(knotCubes[i]);
          {
            PickAction action = new PickAction(knotCubes[i]);
            action.colorInactive = knotCubes[i].color;
            pickables[i] = new PickableSceneObject(knotCubes[i], action);
          }
        }
        // Gizmo must be pickable, too.
        pickables[samples - 1] = gizmo;
      }

      // Setup gound.
      world.addChild(ground);
      ground.addChild(groundPlane0);
      ground.addChild(groundPlane1);

      // Robot drives on the ground.
      robot.setParent(ground);

      // Model ground planes.
      groundPlane0.transformLocal.scale(15, 5, 1);
      groundPlane0.transformLocal.scale(1.1, 1.1, 1);
      groundPlane1.transformLocal.rotateZ(Math.PI * 0.5);
      groundPlane1.transformLocal.scale(15, 5, 1);
      groundPlane1.transformLocal.scale(1.1, 1.1, 1);

      // Initial scene update.
      world.updateTransform();
      animateRobot(0);
      robotPosLast.set(robot.transform.m[0][3], robot.transform.m[1][3], robot.transform.m[2][3]);
    }

    ///<summary>Perform robot path and character animation.</summary>
    private void animateRobot(double time) {
      // Update spline using the knot handle positions.
      {
        final int numKnots = knotCubes.length;
        // Update positions first.
        for (int i = 0; i <= numKnots; ++i) {
          spline.sampleValues[0][2 * i] = knotCubes[i % numKnots].transform.m[0][3];
          spline.sampleValues[1][2 * i] = knotCubes[i % numKnots].transform.m[1][3];
          spline.sampleValues[2][2 * i] = knotCubes[i % numKnots].transform.m[2][3];
        }
        // Compute slopes.
        final double tMax = spline.maxT();
        for (int p = numKnots - 1, i = 0, n = 1;
             i <= numKnots;
             p = (p + 1) % numKnots, ++i, n = (n + 1) % numKnots) {
          // X.
          {
            final double dsn0 = spline.sampleValues[0][n * 2] - spline.sampleValues[0][i * 2];
            final double dsp0 = spline.sampleValues[0][i * 2] - spline.sampleValues[0][p * 2];
            spline.sampleValues[0][(2 * i) + 1] = (dsn0 + dsp0) * 0.5;
          }
          // Y.
          {
            final double dsn1 = spline.sampleValues[1][n * 2] - spline.sampleValues[1][i * 2];
            final double dsp1 = spline.sampleValues[1][i * 2] - spline.sampleValues[1][p * 2];
            spline.sampleValues[1][(2 * i) + 1] = (dsn1 + dsp1) * 0.5;
          }
          // Z.
          {
            final double dsn2 = spline.sampleValues[2][n * 2] - spline.sampleValues[2][i * 2];
            final double dsp2 = spline.sampleValues[2][i * 2] - spline.sampleValues[2][p * 2];
            spline.sampleValues[2][(2 * i) + 1] = (dsn2 + dsp2) * 0.5;
          }
        }
      }
      // Drive the robot around the spline.
      {
        final double botTime = time % spline.maxT();
        // Position robot.
        {
          robot.transformLocal.identity();
          spline.value(botTime, robotPosTmp);
          robot.transformLocal.translate(robotPosTmp.x, robotPosTmp.y, robotPosTmp.z);
        }
        // Orient robot.
        {
          spline.ddt(botTime, robotAngTmp);
          final double zTheta = Math.atan2(robotAngTmp.y, robotAngTmp.x);
          robot.transformLocal.rotateZ(zTheta + (0.5 * Math.PI));
          final double z = robotAngTmp.z;
          robotAngTmp.z = 0;
          final double rad = robotAngTmp.length();
          final double phi = -Math.atan2(z, rad);
          robot.transformLocal.rotateX(phi);
        }
        // Compute positional offset and animate.
        {
          final double dt = time - frameTimeLast;
          robotDisplaceTmp.set(robotPosTmp).sub(robotPosLast);
          robot.animate(dt, robotDisplaceTmp);
        }
        robotPosLast.set(robotPosTmp);
      }
      // Bobble robot head.
      {
        final double ax = Math.cos(time * 2);
        final double ay = Math.sin(time * 2);
        robot.headControl.transformLocal.identity();
        robot.headControl.transformLocal.rotateX(0.2 * ax);
        robot.headControl.transformLocal.rotateY(0.2 * ay);
      }
      // Move arms.
      {
        final double ax = Math.cos(time);
        final double ax2 = Math.cos(4 * time);
        final double ay = Math.cos(time);
        final double ay2 = Math.cos(2 * time);
        robot.upperArmControlL.transformLocal.identity();
        robot.upperArmControlL.transformLocal.rotateX( 0.5 * ax * ax);
        robot.upperArmControlL.transformLocal.rotateY(ax);
          robot.lowerArmControlL.transformLocal.identity();
          robot.lowerArmControlL.transformLocal.rotateY(ay);
          robot.lowerArmControlL.transformLocal.rotateX(ay2);
        robot.upperArmControlR.transformLocal.identity();
        robot.upperArmControlR.transformLocal.rotateX( 0.5 * ax * ax);
        robot.upperArmControlR.transformLocal.rotateY(ay);
          robot.lowerArmControlR.transformLocal.identity();
          robot.lowerArmControlR.transformLocal.rotateY(ay);
          robot.lowerArmControlR.transformLocal.rotateX(ay2);
      }
    }

    public void render(Renderer r) {
      // Collect some parameters.
      Graphics g = r.g;
      final double time = getTime();
      final double aspect = r.screenSize.x / r.screenSize.y;
      final int w = (int)r.screenSize.x;
      final int h = (int)r.screenSize.y;
      // Setup renderer.
      //r.flags.add(Renderer.Flags.CULL_BACKFACE);
      // Camera setup.
      if (nothingPicked) {
        cameraTheta = (time - totalTimeSelected) * 0.2;
      }
      //cameraTheta = Math.PI * 0.25;
      {
        final double ax = Math.cos(cameraTheta);
        final double ay = Math.sin(cameraTheta);
        // Setup view matrix inverse (camera => world).
        {
          r.viewMatInv.identity();
          r.viewMatInv.rotateZ(-cameraTheta);
          r.viewMatInv.translate(0, -5 * ay * ay, 0);
          r.viewMatInv.rotateX((Math.PI * 0.30) - (0.5 * ax));
          r.viewMatInv.translate(0, 0, 18);
        }
        // Set view matrix (world => camera).
        r.viewMat.set(r.viewMatInv).inverse();
      }
      // Projection.
      {
        final double fovH = Math.PI * 0.40;
        r.projMat.projectionFovH(fovH, aspect, 4, 50);
      }
      // Setup shadow.
      {
        shadowProjector.plane.set(0, 0, 1, 0);
        shadowProjector.lightSource.set(0,-4, 10);
        shadowProjector.updateViewMatrix(r.viewMat);
      }

      // Clear screen.
      Color bgColor = Color.white;
      g.setColor(bgColor);
      g.fillRect(0, 0, w, h);

      // Update transforms prior to robot animation.
      world.updateTransform();
      animateRobot(time);
      // Render shadows.
      {
        r.vertexProgram = shadowProjector;
        r.overrideColor = Color.gray;
        for (Geometry k : knotCubes) {
          k.visible = false;
        }
        gizmo.hide();
        world.render(r);
        r.overrideColor = null;
        for (Geometry k : knotCubes) {
          k.visible = true;
        }
        gizmo.unhide();
      }
      // Render the spline.
      r.g.setColor(Color.gray);
      spline.render(r);
      // Render the scene.
      r.resetVertexProgram();
      world.render(r);

      frameTimeLast = time;
    }
  }

  private Scene[] scenes = new Scene[] {new Scene0()};

  private int fpsCount = 0;
  private double fpsTime = getTime();
  private double fps = 0;
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

    //// Count fps.
    //{
    //  ++fpsCount;
    //  final double timeNow = getTime();
    //  final double tElapsed = (timeNow - fpsTime);
    //  if (tElapsed > 1.0) {
    //    fps = fpsCount / tElapsed;
    //    fpsCount = 0;
    //    fpsTime = timeNow;
    //  }
    //  g.setColor(Color.black);
    //  g.drawString("" + fps, 0, 10);
    //}
  }

  /*
   * Mouse routines.
  */

  @Override
  public void mouseMoved(MouseEvent e) {
    // If nothing is picked and we are over a pickable, turn it solid.
    if (nothingPicked) {
      Ray ray = mouseProj.projectScreenPoint(e.getX(), e.getY());
      double dMin = Double.POSITIVE_INFINITY;
      Pickable closest = null;
      for (Pickable p : pickables) {
        // Test pick.
        final double intersectDist = p.testIntersection(ray);
        if ((intersectDist >= 0) && (intersectDist < dMin)) {
          dMin = intersectDist;
          closest = p;
        }
      }
      if (null != closest) {
        closest.mouseOver();
      }
      else {
        noPickAction.onMouseOver();
      }
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {}

  private ScreenRayProjector mouseProj = new ScreenRayProjector(renderer);
  private int pickCount = 0;

  @Override
  public void mousePressed(MouseEvent e) {
    // Cast ray into world from near clip plane and far clip plane.
    Ray ray = mouseProj.projectScreenPoint(e.getX(), e.getY());
    double dMin = Double.POSITIVE_INFINITY;
    Pickable closest = null;
    for (Pickable p : pickables) {
      // Test pick.
      final double intersectDist = p.testIntersection(ray);
      if ((intersectDist >= 0) && (intersectDist < dMin)) {
        dMin = intersectDist;
        closest = p;
      }
    }
    if (null != closest) {
      noPickAction.onMouseOver();
      closest.pick();
      if (nothingPicked) {
        lastPickedTime = getTime();
      }
      nothingPicked = false;
    }
    else {
      noPickAction.onPicked();
      if (!nothingPicked) {
        totalTimeSelected += getTime() - lastPickedTime;
      }
      nothingPicked = true;
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {}

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}

  @Override
  public void mouseClicked(MouseEvent e) {}
}

