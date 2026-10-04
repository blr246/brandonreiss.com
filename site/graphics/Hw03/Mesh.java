/*
 * Mesh.java
 *
 * A generic mesh with temporaries to support rendering.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

/// <summary> A mesh has vertices and faces. </summary>
public class Mesh extends SceneObject {
  // Geometry.
  protected Vector4[] vertices = null;
  protected int[][] faces = null;
  // Rendering.
  protected Vector4[] tformVertices = null;
  protected int[] screenPointsX = null;
  protected int[] screenPointsY = null;
  private boolean wireframe = false;

  /// <summary> Update temporary rendering arrays. </summary>
  protected void updateAllocations() {
    if (null == vertices) {
      tformVertices = null;
      screenPointsX = null;
      screenPointsY = null;
    }
    else {
      final int verticesLength = vertices.length;
      boolean isAlloc = (null != tformVertices) &&
                        (null != screenPointsX) && (null != screenPointsY);
      boolean sizeMatch = isAlloc &&
                          (tformVertices.length == verticesLength) &&
                          (screenPointsX.length == verticesLength) &&
                          (screenPointsY.length == verticesLength);
      if (!isAlloc || !sizeMatch) {
        tformVertices = new Vector4[verticesLength];
        for (int vIdx = 0; vIdx < verticesLength; ++vIdx) {
          tformVertices[vIdx] = Vector4.vectorZero();
        }
        screenPointsX = new int[verticesLength];
        screenPointsY = new int[verticesLength];
      }
    }
  }

  /// <summary> Enable or disable wireframe rendering. </summary>
  public void setWireframe(boolean state) {
    wireframe = state;
  }

  /// <summary> Query mesh wireframe state. </summary>
  public boolean wireframe() {
    return wireframe;
  }

  public Vector4[] getVertices()
  {
    return vertices;
  }

  public int[][] getFaces()
  {
    return faces;
  }

  public Vector4[] getTformVertices()
  {
    return tformVertices;
  }

  public int[] getScreenPointsX()
  {
    return screenPointsX;
  }

  public int[] getScreenPointsY()
  {
    return screenPointsY;
  }

  /// <summary> Render a mesh to the graphics. </summary>
  @Override
  public void render(Renderer r) {
    r.render(this);
  }
}

