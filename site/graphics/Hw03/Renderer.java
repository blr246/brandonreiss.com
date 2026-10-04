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

/// <summary> An orthographic renderer. </summary>
public class Renderer { 
  /// <summary> Vertex processing to occur just before rasterization. </summary>
  public interface VertexProgram {
    public void run(Vector4[] vertices);
  }

  /// <summary> The NOP vertex program. </summary>
  private class VertexPassthrough implements VertexProgram {
    @Override
    public void run(Vector4[] vertices) { }
  }

  /// <summary> The view matrix inverse. </summary>
  public Matrix4x4 viewMatInv = Matrix4x4.matrixIdentity();
  /// <symmary> The projection matrix. </summary>
  public Matrix4x4 projMat = Matrix4x4.matrixIdentity();
  /// <summary> Width of target screen. </summary>
  public Vector2 screenSize = new Vector2(640, 480);
  /// <summary> The canvas. </summary>
  public Graphics g = null;

  private VertexProgram defaultVertexProgram = new VertexPassthrough();
  /// <summary> Vertex program to run just before drawing. </summary>
  public VertexProgram vertexProgram = defaultVertexProgram;

  /// <summary> Restore the default vertex program. </summary>
  public void resetVertexProgram() {
    vertexProgram = defaultVertexProgram;
  }

  public Renderer() { }
  public Renderer(Graphics g_) { g = g_; }

  /// <summary> Transform vertices to screen space for rendering. </summary>
  public void render(Mesh mesh) {
    // Get all rendering arrays.
    Vector4[] vertices = mesh.getVertices();
    assert(null != vertices);
    final int verticesLength = vertices.length;
    Vector4[] tformVertices = mesh.getTformVertices();
    assert((null != tformVertices) && (verticesLength == tformVertices.length));

    // Transform points.
    Matrix4x4 fullMatrix = mesh.getTransformGlobal();
    for (int vIdx = 0; vIdx < verticesLength; ++vIdx) {
      tformVertices[vIdx].set(vertices[vIdx]);
      tformVertices[vIdx].transformCoord(fullMatrix);
    }
    // Run vertex program.
    vertexProgram.run(tformVertices);

    // Apply view matrix inverse to transform to camera space. Apply the
    // projection matrix to get into screen space [-1, 1]. Translate and
    // scale to get 2d image coordinates.
    int[] screenPointsX = mesh.getScreenPointsX();
    int[] screenPointsY = mesh.getScreenPointsY();
    assert((null != screenPointsX) && (verticesLength == screenPointsX.length));
    assert((null != screenPointsY) && (verticesLength == screenPointsY.length));
    final double halfScreenW = screenSize.x / 2.0;
    final double halfScreenH = screenSize.y / 2.0;
    Matrix4x4 screenMat = Matrix4x4.matrixTranslation(halfScreenW, halfScreenH, 0);
    screenMat.scale(halfScreenW, -halfScreenH, 1);
    Matrix4x4 viewTransform = Matrix4x4.mul(screenMat, projMat).mul(viewMatInv);
    for (int vIdx = 0; vIdx < verticesLength; ++vIdx) {
      tformVertices[vIdx].transformCoord(viewTransform);
      screenPointsX[vIdx] = (int)Math.round(tformVertices[vIdx].x);
      screenPointsY[vIdx] = (int)Math.round(tformVertices[vIdx].y);
    }

    // Render them vertices, baby.
    int [][] faces = mesh.getFaces();
    assert(null != faces);
    for (int[] face : faces) {
      assert(null != face);
      final int faceLength = face.length;
      int[] facePtsX = new int[faceLength];
      int[] facePtsY = new int[faceLength];
      for (int facePtIdx = 0; facePtIdx < faceLength; ++facePtIdx) {
        final int ptIdx = face[facePtIdx];
        facePtsX[facePtIdx] = screenPointsX[ptIdx];
        facePtsY[facePtIdx] = screenPointsY[ptIdx];
      }
      if (mesh.wireframe()) {
        g.drawPolygon(facePtsX, facePtsY, faceLength);
      }
      else {
        g.fillPolygon(facePtsX, facePtsY, faceLength);
      }
    }
  }
}

