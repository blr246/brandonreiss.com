/*
 * Shape.java
 *
 * A generic mesh with temporaries to support rendering.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

/// <summary> A shape has vertices. </summary>
public abstract class Shape extends SceneObject {
  protected Vector4[] vertices = null;
  protected Vector4[] tformVertices = null;
  protected int[] screenPointsX = null;
  protected int[] screenPointsY = null;

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
        screenPointsX = new int[verticesLength];
        screenPointsY = new int[verticesLength];
      }
    }
  }

  public Vector4[] getVertices()
  {
    return vertices;
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
}

