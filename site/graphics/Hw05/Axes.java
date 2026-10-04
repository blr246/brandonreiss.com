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
  protected Geometry[] axes = { Geometry.CreateCube(),
                                Geometry.CreateCube(),
                                Geometry.CreateCube(), };
  public static final double AXIS_MAJ_DIM = 1.0;
  public static final double AXIS_MIN_DIM = 0.02;
  protected double scale = 0;
  protected Dummy scaleControl = new Dummy();

  public Axes() {
    scaleControl.setParent(this);
    axes[0].setParent(scaleControl);
    axes[1].setParent(scaleControl);
    axes[2].setParent(scaleControl);
    setup(AXIS_MAJ_DIM, AXIS_MIN_DIM);
  }

  public void setup(double axisMajDim, double axisMinDim) {
    // x.
    final double axisMajHalfDim = axisMajDim / 2;
    final double axisMinHalfDim = axisMinDim / 2;
    {
      axes[0].setWireframe(true);
      axes[0].color = Color.red;
      Matrix4x4 axisTform = axes[0].transformLocal;
      axisTform.identity();
      axisTform.translate(axisMajDim / 2, 0, 0);
      axisTform.scale(axisMajHalfDim, axisMinHalfDim, axisMinHalfDim);
    }
    // y.
    {
      axes[1].setWireframe(true);
      axes[1].color = Color.green;
      Matrix4x4 axisTform = axes[1].transformLocal;
      axisTform.identity();
      axisTform.translate(0, axisMajDim / 2, 0);
      axisTform.scale(axisMinHalfDim, axisMajHalfDim, axisMinHalfDim);
    }
    // z.
    {
      axes[2].setWireframe(true);
      axes[2].color = Color.blue;
      Matrix4x4 axisTform = axes[2].transformLocal;
      axisTform.identity();
      axisTform.translate(0, 0, axisMajDim / 2);
      axisTform.scale(axisMinHalfDim, axisMinHalfDim, axisMajHalfDim);
    }
  }

  ///<summarySet scale.</summary>
  ///<remarks>
  ///<para> If set, this scale factor will be applied uniformly to
  ///the axes such that they do not inherit any scaling from parents.
  ///Setting this value to zero will inherit scale.
  ///</para>
  ///</remarks>
  public void setScale(double scale_) {
    scale = scale_;
  }

  private Vector3 axisTmp = new Vector3();

  ///<summary>Update the scale control to maintain spcified scale.</summary>
  ///<remarks>
  ///<para>This method should be called after evaluating the
  ///scene graph.</para>
  ///</remarks>
  public void updateScaleControl() {
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
    scaleControl.transformLocal.identity();
    scaleControl.transformLocal.scale(scaleX, scaleY, scaleZ);
  }

  @Override
  public void render(Renderer r) {
    updateScaleControl();
  }
}

