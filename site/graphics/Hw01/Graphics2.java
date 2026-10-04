/*
 * Graphics2.java
 *
 * A 2d shape library using java.awt.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
import java.awt.*;

public class Graphics2 {
  /// <summary> X-Y axis aligned box. </summary>
  public static class AxisAlignedBoundingBox {
    public int xMin;
    public int yMin;
    public int xMax;
    public int yMax;

    AxisAlignedBoundingBox() {
      xMin = 0; yMin = 0;
      xMax = 1; yMax = 1;
    }

    AxisAlignedBoundingBox(int xMin_, int xMax_, int yMin_, int yMax_) {
      xMin = xMin_; xMax = xMax_;
      yMin = yMin_; yMax = yMax_;
    }

    /// <summary> Create from list of points. </summary>
    public static AxisAlignedBoundingBox fromPoints(Vector3[] points) {
      AxisAlignedBoundingBox res = new AxisAlignedBoundingBox();
      res.xMin = Integer.MAX_VALUE;
      res.xMax = Integer.MIN_VALUE;
      res.yMin = Integer.MAX_VALUE;
      res.yMax = Integer.MIN_VALUE;
      for (Vector3 p : points) {
        res.xMin = Math.min(res.xMin, (int)p.x);
        res.xMax = Math.max(res.xMax, (int)p.x);
        res.yMin = Math.min(res.yMin, (int)p.y);
        res.yMax = Math.max(res.yMax, (int)p.y);
      }
      return res;
    }

    /// <summary> Merge to bounding boxes. </summary>
    public AxisAlignedBoundingBox merge(AxisAlignedBoundingBox rhs) {
      return new AxisAlignedBoundingBox(Math.min(xMin, rhs.xMin), Math.max(xMax, rhs.xMax),
                                        Math.min(yMin, rhs.yMin), Math.max(yMax, rhs.yMax));
    }

    /// <summary> Scale the bounding box. </summary>
    public AxisAlignedBoundingBox scale(float sx, float sy) {
      float w = (float)(xMax - xMin);
      float h = (float)(yMax - yMin);
      int dx = (int)(((w * sx) - w) / 2.0f);
      int dy = (int)(((h * sy) - h) / 2.0f);
      return new AxisAlignedBoundingBox(xMin - dx, xMax + dx,
                                        yMin - dy, yMax + dy);
    }

    /// <summary> Test intersection. </summary>
    public boolean intersects(AxisAlignedBoundingBox rhs) {
//      assert(xMin <= xMax); assert(rhs.xMin <= rhs.xMax);
//      assert(yMin <= yMax); assert(rhs.yMin <= rhs.yMax);
      return (Math.max(xMin, rhs.xMin) <= Math.min(xMax, rhs.xMax)) &&
             (Math.max(yMin, rhs.yMin) <= Math.min(yMax, rhs.yMax));
    }

    /// <summary> Render the box. </summary>
    public void render(Graphics g) {
      g.drawRect(xMin, yMin, xMax - xMin, yMax - yMin);
    }

    public String toString() {
      return "[ x:(" + xMin + ", " + xMax + "), " +
               "y:(" + yMin + ", " + yMax + ") ]";
    }
  }

  /// <summary> Renderable object with transformation properties. </summary>
  public static abstract class SceneOject {
    public SceneOject parent = null;
    public float rotationRad = 0.0f;
    public Vector3 translation = Vector3.zero();
    public Vector3 scale = new Vector3(1, 1, 1);
    public Matrix3x3 fullMatrix = Matrix3x3.identity();
    public boolean fill = false;

    private double lastUpdate = -1.0;

    /// <summary> Render at the given time. </summary>
    public void render(double time, Graphics g) {
      updateTransform(time);
      onRender(time, g);
    }

    /// <swummary> Get shape bounding box (not implemented by default). </summary>
    public Graphics2.AxisAlignedBoundingBox aabb(double time) {
      assert(false); return null;
    }          

    /// <summary> Internal call to render at the given time. </summary>
    protected abstract void onRender(double time, Graphics g);
    
    /// <summary> Update the matrix transform at the given tme. </summary>
    /// <remarks>
    ///   <para> The time is used to mark the matrix as dirty. </para>
    /// </remarks>
    protected void updateTransform(double time) {
      if (null != parent) {
        parent.updateTransform(time);
      }
      if (time > lastUpdate) {
        Matrix3x3 matTrans = Matrix3x3.matrixTranslation(translation.x,
                                                         translation.y);
        Matrix3x3 matRot = Matrix3x3.matrixRotation(rotationRad);
        Matrix3x3 matScale = Matrix3x3.matrixScale(scale.x, -scale.y);
        fullMatrix = matTrans.mul(matRot.mul(matScale));
        if (null != parent) {
          fullMatrix = parent.fullMatrix.mul(fullMatrix);
        }
      }
    }
  }

  /// <summary> A polygon. </summary>
  public static class Shape extends SceneOject {
    public Vector3[] points = null;
    private Vector3[] tformPoints = null;
    private int[] renderPointsX = null;
    private int[] renderPointsY = null;

    private void generateRenderPoints() {
      // Copy points.
      int pointsLength = points.length;
      boolean isAlloc = (null != tformPoints) &&
                        (null != renderPointsX) && (null != renderPointsY);
      boolean sizeMatch = isAlloc &&
                          (tformPoints.length == pointsLength) &&
                          (renderPointsX.length == pointsLength) &&
                          (renderPointsY.length == pointsLength);
      if (!isAlloc || !sizeMatch) {
        tformPoints = new Vector3[pointsLength];
        renderPointsX = new int[pointsLength];
        renderPointsY = new int[pointsLength];
      }
      System.arraycopy(points, 0, tformPoints, 0, pointsLength);
      // Transform points.
      for (int ptIdx = 0; ptIdx < pointsLength; ++ptIdx) {
        tformPoints[ptIdx] = points[ptIdx].transformCoord(fullMatrix);
      }
      // Truncate points.
      for (int ptIdx = 0; ptIdx < pointsLength; ++ptIdx) {
        renderPointsX[ptIdx] = Math.round(tformPoints[ptIdx].x);
        renderPointsY[ptIdx] = Math.round(tformPoints[ptIdx].y);
      }
    }

    protected void onRender(double time, Graphics g) {
      assert(null != points);

      //// DEBUG points.
      //System.out.println("fullMatrix:\n" + fullMatrix);
      //for (int ptIdx = 0; ptIdx < pointsLength; ++ptIdx) {
      //  System.out.println("" + ptIdx + ": (" +
      //                     renderPointsX[ptIdx] + "," + renderPointsY[ptIdx] +
      //                     ")");
      //}

      // Render points.
      generateRenderPoints();
      int pointsLength = points.length;
      if (fill) {
        g.fillPolygon(renderPointsX, renderPointsY, pointsLength);
      }
      else {
        g.drawPolygon(renderPointsX, renderPointsY, pointsLength);
      }
    }

    /// <swummary> Get shape bounding box. </summary>
    public AxisAlignedBoundingBox aabb(double time) {
      updateTransform(time);
      generateRenderPoints();
      return AxisAlignedBoundingBox.fromPoints(tformPoints);
    }
  }


  public static class Box extends Shape {
    Box() {
      points = new Vector3[] {new Vector3( 0.5f,  0.5f, 1.0f),
                              new Vector3(-0.5f,  0.5f, 1.0f),
                              new Vector3(-0.5f, -0.5f, 1.0f),
                              new Vector3( 0.5f, -0.5f, 1.0f)};
    }
  }

  public static class Star extends Shape {
    Star() {
      points = new Vector3[] {new Vector3( 0.00f,  0.50f, 1.0f),

                              new Vector3(-0.14f,  0.17f, 1.0f),
                              new Vector3(-0.50f,  0.17f, 1.0f),
                              new Vector3(-0.21f, -0.07f, 1.0f),
                              new Vector3(-0.41f, -0.50f, 1.0f),

                              new Vector3( 0.00f, -0.20f, 1.0f),

                              new Vector3( 0.41f, -0.50f, 1.0f),
                              new Vector3( 0.21f, -0.07f, 1.0f),
                              new Vector3( 0.50f,  0.17f, 1.0f),
                              new Vector3( 0.14f,  0.17f, 1.0f)};
    }
  }

  public static class Circle extends SceneOject {
    protected void onRender(double time, Graphics g) {
      // Get the width, height of the standard box.
      Vector3 centerLcl = new Vector3(0.0f, 0.0f, 1.0f);
      Vector3 centerScreen = centerLcl.transformCoord(fullMatrix);
      Vector3 surfaceLcl = new Vector3(-0.5f, 0.0f, 1.0f);
      Vector3 sucfaceScreen = surfaceLcl.transformCoord(fullMatrix);
      int rad = (int)sucfaceScreen.sub(centerScreen).length();
      int diameter = rad * 2;
      if (fill) {
        g.fillOval((int)centerScreen.x - rad, (int)centerScreen.y - rad,
                   diameter, diameter);
      }
      else {
        g.drawOval((int)centerScreen.x - rad, (int)centerScreen.y - rad,
                   diameter, diameter);
      }
    }
  }
}

