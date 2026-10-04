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

import java.util.Arrays;

/// <summary> An 3d renderer. </summary>
public class Renderer { 
  public enum RenderFlags {
    CULL_BACKFACE, CULL_DEPTH,
    WIRE_FACES, WIRE_TRIANGLE,
    ALPHA_BLEND,
    SHADOW_MAP,
  }

  /// <summary> Vertex processing to occur just before rasterization. </summary>
  public interface VertexProgram {
    public void run(Matrix4x4 modelViewMat,
                    Vector3[] in, Vector3[] out);
  }

  /// <summary> The NOP vertex program. </summary>
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

  /// <summary> The view matrix inverse. </summary>
  public Matrix4x4 viewMatInv = Matrix4x4.matrixIdentity();
  /// <symmary> The projection matrix. </summary>
  public Matrix4x4 projMat = Matrix4x4.matrixIdentity();
  /// <summary> The screen pixel coordinates matrix.
  private Matrix4x4 screenMat = Matrix4x4.matrixIdentity();
  /// <summary> Width of target screen. </summary>
  public Vector2 screenSize = new Vector2(640, 480);
  /// <summary> The canvas. </summary>
  public Graphics g = null;
  public Color overrideColor = null;

  /// <summary> Set of render flags. </summary>
  //public EnumSet<RenderFlags> renderFlags = new EnumSet.noneOf(RenderFlags.class);

  /// <summary> Vertex transform capacity. </summary>
  private int tformVerticesCapacity = 3;
  /// <summary> Transformed vertices. </summary>
  private Vector3[] tformVertices = new Vector3[tformVerticesCapacity];

  /// <summary> Projected vertex. </summary>
  private Vector3 projectTmp = new Vector3();

  int screenPtsCapacity = 3;
  int[] screenPtsX = new int[screenPtsCapacity];
  int[] screenPtsY = new int[screenPtsCapacity];

  /// <summary> The default vertex shader. </summary>
  private VertexProgram defaultVertexProgram = new ModelViewTransform();
  /// <summary> Vertex shader program. </summary>
  public VertexProgram vertexProgram = defaultVertexProgram;

  /// <summary> Restore the default vertex program. </summary>
  public void resetVertexProgram() {
    vertexProgram = defaultVertexProgram;
  }

  public Renderer() {
    for (int i = 0; i < tformVerticesCapacity; ++i) {
      tformVertices[i] = new Vector3();
    }
  }

  /// <summary> Render the mesh. </summary>
  public void render(Mesh mesh) {
    // Transform and light vertices.
    Vector3[] vertices = mesh.vertices;
    assert(null != vertices);
    final int verticesLength = vertices.length;
    if (verticesLength > tformVerticesCapacity) {
      tformVerticesCapacity = verticesLength;
      tformVertices = new Vector3[tformVerticesCapacity];
      for (int i = 0; i < tformVerticesCapacity; ++i) {
        tformVertices[i] = new Vector3();
      }
    }
    Matrix4x4 modelViewMat = Matrix4x4.mul(viewMatInv, mesh.transform);
    vertexProgram.run(modelViewMat, vertices, tformVertices);

    // Set the color. This will eventually be per-vertex.
    if (null != overrideColor) {
      g.setColor(overrideColor);
    }
    else {
      g.setColor(mesh.color);
    }

    // Screen space transform.
    final double halfScreenW = screenSize.x / 2.0;
    final double halfScreenH = screenSize.y / 2.0;
    screenMat.identity();
    screenMat.translate(halfScreenW, halfScreenH, 0);
    screenMat.scale(halfScreenW, -halfScreenH, 1);

    // Draw the faces.
    int[][] faces = mesh.faces;
    assert(null != faces);
    for (int[] face : faces) {
      assert(null != face);
      final int faceLength = face.length;
      assert(faceLength >= 3);
      if (faceLength > screenPtsCapacity) {
        screenPtsCapacity = faceLength;
        screenPtsX = new int[faceLength];
        screenPtsY = new int[faceLength];
      }
      // Project.
      // TODO(reissb) -- 20120226 -- Impl Sutherland-Hodgman clip algorithm.
      for (int fIdx = 0; fIdx < faceLength; ++fIdx) {
        projectTmp.set(tformVertices[face[fIdx]]).transformCoord(projMat);
        projectTmp.transformCoord(screenMat);
        screenPtsX[fIdx] = (int)projectTmp.x;
        screenPtsY[fIdx] = (int)projectTmp.y;
      }
      g.drawPolygon(screenPtsX, screenPtsY, faceLength);
    }

  }
}

