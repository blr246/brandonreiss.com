/*
 * Surface.java
 *
 * A 2d surface.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

/// <summary> A mesh surface. </summary>
public class Surface extends Shape {
  private boolean fill = true;

  /// <summary> Either fill or outline rendering. </summary>
  public void setFill(boolean fill_) {
    fill = fill_;
  }

  /// <summary> Render a scene object to the graphics. </summary>
  @Override
  public void render(Renderer r) {
    r.transformVertices(this);
    final int pointsLength = vertices.length;
    if (fill) {
      r.g.fillPolygon(screenPointsX, screenPointsY, pointsLength);
    }
    else {
      r.g.drawPolygon(screenPointsX, screenPointsY, pointsLength);
    }
  }
}

