/*
 * Geometry.java
 *
 * Master class for all geometric primitives.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;
import java.util.Arrays;

public class Geometry extends Mesh {

  ///<summary>Construction is through public CreateXXX methods.</summary>
  private Geometry() {}

  ///<summary>Static initializer for empty shape.</summary>
  public static Geometry CreateNullShape() {
    Geometry g = new Geometry();
    g.vertices = new Vector3[0];
    g.faces = new int[0][0];
    return g;
  }

  /* --------------------------------------------------------------------------
   * Plane
   * --------------------------------------------------------------------------
  */
  
  ///<summary>Static initializer for plane.</summary>
  public static Geometry CreatePlane() {
    return (new Geometry()).Plane();
  }
  ///<summary>Static initializer for plane.</summary>
  public static Geometry CreatePlane(int xSegs, int ySegs) {
    return (new Geometry()).Plane(xSegs, ySegs);
  }

  ///<summary>Unit plane.</summary>
  public Geometry Plane() {
    return Plane(1, 1);
  }

  ///<summary>Segmented plane.</summary>
  ///<param name="xSegs">Segments along X-axis (greater than 0)</param>
  ///<param name="ySegs">Segments along Y-axis (greater than 0)</param>
  public Geometry Plane(int xSegs, int ySegs) {
    // Check cache.
    String instanceKey = "Plane" + "|" + xSegs + "|" + ySegs;
    {
      StaticInstanceMap.Record instance = StaticInstanceMap.get(instanceKey);
      if (null != instance) {
        vertices = instance.vertices;
        faces = instance.faces;
        return this;
      }
    }

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

    // Cache this instance.
    StaticInstanceMap.put(instanceKey, vertices, faces);
    return this;
  }

  /* --------------------------------------------------------------------------
   * Sphere
   * --------------------------------------------------------------------------
  */

  ///<summary>Static initializer for sphere.</summary>
  public static Geometry CreateSphere(int xSegs, int ySegs, int zSegs) {
    return (new Geometry()).Sphere(xSegs, ySegs, zSegs);
  }
  ///<summary>Static initializer for sphere.</summary>
  public static Geometry CreateSphere(int longSteps, int latSteps) {
    return (new Geometry()).Sphere(longSteps, latSteps);
  }

  ///<summary>Create a sphere from a segmented cube.</summary>
  ///<param name="xSegs">Segments along X-axis (greater than 0)</param>
  ///<param name="ySegs">Segments along Y-axis (greater than 0)</param>
  ///<param name="zSegs">Segments along Z-axis (greater than 0)</param>
  public Geometry Sphere(int xSegs, int ySegs, int zSegs) {
    // Check cache.
    String instanceKey = "Sphere" + "|" + xSegs + "|" + ySegs + "|" + zSegs;
    {
      StaticInstanceMap.Record instance = StaticInstanceMap.get(instanceKey);
      if (null != instance) {
        vertices = instance.vertices;
        faces = instance.faces;
        return this;
      }
    }

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
        stepOffset = (new Vector3( 0,-stepSizes.y, 0)).mul(baseOffset.x);
        stepsThisSide = ySegs;
      }
      // Front or back are odd.
      else {
        final int baseCoord = -1 + (2 * (faceIdx / 2));
        baseOffset = new Vector3(-baseCoord, baseCoord, 1);
        stepOffset = (new Vector3( stepSizes.x, 0, 0)).mul(baseOffset.y);
        stepsThisSide = xSegs;
      }
      // Create steps of vertices stacked along height.
      for (int stepIdx = 0; stepIdx < stepsThisSide; ++stepIdx) {
        Vector3 baseVertex = (new Vector3(stepOffset)).mul(stepIdx).add(baseOffset);
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

    // Cache this instance.
    StaticInstanceMap.put(instanceKey, vertices, faces);
    return this;
  }

  ///<summary>Create a sphere from a globe shape.</summary>
  ///<remarks>
  ///<para>Both segmentation parameters must be greater than 1.</para>
  ///</remarks>
  public Geometry Sphere(int longSteps, int latSteps) {
    // Check cache.
    String instanceKey = "Sphere" + "|" + longSteps + "|" + latSteps;
    {
      StaticInstanceMap.Record instance = StaticInstanceMap.get(instanceKey);
      if (null != instance) {
        vertices = instance.vertices;
        faces = instance.faces;
        return this;
      }
    }
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
          faces[faceIdx] = new int[] {baseIdx + ((longStep + 1) % longSteps),
                                      baseIdx + longStep,
                                      poleIdx,
          };
        }
      }
      assert(numFaces == faceIdx);
    }

    // Cache this instance.
    StaticInstanceMap.put(instanceKey, vertices, faces);
    return this;
  }

  /* --------------------------------------------------------------------------
   * Cube
   * --------------------------------------------------------------------------
  */

  ///<summary>Static initializer for cube.</summary>
  public static Geometry CreateCube() {
    return (new Geometry()).Cube();
  }
  ///<summary>Static initializer for sphere.</summary>
  public static Geometry CreateCube(int xSegs, int ySegs, int zSegs) {
    return (new Geometry()).Cube(xSegs, ySegs, zSegs);
  }

  ///<summaryStandard unit cube.</summary>
  public Geometry Cube() {
    return Cube(1, 1, 1);
  }

  ///<summary>Construct segmented cube.</summary>
  ///<param name="xSegs">Segments along X-axis (greater than 0)</param>
  ///<param name="ySegs">Segments along Y-axis (greater than 0)</param>
  ///<param name="zSegs">Segments along Z-axis (greater than 0)</param>
  public Geometry Cube(int xSegs, int ySegs, int zSegs) {
    // Check cache.
    String instanceKey = "Cube" + "|" + xSegs + "|" + ySegs + "|" + zSegs;
    {
      StaticInstanceMap.Record instance = StaticInstanceMap.get(instanceKey);
      if (null != instance) {
        vertices = instance.vertices;
        faces = instance.faces;
        return this;
      }
    }

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
                                 Util.WindingDirection.CW,
                                 faceIdx, faces);
        Util.WindFacesVertexGrid(vIdx + (numVerticesXZ / 2),
                                 xSegs + 1, zSegs + 1,
                                 Util.WindingDirection.CCW,
                                 faceIdx + (numFacesXZ / 2), faces);
        vIdx += numVerticesXZ;
        faceIdx += numFacesXZ;
      }
      assert(numFaces == faceIdx);
    }

    // Cache this instance.
    StaticInstanceMap.put(instanceKey, vertices, faces);
    return this;
  }

  /* --------------------------------------------------------------------------
   * Cylinder
   * --------------------------------------------------------------------------
  */

  ///<summary>Static initializer for cylinder.</summary>
  public static Geometry CreateCylinder(int thetaSteps, int zSegs) {
    return (new Geometry()).Cylinder(thetaSteps, zSegs);
  }

  ///<summary>Create a sphere from a segmented cube.</summary>
  ///<param name="thetaSteps">Segments along circumference (greater than 1)</param>
  ///<param name="zSegs">Segments along height (greater than 0)</param>
  public Geometry Cylinder(int thetaSteps, int zSegs) {
    // Check cache.
    String instanceKey = "Cylinder" + "|" + thetaSteps + "|" + zSegs;
    {
      StaticInstanceMap.Record instance = StaticInstanceMap.get(instanceKey);
      if (null != instance) {
        vertices = instance.vertices;
        faces = instance.faces;
        return this;
      }
    }

    final int tubeVertices = (thetaSteps * (zSegs + 1));
    final int capVertices = 2 + (2 * thetaSteps);
    final int numVertices = tubeVertices + capVertices;
    final int tubeFaces = thetaSteps * zSegs;
    final int capFaces = 2 * thetaSteps;
    final int numFaces = tubeFaces + capFaces;

    // Create vertices.
    vertices = new Vector3[numVertices];
    {
      int vIdx = 0;
      final double thetaStepSize = (2 * Math.PI) / thetaSteps;
      // Create the tube.
      {
        final double zStepSize = 2.0 / zSegs;
        for (int zStep = 0; zStep < zSegs; ++zStep) {
          final double z = 1.0 - (zStepSize * zStep);
          for (int thetaStep = 0; thetaStep < thetaSteps; ++thetaStep, ++vIdx) {
            final double theta = thetaStep * thetaStepSize;
            final double ax = Math.cos(theta);
            final double ay = Math.sin(theta);
            vertices[vIdx] = new Vector3(ax, ay, z);
          }
        }
        // Create bottom tube (explicit z).
        for (int thetaStep = 0; thetaStep < thetaSteps; ++thetaStep, ++vIdx) {
          final double theta = thetaStep * thetaStepSize;
          final double ax = Math.cos(theta);
          final double ay = Math.sin(theta);
          vertices[vIdx] = new Vector3(ax, ay, -1);
        }
      }
      // Create caps.
      {
        for (int capIdx = 0; capIdx < 2; ++capIdx, vIdx++) {
          final double z = 1.0 - (2 * capIdx);
          for (int thetaStep = 0; thetaStep < thetaSteps; ++thetaStep, ++vIdx) {
            final double theta = thetaStep * thetaStepSize;
            final double ax = Math.cos(theta);
            final double ay = Math.sin(theta);
            vertices[vIdx] = new Vector3(ax, ay, z);
          }
          vertices[vIdx] = new Vector3(0, 0, z);
        }
      }
      assert(numVertices == vIdx);
    }

    // Create faces.
    faces = new int[numFaces][];
    {
      int faceIdx = 0;
      // Tube wrap.
      {
        Util.WindFacesVertexGrid(0, thetaSteps, zSegs + 1,
                                 Util.WindingDirection.CCW, 0, faces);
        faceIdx = (thetaSteps - 1) * zSegs;
        // Stitch theta.
        {
          final int baseIdxR = 0;
          final int baseIdxL = baseIdxR + (thetaSteps - 1);
          for (int sIdx = 0; sIdx < zSegs; ++sIdx, ++faceIdx) {
            final int ur = baseIdxR + (sIdx * thetaSteps);
            final int ul = baseIdxL + (sIdx * thetaSteps);
            final int br = ur + thetaSteps;
            final int bl = ul + thetaSteps;
            faces[faceIdx] = new int[] {ur, ul, bl, br};
          }
        }
      }
      // Wind top triangles.
      {
        final int baseIdx = tubeVertices;
        final int poleIdx = baseIdx + thetaSteps;
        for (int thetaStep = 0; thetaStep < thetaSteps; ++thetaStep, ++faceIdx) {
          faces[faceIdx] = new int[] {baseIdx + thetaStep,
                                      baseIdx + ((thetaStep + 1) % thetaSteps),
                                      poleIdx,
          };
        }
      }
      // Wind bottom triangles.
      {
        final int baseIdx = tubeVertices + thetaSteps + 1;
        final int poleIdx = baseIdx + thetaSteps;
        for (int thetaStep = 0; thetaStep < thetaSteps; ++thetaStep, ++faceIdx) {
          faces[faceIdx] = new int[] {baseIdx + ((thetaStep + 1) % thetaSteps),
                                      baseIdx + thetaStep,
                                      poleIdx,
          };
        }
      }
      assert(numFaces == faceIdx);
    }

    // Cache this instance.
    StaticInstanceMap.put(instanceKey, vertices, faces);
    return this;
  }

  /* --------------------------------------------------------------------------
   * Cone
   * --------------------------------------------------------------------------
  */

  ///<summary>Static initializer for cone.</summary>
  public static Geometry CreateCone(int thetaSteps, int zSegs) {
    return (new Geometry()).Cone(thetaSteps, zSegs);
  }

  ///<summaryCreate a sphere from a segmented cube.</summary>
  ///<param name="thetaSteps">Steps around circumference (greater than 1)</param>
  ///<param name="zSegs">Steps along height (greater than 0)</param>
  public Geometry Cone(int thetaSteps, int zSegs) {
    // Check cache.
    String instanceKey = "Cone" + "|" + thetaSteps + "|" + zSegs;
    {
      StaticInstanceMap.Record instance = StaticInstanceMap.get(instanceKey);
      if (null != instance) {
        vertices = instance.vertices;
        faces = instance.faces;
        return this;
      }
    }

    final int tubeVertices = 1 + (thetaSteps * zSegs);
    final int capVertices = 1 + thetaSteps;
    final int numVertices = tubeVertices + capVertices;
    final int tubeFaces = thetaSteps * zSegs;
    final int capFaces = thetaSteps;
    final int numFaces = tubeFaces + capFaces;

    // Create vertices.
    vertices = new Vector3[numVertices];
    {
      int vIdx = 0;
      final double thetaStepSize = (2 * Math.PI) / thetaSteps;
      // Peak vertex.
      vertices[vIdx++] = new Vector3(0, 0, 1);
      // Create the tube.
      {
        final double zStepSize = 2.0 / zSegs;
        final double radStepSize = 1.0 / zSegs;
        for (int zStep = 1; zStep < zSegs; ++zStep) {
          final double z = 1.0 - (zStepSize * zStep);
          final double r = radStepSize * zStep;
          for (int thetaStep = 0; thetaStep < thetaSteps; ++thetaStep, ++vIdx) {
            final double theta = thetaStep * thetaStepSize;
            final double ax = r * Math.cos(theta);
            final double ay = r * Math.sin(theta);
            vertices[vIdx] = new Vector3(ax, ay, z);
          }
        }
      }
      // Create caps.
      for (int capIdx = 0; capIdx < 2; ++capIdx) {
        for (int thetaStep = 0; thetaStep < thetaSteps; ++thetaStep, ++vIdx) {
          final double theta = thetaStep * thetaStepSize;
          final double ax = Math.cos(theta);
          final double ay = Math.sin(theta);
          vertices[vIdx] = new Vector3(ax, ay, -1);
        }
      }
      vertices[vIdx++] = new Vector3(0, 0, -1);
      assert(numVertices == vIdx);
    }

    // Create faces.
    faces = new int[numFaces][];
    {
      int faceIdx = 0;
      // Wind top triangles.
      {
        final int poleIdx = 0;
        final int baseIdx = poleIdx + 1;
        for (int thetaStep = 0; thetaStep < thetaSteps; ++thetaStep, ++faceIdx) {
          faces[faceIdx] = new int[] {baseIdx + thetaStep,
                                      baseIdx + ((thetaStep + 1) % thetaSteps),
                                      poleIdx,
          };
        }
      }
      // Tube wrap.
      final int baseIdxR = 1;
      faceIdx += Util.WindFacesVertexGrid(baseIdxR,
                                          thetaSteps, zSegs,
                                          Util.WindingDirection.CCW,
                                          faceIdx, faces);
      // Stitch theta.
      {
        final int baseIdxL = baseIdxR + (thetaSteps - 1);
        for (int sIdx = 0; sIdx < (zSegs - 1); ++sIdx, ++faceIdx) {
          final int ur = baseIdxR + (sIdx * thetaSteps);
          final int ul = baseIdxL + (sIdx * thetaSteps);
          final int br = ur + thetaSteps;
          final int bl = ul + thetaSteps;
          faces[faceIdx] = new int[] {ur, ul, bl, br};
        }
      }
      // Wind bottom triangles.
      {
        final int baseIdx = tubeVertices;
        final int poleIdx = baseIdx + thetaSteps;
        for (int thetaStep = 0; thetaStep < thetaSteps; ++thetaStep, ++faceIdx) {
          faces[faceIdx] = new int[] {baseIdx + ((thetaStep + 1) % thetaSteps),
                                      baseIdx + thetaStep,
                                      poleIdx,
          };
        }
      }
      assert(numFaces == faceIdx);
    }

    // Cache this instance.
    StaticInstanceMap.put(instanceKey, vertices, faces);
    return this;
  }

  /* --------------------------------------------------------------------------
   * Torus
   * --------------------------------------------------------------------------
  */

  ///<summary>Static initializer for torus.</summary>
  public static Geometry CreateTorus(int thetaSteps, int phiSteps, double tubeRad) {
    return (new Geometry()).Torus(thetaSteps, phiSteps, tubeRad);
  }


  ///<summary>A 3d torus or doughnut.</summary>
  ///<param name="thetaSteps">Segments around circumference (greater than 1)</param>
  ///<param name="phiSteps">Segments around tube (greater than 1)</param>
  ///<param name="tubeRad">Tube radius</param>
  public Geometry Torus(int thetaSteps, int phiSteps, double tubeRad) {
    // Check cache.
    String instanceKey = "Torus" + "|" + thetaSteps + "|" + phiSteps + "|" + tubeRad;
    {
      StaticInstanceMap.Record instance = StaticInstanceMap.get(instanceKey);
      if (null != instance) {
        vertices = instance.vertices;
        faces = instance.faces;
        return this;
      }
    }

    final int numVertices = thetaSteps * phiSteps;
    final int numFaces = thetaSteps * phiSteps;
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
                               Util.WindingDirection.CW,
                               0, faces);
      faceIdx += (thetaSteps - 1) * (phiSteps - 1);
      // Stitch phi.
      for (int sIdx = 0; sIdx < phiSteps; ++sIdx, ++faceIdx) {
        final int ur = sIdx * thetaSteps;
        final int ul = ur + thetaSteps - 1;
        final int br = (ur + thetaSteps) % numVertices;
        final int bl = br + thetaSteps - 1;
        faces[faceIdx] = new int[] {ul, ur, br, bl};
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

    // Cache this instance.
    StaticInstanceMap.put(instanceKey, vertices, faces);
    return this;
  }
}

