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
import java.awt.*;

/// <summary> A mesh has vertices and faces. </summary>
public class Mesh extends SceneObject {
  // Geometry.
  public Vector3[] vertices = null;
  public int[][] faces = null;
  public boolean wireframe = false;

  /// <summary> The color to render the object. </summary>
  public Color color = Color.black;

  /// <summary> Enable or disable wireframe rendering. </summary>
  public void setWireframe(boolean state) {
    wireframe = state;
  }

  /// <summary> Render a mesh to the graphics. </summary>
  @Override
  public void render(Renderer r) {
    r.render(this);
  }
}

