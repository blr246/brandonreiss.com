/*
 * Axes.java
 *
 * A 3d axes gizmo.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;
import com.brandonreiss.math.*;
import java.awt.*;

public class Axes extends SceneObject {
  private Cube[] axes = { new Cube(), new Cube(), new Cube(), };
  private final static double AXIS_MAJ_DIM = 1.0;
  private final static double AXIS_MIN_DIM = 0.02;
  private double scale = 0;

  public Axes() {
    // x.
    {
      axes[0].setWireframe(true);
      Matrix4x4 axisTform = axes[0].getTransformLocal();
      axisTform.identity();
      axisTform.translate(AXIS_MAJ_DIM / 2, 0, 0);
      axisTform.scale(AXIS_MAJ_DIM / 2, AXIS_MIN_DIM / 2, AXIS_MIN_DIM / 2);
    }
    // y.
    {
      axes[1].setWireframe(true);
      Matrix4x4 axisTform = axes[1].getTransformLocal();
      axisTform.identity();
      axisTform.translate(0, AXIS_MAJ_DIM / 2, 0);
      axisTform.scale(AXIS_MIN_DIM / 2, AXIS_MAJ_DIM / 2, AXIS_MIN_DIM / 2);
    }
    // z.
    {
      axes[2].setWireframe(true);
      Matrix4x4 axisTform = axes[2].getTransformLocal();
      axisTform.identity();
      axisTform.translate(0, 0, AXIS_MAJ_DIM / 2);
      axisTform.scale(AXIS_MIN_DIM / 2, AXIS_MIN_DIM / 2, AXIS_MAJ_DIM / 2);
    }
    axes[0].setParent(this);
    axes[1].setParent(this);
    axes[2].setParent(this);
  }

  /// <summary> Set scale. </summary>
  /// <remarks>
  ///   <para> If set, this scale factor will be applied uniformly to
  ///     the axes such that they do not inherit any scaling from parents.
  ///     Setting this value to zero will inherit scale.
  ///   </para>
  /// </remarks>
  public void setScale(double scale_) {
    scale = scale_;
  }

  public void render(Renderer r)
  {
    // Set scale to scale factor.
    if (0 != scale) {
      Matrix4x4 tformLocal = getTransformLocal();
      tformLocal.identity();
      Matrix4x4 tformGlobal = getTransformGlobal().clone();
      // No translation.
      tformGlobal.m[0][3] = 0;
      tformGlobal.m[1][3] = 0;
      tformGlobal.m[2][3] = 0;
      double scaleX = 1;
      {
        Vector4 axis = new Vector4(1, 0, 0, 1);
        axis.transformCoord(tformGlobal);
        axis.w = 0;
        scaleX = scale / axis.length();
      }
      double scaleY = 1;
      {
        Vector4 axis = new Vector4(0, 1, 0, 1);
        axis.transformCoord(tformGlobal);
        axis.w = 0;
        scaleY = scale / axis.length();
      }
      double scaleZ = 1;
      {
        Vector4 axis = new Vector4(0, 0, 1, 1);
        axis.transformCoord(tformGlobal);
        axis.w = 0;
        scaleZ = scale / axis.length();
      }
      tformLocal.scale(scaleX, scaleY, scaleZ);
    }
    r.g.setColor(Color.red);
    axes[0].render(r);
    r.g.setColor(Color.green);
    axes[1].render(r);
    r.g.setColor(Color.blue);
    axes[2].render(r);
  }
}

