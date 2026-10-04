/*
 * Wireframe.java
 *
 * A wireframe mesh with vertices encoding line segments.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

/// <summary> A wireframe mesh. </summary>
/// <remarks>
///   <para> Vertices are pairs indicating line segments. </para>
/// <remarks>
public class Wireframe extends Shape {
  /// <summary> Render a scene object to the graphics. </summary>
  @Override
  public void render(Renderer r) {
    r.transformVertices(this);
    final int pointsLength = vertices.length;
    assert(0 == (pointsLength % 2));
    final int numSegments = pointsLength / 2;
    for (int segIdx = 0; segIdx < numSegments; ++segIdx) {
      int idxBase = segIdx * 2;
      r.g.drawLine(screenPointsX[idxBase + 0], screenPointsY[idxBase + 0],
                   screenPointsX[idxBase + 1], screenPointsY[idxBase + 1]);
    }
  }
}

