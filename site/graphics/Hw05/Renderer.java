/*
 * Renderer.java
 *
 * A scene renderer. Transforms vertices and may apply a special vertex
 * transformation program.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;
import java.awt.*;
import java.util.EnumSet;
import java.util.Vector;

import java.util.Arrays;

///<summary>An 3d renderer.</summary>
public class Renderer { 
  public enum Flags {
    CULL_BACKFACE,
    ZBUFFER_ENABLE,
    WIRE_TRIANGLES,
    ALPHA_BLEND,
    SHADOW_MAP,
  }

  ///<summary>Vertex processing to occur just before rasterization.</summary>
  public interface VertexProgram {
    public void run(Matrix4x4 modelViewMat,
                    Vector3[] in, Vector3[] out);
  }

  ///<summary>The standard modelview vertex program.</summary>
  private class ModelViewTransform implements VertexProgram {
    @Override
    public void run(Matrix4x4 modelViewMat,
                    Vector3[] in, Vector3[] out) {
      final int numVertices = in.length;
      assert(numVertices <= out.length);
      for (int i = 0; i < numVertices; ++i) {
        out[i].set(in[i]).transformCoord(modelViewMat);
      }
    }
  }

  ///<summary>The view matrix inverse.</summary>
  public Matrix4x4 viewMat = Matrix4x4.CreateMatrixIdentity();
  ///<summary>The view matrix inverse.</summary>
  public Matrix4x4 viewMatInv = Matrix4x4.CreateMatrixIdentity();
  ///<symmary>The projection matrix.</summary>
  public Matrix4x4 projMat = Matrix4x4.CreateMatrixIdentity();
  ///<summary>Camera inverse appled to model global transform.</summary>
  private Matrix4x4 modelViewMat = Matrix4x4.CreateMatrixIdentity();
  ///<summary>Width of target screen.</summary>
  public Vector2 screenSize = new Vector2(640, 480);
  ///<summary>The canvas.</summary>
  public Graphics g = null;
  public Color overrideColor = null;

  ///<summary>Set of render flags.</summary>
  public EnumSet<Flags> flags = EnumSet.noneOf(Flags.class);

  final int VEC_CAPACITY_INCR = 10;
  ///<summary>Transformed vertices.</summary>
  private Vector<Vector3> tformVertices = new Vector<Vector3>(0, VEC_CAPACITY_INCR);
  private Vector3[] tformVerticesShader = new Vector3[0];
  ///<summary>Projected vertices.</summary>
  private Vector<Vector3> projVertices = new Vector<Vector3>(0, VEC_CAPACITY_INCR);
  ///<summary>Projected vertices NDC clip status.</summary>
  private boolean[] projClipMask = null;
  ///<summary>Face projection coordinates swap A.</summary>
  private Vector<Vector3> faceVerticesA = new Vector<Vector3>(0, VEC_CAPACITY_INCR);
  ///<summary>Face projection coordinates swap B.</summary>
  private Vector<Vector3> faceVerticesB = new Vector<Vector3>(0, VEC_CAPACITY_INCR);
  // Pixel coordinates.
  int screenPtsCapacity = 0;
  int[] screenPtsX = null;
  int[] screenPtsY = null;

  ///<summary>Camera z-direction used for culling.</summary.
  private Vector3 cameraZDir = new Vector3(0, 0, 1);
  private Vector3 cullEdge0 = new Vector3();
  private Vector3 cullEdge1 = new Vector3();
  private Vector3 cullNormal = new Vector3();

  ///<summary>The default vertex shader.</summary>
  private VertexProgram defaultVertexProgram = new ModelViewTransform();
  ///<summary>Vertex shader program.</summary>
  public VertexProgram vertexProgram = defaultVertexProgram;

  ///<summary>Restore the default vertex program.</summary>
  public void resetVertexProgram() {
    vertexProgram = defaultVertexProgram;
  }

  ///<summary>Render the mesh.</summary>
  public void render(Mesh mesh) {
    // Transform and light vertices.
    Vector3[] vertices = mesh.vertices;
    assert(null != vertices);
    final int verticesLength = vertices.length;
    // Check tformVertices, projVertices, projClipMask size.
    {
      final int tformVerticesSize = tformVertices.size();
      if (verticesLength > tformVerticesSize) {
        final int numToAdd = verticesLength - tformVerticesSize;
        for (int i = 0; i < numToAdd; ++i) {
          tformVertices.add(new Vector3());
          projVertices.add(new Vector3());
        }
        projClipMask = new boolean[verticesLength];
      }
    }
    // Compute model view matrix for this mesh depending on parent.
    modelViewMat.set(viewMat);
    if (mesh.hasParent()) {
      modelViewMat.mul(mesh.transform);
    }
    else {
      modelViewMat.mul(mesh.transformLocal);
    }
    // Vector::toArray() will only allocate a new array when the size changes.
    tformVerticesShader = tformVertices.toArray(tformVerticesShader);
    vertexProgram.run(modelViewMat, vertices, tformVerticesShader);

    // Set the color. This will eventually be per-vertex.
    if (null != overrideColor) {
      g.setColor(overrideColor);
    }
    else {
      g.setColor(mesh.color);
    }

    // Project the vertices and check mesh clip.
    int clipVerticesMesh = 0;
    for (int vIdx = 0; vIdx < verticesLength; ++vIdx) {
      Vector3 vProj = projVertices.get(vIdx);
      // Project vertex.
      vProj.set(tformVertices.get(vIdx)).transformCoord(projMat);
      // Check vertex clip in NDC.
      final boolean clipV = (vProj.min() < -1) || (vProj.max() > 1);
      projClipMask[vIdx] = clipV;
      clipVerticesMesh += clipV ? 1 : 0;
    }
    // Check clip entire mesh.
    if (verticesLength == clipVerticesMesh) {
      return;
    }

    // Setup screen space transform.
    final double halfScreenW = screenSize.x / 2.0;
    final double halfScreenH = screenSize.y / 2.0;

    // Draw the faces.
    int[][] faces = mesh.faces;
    assert(null != faces);
    for (int[] face : faces) {
      assert(null != face);
      final int faceLength = face.length;
      assert(faceLength >= 3);
      // Check projection buffer length.
      {
        final int faceVerticesASize = faceVerticesA.size();
        if (faceLength > faceVerticesASize) {
          final int numToAdd = faceLength - faceVerticesASize;
          for (int i = 0; i < numToAdd; ++i) {
            faceVerticesA.add(new Vector3());
            faceVerticesB.add(new Vector3());
          }
        }
      }
      Vector<Vector3> faceVertices = faceVerticesA;

      // Get projected face and check clipping.
      int clipVerticesFace = 0;
      for (int fIdx = 0; fIdx < faceLength; ++fIdx) {
        Vector3 v = faceVertices.get(fIdx);
        v.set(projVertices.get(face[fIdx]));
        clipVerticesFace += projClipMask[face[fIdx]] ? 1 : 0;
      }
      if (faceLength == clipVerticesFace) {
        continue;
      }

      // Culling.
      if (flags.contains(Flags.CULL_BACKFACE)) {
        // Assume convex and compute normal.
        cullEdge0.set(faceVertices.get(1)).sub(faceVertices.get(0));
        cullEdge1.set(faceVertices.get(2)).sub(faceVertices.get(0));
        // Cross to get face normal.
        cullNormal.set(cullEdge0).cross(cullEdge1);
        // Backfaces point in same direction as camera.
        final double cullCheck = cameraZDir.dot(cullNormal);
        if (cullCheck < 0) {
          continue;
        }
      }

      // Clip when needed.
      int clippedFaceLength = faceLength;
      if (clipVerticesFace > 0) {
        // Clip to projection NDC cuboid (will result to B).
        clippedFaceLength = clipSutherlandHodgman(faceVertices, faceLength,
                                                  faceVerticesB);
        faceVertices = faceVerticesB;
      }

      // Increase screen buffer if clipping emits additional lines.
      if (clippedFaceLength > screenPtsCapacity) {
        screenPtsCapacity = clippedFaceLength;
        screenPtsX = new int[screenPtsCapacity];
        screenPtsY = new int[screenPtsCapacity];
      }
      // Screen transformation.
      for (int fIdx = 0; fIdx < clippedFaceLength; ++fIdx) {
        Vector3 v = faceVertices.get(fIdx);
        screenPtsX[fIdx] = (int)Math.max(Math.min(( v.x * halfScreenW) + halfScreenW,
                                                  screenSize.x - 1), 0);
        screenPtsY[fIdx] = (int)Math.max(Math.min((-v.y * halfScreenH) + halfScreenH,
                                                  screenSize.x - 1), 0);
      }
      if (mesh.wireframe) {
        g.drawPolygon(screenPtsX, screenPtsY, clippedFaceLength);
      }
      else {
        g.fillPolygon(screenPtsX, screenPtsY, clippedFaceLength);
      }
    }
  }

  ///<summary>Temporary used for clipping.</summary>
  private Vector3 clipTmp = new Vector3();
  ///<summary>Temporary used for clipping.</summary>
  private Vector3 clipProjDirTmp = new Vector3();
  ///<summary>Projection cuboid clipped using unit cube.</summary>
  private Vector4[] clipPlanes = {new Vector4( 1, 0, 0,-1),
                                  new Vector4(-1, 0, 0,-1),
                                  new Vector4( 0, 1, 0,-1),
                                  new Vector4( 0,-1, 0,-1),
                                  new Vector4( 0, 0, 1,-1),
                                  new Vector4( 0, 0,-1,-1)};

  ///<summary>Sutherland-Hodgman clip algorithm.</summary>
  ///<remarks>
  ///<para>See
  ///http://en.wikipedia.org/wiki/Sutherland%E2%80%93Hodgman_algorithm
  ///for more information.
  ///</para>
  ///</remarks>
  private int clipSutherlandHodgman(Vector<Vector3> inputList, int faceLength,
                                    Vector<Vector3> outputList) {
    int clippedFaceLength = faceLength;
    // Copy input to output.
    for (int i = 0; i < clippedFaceLength; ++i) {
      outputList.get(i).set(inputList.get(i));
    }
    // Loop over clip polygons (X-axis, Y-axis, Z-axis).
    final int clipPlanesLength = clipPlanes.length;
    for (int clipPlaneIdx = 0;
         (clipPlaneIdx < clipPlanesLength) &&
         (clippedFaceLength > 0); ++clipPlaneIdx) {
      Vector4 clipPlane = clipPlanes[clipPlaneIdx];
      // Copy output to input.
      for (int i = 0; i < clippedFaceLength; ++i) {
        inputList.get(i).set(outputList.get(i));
      }
      // Initialize list sizes, point, inside test.
      final int listSize = inputList.size();
      int outputListSize = 0;
      Vector3 s = inputList.get(clippedFaceLength - 1);
      boolean sInside = Util.PointPlaneDistance(s, clipPlane) <= 0;
      // Test all points in polygon.
      for (int vIdx = 0; vIdx < clippedFaceLength; ++vIdx) {
        Vector3 e = inputList.get(vIdx);
        final boolean eInside = Util.PointPlaneDistance(e, clipPlane) <= 0;
        if (eInside) {
          if (!sInside) {
            // Project to clip plane.
            clipProjDirTmp.set(e).sub(s).normalize();
            Util.PointProjectPlaneAlongDir(clipTmp.set(s), clipPlane,
                                           clipProjDirTmp);
//            // DEBUG clip error.
//            if ((e.z < 0) && (clipTmp.z > 0))
//            {
//              System.out.println("s = " + s + ", e = " + e + ", clipTmp = " + clipTmp);
//            }
            // Add projected point.
            if (outputListSize < listSize) {
              outputList.get(outputListSize).set(clipTmp);
            }
            else {
              outputList.add(new Vector3(clipTmp));
            }
            ++outputListSize;
          }
          // Add original point.
          if (outputListSize < listSize) {
            outputList.get(outputListSize).set(e);
          }
          else {
            outputList.add(new Vector3(e));
          }
          ++outputListSize;
        }
        else if (sInside) {
          // Project to clip plane.
          clipProjDirTmp.set(s).sub(e).normalize();
          Util.PointProjectPlaneAlongDir(clipTmp.set(e), clipPlane,
                                         clipProjDirTmp);
//          // DEBUG clip error.
//          if ((s.z < 0) && (clipTmp.z > 0))
//          {
//              System.out.println("e = " + s + ", s = " + e + ", clipTmp = " + clipTmp);
//          }
          // Add projected point.
          if (outputListSize < listSize) {
            outputList.get(outputListSize).set(clipTmp);
          }
          else {
            outputList.add(new Vector3(clipTmp));
          }
          ++outputListSize;
        }
        s = e;
        sInside = eInside;
      }
      // Sync input list size.
      final int numToAdd = outputListSize - listSize;
      for (int i = 0; i < numToAdd; ++i) {
        inputList.add(new Vector3());
      }
      clippedFaceLength = outputListSize;
    }
    // Return length of result.
    return clippedFaceLength;
  }
}

