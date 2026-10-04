/*
 * Torus.java
 *
 * A 3d torus mesh.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

public class Torus extends Mesh {
  public Torus(int thetaSteps_, int phiSteps_, double tubeRatio_) {
    final int thetaSteps = thetaSteps_; assert(thetaSteps > 1);
    final int phiSteps = phiSteps_; assert(phiSteps > 1);
    final double tubeRatio = tubeRatio_;

    final int numVertices = thetaSteps * phiSteps;
    final int numFaces = thetaSteps * phiSteps;
    final double tubeRad = tubeRatio;
    final double ringRad = 1.0 - tubeRad;

    // Create vertices.
    vertices = new Vector3[numVertices];
    {
      int vIdx = 0;
      final double phiStepSize = (2 * Math.PI) / phiSteps;
      final double thetaStepSize = (2 * Math.PI) / thetaSteps;
      // Walk around tube.
      for (int phiStep = 0; phiStep < phiSteps; ++phiStep) {
        final double phi = phiStep * phiStepSize;
        final double z = tubeRad * Math.sin(phi);
        final double ringSliceRad = ringRad + (tubeRad * Math.cos(phi));
        // Walk around ring.
        for (int thetaStep = 0; thetaStep < thetaSteps; ++thetaStep, ++vIdx) {
          final double theta = thetaStep * thetaStepSize;
          final double x = ringSliceRad * Math.cos(theta);
          final double y = ringSliceRad * Math.sin(theta);
          vertices[vIdx] = new Vector3(x, y, z);
        }
      }
    }

    // Create faces.
    faces = new int[numFaces][];
    {
      int faceIdx = 0;
      // Do main grid.
      Util.WindFacesVertexGrid(0,
                               thetaSteps, phiSteps,
                               Util.WindingDirection.CCW,
                               0, faces);
      faceIdx += (thetaSteps - 1) * (phiSteps - 1);
      // Stitch phi.
      for (int sIdx = 0; sIdx < phiSteps; ++sIdx, ++faceIdx) {
        final int ur = sIdx * thetaSteps;
        final int ul = ur + thetaSteps - 1;
        final int br = (ur + thetaSteps) % numVertices;
        final int bl = br + thetaSteps - 1;
        faces[faceIdx] = new int[] {ur, ul, bl, br};
      }
      // Stitch theta.
      {
        final int topBaseIdx = 0;
        final int botBaseIdx = numVertices - thetaSteps;
        for (int sIdx = 0; sIdx < (thetaSteps - 1); ++sIdx, ++faceIdx) {
          final int ul = topBaseIdx + sIdx;
          final int ur = topBaseIdx + ((sIdx + 1) % thetaSteps);
          final int bl = botBaseIdx + sIdx;
          final int br = botBaseIdx + ((sIdx + 1) % thetaSteps);
          faces[faceIdx] = new int[] {ur, ul, bl, br};
        }
      }
      assert(numFaces == faceIdx);
    }
  }
}

