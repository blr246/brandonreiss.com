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
      axes[0].color = Color.red;
      Matrix4x4 axisTform = axes[0].modelTransformLocal;
      axisTform.identity();
      axisTform.translate(AXIS_MAJ_DIM / 2, 0, 0);
      axisTform.scale(AXIS_MAJ_DIM / 2, AXIS_MIN_DIM / 2, AXIS_MIN_DIM / 2);
    }
    // y.
    {
      axes[1].setWireframe(true);
      axes[1].color = Color.green;
      Matrix4x4 axisTform = axes[1].modelTransformLocal;
      axisTform.identity();
      axisTform.translate(0, AXIS_MAJ_DIM / 2, 0);
      axisTform.scale(AXIS_MIN_DIM / 2, AXIS_MAJ_DIM / 2, AXIS_MIN_DIM / 2);
    }
    // z.
    {
      axes[2].setWireframe(true);
      axes[2].color = Color.blue;
      Matrix4x4 axisTform = axes[2].modelTransformLocal;
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

  private Vector3 axisTmp = new Vector3();
  public void render(Renderer r)
  {
    double scaleX = 1;
    double scaleY = 1;
    double scaleZ = 1;
    // Set scale to scale factor.
    if (0 != scale) {
      {
        axisTmp.set(1, 0, 0);
        axisTmp.transformNormal(transform);
        scaleX = scale / axisTmp.length();
      }
      {
        axisTmp.set(0, 1, 0);
        axisTmp.transformNormal(transform);
        scaleY = scale / axisTmp.length();
      }
      {
        axisTmp.set(0, 0, 1);
        axisTmp.transformNormal(transform);
        scaleZ = scale / axisTmp.length();
      }
    }
    // Update the axes.
    for (Cube ax : axes) {
      ax.animTransformLocal.identity();
      ax.animTransformLocal.scale(scaleX, scaleY, scaleZ);
    }
  }
}

