/*
 * Plane.java
 *
 * A 3d plane.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

public class Plane extends Mesh {
  public Plane() {
    this(1, 1);
  }

  public Plane(int xSegs_, int ySegs_) {
    final int xSegs = xSegs_; assert(xSegs > 0);
    final int ySegs = ySegs_; assert(ySegs > 0);

    // Vertices and faces by plane.
    final int numVertices = (xSegs + 1) * (ySegs + 1);
    final int numFaces = xSegs * ySegs;

    // Create vertices.
    vertices = new Vector3[numVertices];
    {
      Vector2 stepSizes = new Vector2(2.0 / xSegs, 2.0 / ySegs);
      Vector3 baseVertex = Vector3.vectorZero();
      int vIdx = 0;
      // XY plane.
      for (int yStep = 0; yStep <= ySegs; ++yStep) {
        baseVertex.set(-1, 1 - (yStep * stepSizes.y), 0);
        for (int xStep = 0; xStep <= xSegs; ++xStep, ++vIdx) {
          assert(vIdx < numVertices);
          vertices[vIdx] = baseVertex.clone();
          vertices[vIdx].x += xStep * stepSizes.x;
        }
      }
      assert(numVertices == vIdx);
    }

    // Create faces.
    faces = new int[numFaces][];
    {
      Util.WindFacesVertexGrid(0,
                               xSegs + 1, ySegs + 1,
                               Util.WindingDirection.CCW,
                               0, faces);
    }
  }
}

