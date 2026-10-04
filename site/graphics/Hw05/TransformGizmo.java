/*
 * TransformGizmo.java
 *
 * Interactive object used to translate objects in 3d space.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.interactive;

import com.brandonreiss.math.*;
import com.brandonreiss.geometry.*;
import java.awt.*;
import java.awt.event.*;

///<summary>Axes used to transform a scene object.</summary>
public class TransformGizmo extends Axes implements Pickable, MouseListener, MouseMotionListener {
  private enum TransformState {
    AXIS_NONE, AXIS_X, AXIS_Y, AXIS_Z;

    public static int toIndex(TransformState s) {
      switch (s)
      {
        case AXIS_X: return 0;
        case AXIS_Y: return 1;
        case AXIS_Z: return 2;
      }
      return -1;
    }
  }

  private static final double GIZMO_NORM_SIZE = 0.15;
  private static final double TFORM_SPEED = 0.04;
  private double scale = 1;
  private Renderer renderer = null;
  private PickableSceneObject[] pickableAxes = null;
  private PickableSceneObject[] pickableArrows = null;
  private TransformState tformState = TransformState.AXIS_NONE;
  private ScreenRayProjector screenRayProj = new ScreenRayProjector();
  private Matrix4x4 tformOrigin = new Matrix4x4();
  private Vector2 mouseOrigin = new Vector2();
  private Vector2 mouseMoveTmp = new Vector2();
  private Vector3 projOriginTmp = new Vector3();
  private Vector3 projAxisDir = new Vector3();
  private boolean transforming = false;
  private boolean active = false;
  private Geometry[] arrows = new Geometry[] {Geometry.CreateCone(8, 1),
                                              Geometry.CreateCone(8, 1),
                                              Geometry.CreateCone(8, 1),
  };

  public TransformGizmo() {
    // Wrap each axis in a pickable.
    {
      final int axesLength = axes.length;
      pickableAxes = new PickableSceneObject[axesLength];
      pickableArrows = new PickableSceneObject[axesLength];
      for (int i = 0; i < axesLength; ++i) {
        pickableAxes[i] = new PickableSceneObject(axes[i]);
        pickableArrows[i] = new PickableSceneObject(arrows[i]);
      }
    }
    // Make this thicker than the average axes.
    setup(AXIS_MAJ_DIM, 5 * AXIS_MIN_DIM);
    // Model arrows.
    {
      final double arrowBaseSize = 6 * AXIS_MIN_DIM;
      final double arrowLenSize = 2 * arrowBaseSize;
      final double piDiv2 = Math.PI * 0.5;
      // X.
      {
        arrows[0].setParent(scaleControl);
        arrows[0].transformLocal.identity();
        arrows[0].transformLocal.rotateY(piDiv2);
        arrows[0].transformLocal.translate(0, 0, AXIS_MAJ_DIM + arrowLenSize);
        arrows[0].transformLocal.scale(arrowBaseSize, arrowBaseSize, arrowLenSize);
        arrows[0].color = axes[0].color;
      }
      // Y.
      {
        arrows[1].setParent(scaleControl);
        arrows[1].transformLocal.identity();
        arrows[1].transformLocal.rotateX(-piDiv2);
        arrows[1].transformLocal.translate(0, 0, AXIS_MAJ_DIM + arrowLenSize);
        arrows[1].transformLocal.scale(arrowBaseSize, arrowBaseSize, arrowLenSize);
        arrows[1].color = axes[1].color;
      }
      // Z.
      {
        arrows[2].setParent(scaleControl);
        arrows[2].transformLocal.identity();
        arrows[2].transformLocal.translate(0, 0, AXIS_MAJ_DIM + arrowLenSize);
        arrows[2].transformLocal.scale(arrowBaseSize, arrowBaseSize, arrowLenSize);
        arrows[2].color = axes[2].color;
      }
    }
  }

  public TransformGizmo(Renderer r) {
    this();
    setRenderer(r);
    renderer = r;
  }

  ///<summary>Set the renderer for screen ray projection.</summary>
  public void setRenderer(Renderer r) {
    screenRayProj.setRenderer(r);
  }
  
  ///<summary>Enable or disable active state.</summary>
  public void setActive(boolean state) {
    // Unset axis if set.
    {
      final int axisIdx = TransformState.toIndex(tformState);
      if (axisIdx >= 0) {
        axes[axisIdx].setWireframe(true);
        arrows[axisIdx].setWireframe(true);
      }
    }
    active = state;
    transforming = false;
    tformState = TransformState.AXIS_NONE;
  }

  @Override
  public double testIntersection(Ray ray) {
    if (!active) {
      return -1;
    }
    // See if an axis is activated.
    double dMin = Double.POSITIVE_INFINITY;
    // X.
    {
      {
        final double intersectDist = pickableAxes[0].testIntersection(ray);
        if ((intersectDist >= 0) && (intersectDist < dMin)) {
          dMin = intersectDist;
          tformState = TransformState.AXIS_X;
        }
      }
      {
        final double intersectDist = pickableArrows[0].testIntersection(ray);
        if ((intersectDist >= 0) && (intersectDist < dMin)) {
          dMin = intersectDist;
          tformState = TransformState.AXIS_X;
        }
      }
    }
    // Y.
    {
      {
        final double intersectDist = pickableAxes[1].testIntersection(ray);
        if ((intersectDist >= 0) && (intersectDist < dMin)) {
          dMin = intersectDist;
          tformState = TransformState.AXIS_Y;
        }
      }
      {
        final double intersectDist = pickableArrows[1].testIntersection(ray);
        if ((intersectDist >= 0) && (intersectDist < dMin)) {
          dMin = intersectDist;
          tformState = TransformState.AXIS_Y;
        }
      }
    }
    // Z.
    {
      {
        final double intersectDist = pickableAxes[2].testIntersection(ray);
        if ((intersectDist >= 0) && (intersectDist < dMin)) {
          dMin = intersectDist;
          tformState = TransformState.AXIS_Z;
        }
      }
      {
        final double intersectDist = pickableArrows[2].testIntersection(ray);
        if ((intersectDist >= 0) && (intersectDist < dMin)) {
          dMin = intersectDist;
          tformState = TransformState.AXIS_Z;
        }
      }
    }
    return (Double.POSITIVE_INFINITY == dMin) ? -1 : dMin;
  }

  public void hide() {
    // Propagate visiblity to children.
    for (Geometry g : arrows) {
      g.visible = false;
    }
    // Propagate visiblity to children.
    for (Geometry g : axes) {
      g.visible = false;
    }
  }

  public void unhide() {
    // Propagate visiblity to children.
    for (Geometry g : arrows) {
      g.visible = true;
    }
    // Propagate visiblity to children.
    for (Geometry g : axes) {
      g.visible = true;
    }
  }

  @Override
  public void pick() {
    if (active) {
      // Activate the selected axis.
      transforming = true;
      if (null != parent) {
        tformOrigin.set(parent.transformLocal);
      }
      // Compute direction on screen.
      {
        projOriginTmp.zero();
        projOriginTmp.transformCoord(transform);
        projOriginTmp.transformCoord(renderer.viewMat);
        projAxisDir.zero().setByIndex(TransformState.toIndex(tformState), 1);
        projAxisDir.transformCoord(transform);
        projAxisDir.transformCoord(renderer.viewMat);
        projAxisDir.sub(projOriginTmp);
        final double ar = renderer.screenSize.x / renderer.screenSize.y;
        projAxisDir.x *= ar;
        projAxisDir.y *= -1;
        projAxisDir.normalize();
      }
    }
  }


  @Override
  public void mouseOver() {}

  @Override
  public void mouseMoved(MouseEvent e) {
    // If this is active, then perform rollover events.
    if (!transforming && active) {
      // Unset current.
      {
        final int axisIdx = TransformState.toIndex(tformState);
        if (axisIdx >= 0) {
          axes[axisIdx].setWireframe(true);
          arrows[axisIdx].setWireframe(true);
        }
        tformState = TransformState.AXIS_NONE;
      }
      // See if mousing over.
      Ray ray = screenRayProj.projectScreenPoint(e.getX(), e.getY());
      final double d = testIntersection(ray);
      // Set current.
      {
        final int axisIdx = TransformState.toIndex(tformState);
        if (axisIdx >= 0) {
          axes[axisIdx].setWireframe(false);
          arrows[axisIdx].setWireframe(false);
        }
      }
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if (transforming) {
      // Move the parent based on scale of pixels moved.
      if (null != parent) {
        final int x = e.getX();
        final int y = e.getY();
        mouseMoveTmp.set(x, y);
        mouseMoveTmp.sub(mouseOrigin);
        // Project onto projected axis.
        final double t = mouseMoveTmp.dot(projAxisDir);
        final double offset = scale * TFORM_SPEED * t;
        switch (tformState)
        {
          case AXIS_X:
            parent.transformLocal.set(tformOrigin).translate(offset, 0, 0);
            break;
          case AXIS_Y:
            parent.transformLocal.set(tformOrigin).translate(0, offset, 0);
            break;
          case AXIS_Z:
            parent.transformLocal.set(tformOrigin).translate(0, 0, offset);
            break;
        }
      }
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    mouseOrigin.set(e.getX(), e.getY());
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    transforming = false;
  }

  @Override
  public void mouseEntered(MouseEvent e) {}

  @Override
  public void mouseExited(MouseEvent e) {}

  @Override
  public void mouseClicked(MouseEvent e) {}

  @Override
  public void render(Renderer r) {
    // Scale based on depth.
    {
      projOriginTmp.zero().transformCoord(transform);
      projOriginTmp.transformCoord(renderer.viewMat);
      final double depth = projOriginTmp.z;
      final double size = renderer.projMat.m[0][0] / -depth;
      scale = GIZMO_NORM_SIZE / size;
      setScale(scale);
    }
    // Update scale.
    super.render(r);
  }
}

