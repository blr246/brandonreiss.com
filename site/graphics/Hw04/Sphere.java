/*
 * Sphere.java
 *
 * A 3d sphere.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

public class Sphere extends Mesh {
  /// <summary> Create a sphere from a segmented cube. </summary>
  public Sphere(int xSegs_, int ySegs_, int zSegs_) {
    final int xSegs = xSegs_; assert(xSegs > 0);
    final int ySegs = ySegs_; assert(ySegs > 0);
    final int zSegs = zSegs_; assert(zSegs > 0);
    // Top and bottom.
    final int numTBVertices = (2 * (xSegs + 1) * (ySegs + 1)) -
                              (2 * 2 * (xSegs + ySegs));
    final int numTBFaces = 2 * xSegs * ySegs;
    // Right, front, left, and back.
    final int numRFLBVertices = (zSegs + 1) * (2 * (xSegs + ySegs));
    final int numRFLBFaces = zSegs * 2 * (xSegs + ySegs);
    // Totals.
    final int numVertices = numTBVertices + numRFLBVertices;
    final int numFaces = numTBFaces + numRFLBFaces;
    // Create vertices counting from 0.
    vertices = new Vector3[numVertices];
    Vector3 stepSizes = new Vector3(2.0 / xSegs, 2.0 / ySegs, 2.0 / zSegs);
    int vIdx = 0;
    // Wrap around right, front, left, and back first.
    for (int faceIdx = 0; faceIdx < 4; ++faceIdx) {
      Vector3 baseOffset = null;
      Vector3 stepOffset = null;
      int stepsThisSide;
      // Right or left are even.
      if (0 == (faceIdx % 2)) { 
        final int baseCoord =  1 - (2 * (faceIdx / 2));
        baseOffset = new Vector3( baseCoord, baseCoord, 1);
        stepOffset = Vector3.vectorMul(new Vector3( 0,-stepSizes.y, 0), baseOffset.x);
        stepsThisSide = ySegs;
      }
      // Front or back are odd.
      else {
        final int baseCoord = -1 + (2 * (faceIdx / 2));
        baseOffset = new Vector3(-baseCoord, baseCoord, 1);
        stepOffset = Vector3.vectorMul(new Vector3( stepSizes.x, 0, 0), baseOffset.y);
        stepsThisSide = xSegs;
      }
      // Create steps of vertices stacked along height.
      for (int stepIdx = 0; stepIdx < stepsThisSide; ++stepIdx) {
        Vector3 baseVertex = Vector3.vectorMul(stepOffset, stepIdx).add(baseOffset);
        for (int htIdx = 0; htIdx <= zSegs; ++htIdx, ++vIdx) {
          assert(vIdx < numVertices);
          vertices[vIdx] = baseVertex.clone();
          vertices[vIdx].z -= stepSizes.z * htIdx;
        }
      }
    }
    // Make top and bottom vertices.
    for (int faceIdx = 0; faceIdx < 2; ++faceIdx) {
      final int baseCoord = 1 - (2 * faceIdx);
      Vector3 baseOffset = new Vector3(1, 1, baseCoord);
      for (int xStep = 1; xStep < xSegs; ++xStep) {
        Vector3 baseVertex = baseOffset.clone();
        baseVertex.x += -xStep * stepSizes.x;
        for (int yStep = 1; yStep < ySegs; ++yStep, ++vIdx) {
          assert(vIdx < numVertices);
          vertices[vIdx] = baseVertex.clone();
          vertices[vIdx].y -= yStep * stepSizes.y;
        }
      }
    }
    assert(numVertices == vIdx);

    // Project vertices to surface of sphere.
    for (Vector3 v : vertices) {
      final double rad = v.length();
      final double scale = 1.0 / rad;
      v.mul(scale);
    }

    // Wind up faces.
    faces = new int[numFaces][];
    int faceIdx = 0;
    // Wrap right, front, left, back first.
    {
      final int edgePitch = zSegs;
      final int vColPitch = zSegs + 1;
      for (; faceIdx < numRFLBFaces; ++faceIdx) {
        final int vIdxEdgeR = faceIdx + (faceIdx / edgePitch);
        final int vIdxEdgeL = (vIdxEdgeR + vColPitch) % numRFLBVertices;
        faces[faceIdx] = new int[] {vIdxEdgeR + 1, vIdxEdgeR,
                                    vIdxEdgeL, vIdxEdgeL + 1,
        };
      }
    }
    // Create an index buffer map to make top and bottom winding easy.
    final int numTBVerticesMap = (xSegs + 1) * (ySegs + 1);
    int[][] tbFaceIdxMap = new int[numTBVerticesMap][2];
    {
      final int rp = ySegs + 1;
      // Shared vertices.
      {
        final int perimPitch = zSegs + 1;
        final int vPerimTRBaseIdx = 0;
        final int vPerimTLBaseIdx = -(xSegs * perimPitch) + numRFLBVertices;
        final int numVPerim = 2 * (xSegs + ySegs);
        for (int r = 0; r <= xSegs; ++r) {
          // Top.
          tbFaceIdxMap[(r * rp) + 0    ][0] = vPerimTRBaseIdx + (r * perimPitch);
          tbFaceIdxMap[(r * rp) + ySegs][0] = vPerimTLBaseIdx - (r * perimPitch);
          // Bottom.
          tbFaceIdxMap[(r * rp) + 0    ][1] = tbFaceIdxMap[(r * rp) + 0    ][0] + zSegs;
          tbFaceIdxMap[(r * rp) + ySegs][1] = tbFaceIdxMap[(r * rp) + xSegs][0] + zSegs;
        }
        for (int edgeIdx = 0; edgeIdx < 2; ++edgeIdx) {
          final int r = edgeIdx * xSegs;
          final int offset = perimPitch * -((2 * edgeIdx) - 1);
          for (int c = ySegs - 1; c > 0; --c) {
            // Top.
            tbFaceIdxMap[(r * rp) + c][0] = tbFaceIdxMap[(r * rp) + c + 1][0] + offset;
            // Bottom.
            tbFaceIdxMap[(r * rp) + c][1] = tbFaceIdxMap[(r * rp) + c][0] + zSegs;
          }
        }
      }
      // Now actual top and bottom vertices.
      {
        int topIdx = numRFLBVertices;
        int botIdx = topIdx + (numTBVertices / 2);
        for (int c = 1; c < ySegs; ++c) {
          for (int r = 1; r < xSegs; ++r, ++topIdx, ++botIdx) {
            tbFaceIdxMap[(r * rp) + c][0] = topIdx;
            tbFaceIdxMap[(r * rp) + c][1] = botIdx;
          }
        }
      }
      // Wind up top and bottom faces.
      {
        for (int tbFaceIdx = 0; tbFaceIdx < (numTBFaces / 2); ++tbFaceIdx) {
          final int vIdxEdgeT = tbFaceIdx + (tbFaceIdx / xSegs);
          final int vIdxEdgeB = vIdxEdgeT + rp;
          // Top.
          {
            faces[faceIdx++] = new int[] {tbFaceIdxMap[vIdxEdgeT    ][0],
                                          tbFaceIdxMap[vIdxEdgeT + 1][0],
                                          tbFaceIdxMap[vIdxEdgeB + 1][0],
                                          tbFaceIdxMap[vIdxEdgeB    ][0],
            };
          }
          // Bottom.
          {
            faces[faceIdx++] = new int[] {tbFaceIdxMap[vIdxEdgeT + 1][1],
                                          tbFaceIdxMap[vIdxEdgeT    ][1],
                                          tbFaceIdxMap[vIdxEdgeB    ][1],
                                          tbFaceIdxMap[vIdxEdgeB + 1][1],
            };
          }
        }
      }
    }
    assert(numFaces == faceIdx);
  }

  /// <summary> Create a sphere from a globe shape. </summary>
  public Sphere(int latSteps_, int longSteps_) {
    final int latSteps = latSteps_; assert(latSteps > 1);
    final int longSteps = longSteps_; assert(longSteps > 1);
    final int numVertices = 2 + ((latSteps - 1) * longSteps);
    final int numFaces = (longSteps * latSteps);

    // Create vertices.
    vertices = new Vector3[numVertices];
    {
      int vIdx = 0;
      // Add top vertex
      vertices[vIdx++] = new Vector3(0, 0, 1);
      // Create vertices. Ignore special case at the poles first.
      final double piDiv2 = Math.PI * 0.5;
      final double phiStep = Math.PI / latSteps;
      final double thetaStep = (2 * Math.PI) / longSteps;
      for (int latStep = 1; latStep < latSteps; ++latStep) {
        final double phi = piDiv2 - (latStep * phiStep);
        final double z = Math.sin(phi);
        final double r = Math.cos(phi);
        for (int longStep = 0; longStep < longSteps; ++longStep, ++vIdx) {
          final double theta = longStep * thetaStep;
          vertices[vIdx] = new Vector3(r * Math.cos(theta),
                                       r * Math.sin(theta), z);
        }
      }
      // Add bottom pole vertex.
      vertices[vIdx++] = new Vector3(0, 0,-1);
      assert(numVertices == vIdx);
    }

    // Wind faces.
    faces = new int[numFaces][];
    {
      int faceIdx = 0;
      // Wind top triangles.
      {
        final int poleIdx = 0;
        final int baseIdx = 1;
        for (int longStep = 0; longStep < longSteps; ++longStep, ++faceIdx) {
          faces[faceIdx] = new int[] {baseIdx + longStep,
                                      baseIdx + ((longStep + 1) % longSteps),
                                      poleIdx,
          };
        }
      }
      // Wind middle faces.
      for (int latStep = 1; latStep < latSteps - 1; ++latStep) {
        final int baseIdxT = 1 + ((latStep - 1) * longSteps);
        final int baseIdxB = baseIdxT + longSteps;
        for (int longStep = 0; longStep < longSteps; ++longStep, ++faceIdx) {
          faces[faceIdx] = new int[] {baseIdxB + longStep,
                                      baseIdxB + ((longStep + 1) % longSteps),
                                      baseIdxT + ((longStep + 1) % longSteps),
                                      baseIdxT + longStep,
          };
        }
      }
      // Wind bottom triangles.
      {
        final int poleIdx = numVertices - 1;
        final int baseIdx = poleIdx - longSteps;
        for (int longStep = 0; longStep < longSteps; ++longStep, ++faceIdx) {
          faces[faceIdx] = new int[] {baseIdx + longStep,
                                      baseIdx + ((longStep + 1) % longSteps),
                                      poleIdx,
          };
        }
      }
      assert(numFaces == faceIdx);
    }
  }
}

