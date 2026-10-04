/*
 * ScreenRayProjector.java
 *
 * Project screen coordinates using a renderer to get transformations.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

///<summary>Project screen coordinates into a 3d scene.</summary>
public class ScreenRayProjector {
  ///<summary>The 3d scene renderer.</summary>
  private Renderer r = null;
  ///<summary>Temporary used to compute inverse projection matrix.</summary>
  private Matrix4x4 projMatInv = new Matrix4x4();
  ///<summary>The projected screen point.</summary>
  private Ray ray = new Ray();

  public ScreenRayProjector() {}

  public ScreenRayProjector(Renderer r_) {
    r = r_;
  }

  ///<summary>Set the 3d scene renderer.</summary>
  public void setRenderer(Renderer r_) {
    r = r_;
  }

  ///<summary>Project the screen coordinate into the world.</summary>
  public Ray projectScreenPoint(double ptX, double ptY) {
    final double halfScreenX = r.screenSize.x / 2;
    final double halfScreenY = r.screenSize.y / 2;
    final double ptNdcX =  (ptX - halfScreenX) / halfScreenX;
    final double ptNdcY = -(ptY - halfScreenY) / halfScreenY;
    projMatInv.set(r.projMat).inverse();
    Matrix4x4 cameraMat = r.viewMatInv;
    // Initialize ray direction to far clip plane projection.
    ray.dir.set(ptNdcX, ptNdcY, 1);
    ray.dir.transformCoord(projMatInv);
    ray.dir.transformCoord(cameraMat);
    // Set camera position and then subtract from far clip to get direction.
    ray.pt.set(cameraMat.m[0][3], cameraMat.m[1][3], cameraMat.m[2][3]);
    ray.dir.sub(ray.pt).normalize();
    return ray;
  }

  ///<summary>Accessor for the computed ray.</summary>
  ///<remarks>
  ///<para>Call projectScreenPoint() before using this accessor.</para>
  ///</remarks>
  public Ray getRay() {
    return ray;
  }
}

