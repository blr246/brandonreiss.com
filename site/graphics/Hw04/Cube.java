/*
 * Cube.java
 *
 * A 3d cube.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

public class Cube extends Mesh {
  /// <summary> Classic unit cube. </summary>
  public Cube() {
    this(1, 1, 1);
  }

  /// <summary> Construct segmented cube. </summary>
  public Cube(int xSegs_, int ySegs_, int zSegs_) {
    final int xSegs = xSegs_; assert(xSegs > 0);
    final int ySegs = ySegs_; assert(ySegs > 0);
    final int zSegs = zSegs_; assert(zSegs > 0);

    // Vertices and faces by plane.
    final int numVerticesXY = (2 * ((xSegs + 1) * (ySegs + 1)));
    final int numVerticesYZ = (2 * ((ySegs + 1) * (zSegs + 1)));
    final int numVerticesXZ = (2 * ((zSegs + 1) * (xSegs + 1)));
    final int numFacesXY = (2 * (xSegs * ySegs));
    final int numFacesYZ = (2 * (ySegs * zSegs));
    final int numFacesXZ = (2 * (zSegs * xSegs));
    // Totals.
    final int numVertices = numVerticesXY + numVerticesYZ + numVerticesXZ;
    final int numFaces = numFacesXY + numFacesYZ + numFacesXZ;

    // Create vertices.
    vertices = new Vector3[numVertices];
    {
      Vector3 stepSizes = new Vector3(2.0 / xSegs, 2.0 / ySegs, 2.0 / zSegs);
      Vector3 baseVertex = Vector3.vectorZero();
      int vIdx = 0;
      // XY plane.
      for (int faceIdx = 0; faceIdx < 2; ++faceIdx) {
        final int z = 1 - (2 * faceIdx); // 1 or -1.
        for (int yStep = 0; yStep <= ySegs; ++yStep) {
          baseVertex.set(-1, 1 - (yStep * stepSizes.y), z);
          for (int xStep = 0; xStep <= xSegs; ++xStep, ++vIdx) {
            assert(vIdx < numVertices);
            vertices[vIdx] = baseVertex.clone();
            vertices[vIdx].x += xStep * stepSizes.x;
          }
        }
      }
      // YZ plane.
      for (int faceIdx = 0; faceIdx < 2; ++faceIdx) {
        final int x = 1 - (2 * faceIdx); // 1 or -1.
        for (int zStep = 0; zStep <= zSegs; ++zStep) {
          baseVertex.set(x, -1, 1 - (zStep * stepSizes.z));
          for (int yStep = 0; yStep <= ySegs; ++yStep, ++vIdx) {
            assert(vIdx < numVertices);
            vertices[vIdx] = baseVertex.clone();
            vertices[vIdx].y += yStep * stepSizes.y;
          }
        }
      }
      // XZ plane.
      for (int faceIdx = 0; faceIdx < 2; ++faceIdx) {
        final int y = 1 - (2 * faceIdx); // 1 or -1.
        for (int zStep = 0; zStep <= zSegs; ++zStep) {
          baseVertex.set(-1, y, 1 - (zStep * stepSizes.z));
          for (int xStep = 0; xStep <= xSegs; ++xStep, ++vIdx) {
            assert(vIdx < numVertices);
            vertices[vIdx] = baseVertex.clone();
            vertices[vIdx].x += xStep * stepSizes.x;
          }
        }
      }
      assert(numVertices == vIdx);
    }

    // Create faces.
    faces = new int[numFaces][];
    {
      int vIdx = 0;
      int faceIdx = 0;
      // XY plane.
      {
        Util.WindFacesVertexGrid(vIdx,
                                 xSegs + 1, ySegs + 1,
                                 Util.WindingDirection.CCW,
                                 faceIdx, faces);
        Util.WindFacesVertexGrid(vIdx + (numVerticesXY / 2),
                                 xSegs + 1, ySegs + 1,
                                 Util.WindingDirection.CW,
                                 faceIdx + (numFacesXY / 2), faces);
        vIdx += numVerticesXY;
        faceIdx += numFacesXY;
      }
      // YZ plane.
      {
        Util.WindFacesVertexGrid(vIdx,
                                 ySegs + 1, zSegs + 1,
                                 Util.WindingDirection.CCW,
                                 faceIdx, faces);
        Util.WindFacesVertexGrid(vIdx + (numVerticesYZ / 2),
                                 ySegs + 1, zSegs + 1,
                                 Util.WindingDirection.CW,
                                 faceIdx + (numFacesYZ / 2), faces);
        vIdx += numVerticesYZ;
        faceIdx += numFacesYZ;
      }
      // XZ plane.
      {
        Util.WindFacesVertexGrid(vIdx,
                                 xSegs + 1, zSegs + 1,
                                 Util.WindingDirection.CCW,
                                 faceIdx, faces);
        Util.WindFacesVertexGrid(vIdx + (numVerticesXZ / 2),
                                 xSegs + 1, zSegs + 1,
                                 Util.WindingDirection.CW,
                                 faceIdx + (numFacesXZ / 2), faces);
        vIdx += numVerticesXZ;
        faceIdx += numFacesXZ;
      }
      assert(numFaces == faceIdx);
    }
  }
}

