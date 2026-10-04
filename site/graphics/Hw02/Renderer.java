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
  public Matrix4x4 viewMatInv = new Matrix4x4( 1.0, 0.0, 0.0, 0.0,
                                               0.0, 1.0, 0.0,-2.0,
                                               0.0, 0.0, 1.0,-8.0,
                                               0.0, 0.0, 0.0, 1.0);
//  public Matrix4x4 viewMatInv = new Matrix4x4( 0.0, 1.0, 0.0,-1.5,
//                                               0.0, 0.0, 1.0,-3.0,
//                                               1.0, 0.0, 0.0,-8.0,
//                                               0.0, 0.0, 0.0, 1.0);
  /// <summary> Scale to adjust camera fov. </summary>
  public double fovScale = 1;
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
  /// <remarks>
  ///   <para> For best performance, use the same arrays each frame
  ///     so that they do not need to be re-allocated.
  ///   </para>
  /// </remarks>
  public void transformVertices(Shape shape) {
    // Copy points for transformation.
    Vector4[] vertices = shape.getVertices();
    assert(null != vertices);
    final int verticesLength = vertices.length;
    Vector4[] tformVertices = shape.getTformVertices();
    int[] screenPointsX = shape.getScreenPointsX();
    int[] screenPointsY = shape.getScreenPointsY();
    assert((null != tformVertices) && (verticesLength == tformVertices.length));
    assert((null != screenPointsX) && (verticesLength == screenPointsX.length));
    assert((null != screenPointsY) && (verticesLength == screenPointsY.length));
    System.arraycopy(vertices, 0, tformVertices, 0, verticesLength);
    // Transform points.
    Matrix4x4 fullMatrix = shape.getTransformGlobal();
    for (int vIdx = 0; vIdx < verticesLength; ++vIdx) {
      tformVertices[vIdx] = vertices[vIdx].transformCoord(fullMatrix);
    }
    // Run vertex program.
    vertexProgram.run(tformVertices);
    // Apply view matrix inverse to transform to camera space.
    Matrix4x4 fovScaleMat = Matrix4x4.matrixScale(fovScale, fovScale, 1);
    // Scale points [-1, 1], by half width to get [-w_div_2, w_div_2]. Then
    // transform by w_div_2 to get [0, w] for screen rendering.
    final double halfScreenW = screenSize.x / 2.0;
    final double halfScreenH = screenSize.y / 2.0;
    Matrix4x4 screenMat = Matrix4x4.matrixScale(halfScreenW, -halfScreenW, 0);
    screenMat.translate(halfScreenW, halfScreenH, 0);
    Matrix4x4 viewTransform = Matrix4x4.mul(Matrix4x4.mul(screenMat, fovScaleMat),
                                            viewMatInv);
    for (int vIdx = 0; vIdx < verticesLength; ++vIdx) {
      tformVertices[vIdx] = tformVertices[vIdx].transformCoord(viewTransform);
    }
    // Clip to screen coordinates.
    for (int ptIdx = 0; ptIdx < verticesLength; ++ptIdx) {
      screenPointsX[ptIdx] = (int)Math.round(tformVertices[ptIdx].x);
      screenPointsY[ptIdx] = (int)Math.round(tformVertices[ptIdx].y);
    }
  }
}

