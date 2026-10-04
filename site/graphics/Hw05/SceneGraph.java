/*
 * SceneGraph.java
 *
 * Tree of SceneObjects used to maintain scene heirarchy.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

///<summaryA tree of scene objects to render.</summary>
public class SceneGraph extends SceneObject {
  ///<summary>Default root transform.</summary>
  private Matrix4x4 eye = Matrix4x4.CreateMatrixIdentity();
  ///<summary>Matrix stack to optimize transform computation.</summary>
  private MatrixStack matrixStack = new MatrixStack();

  ///<summary>Update geometry transforms.</summary>
  private void updateTransform(SceneObject t) {
    // Push the root local transform.
    matrixStack.push(t.transformLocal);
    {
      // Set the global transform.
      t.transform.set(matrixStack.top());
      // Update the children.
      for (SceneObject c : t.children) {
        updateTransform(c);
      }
    }
    matrixStack.pop();
  }

  ///<summary>Update geometry transforms.</summary>
  public void updateTransform() {
    // Push the root transform (always identity).
    matrixStack.push(eye);
    {
      // Traverse scene graph and transform it.
      for (SceneObject c : children) {
        updateTransform(c);
      }
    }
    matrixStack.pop();
  }

  ///<summary>Render visible scene graph objects.</summary>
  public void render(Renderer r, SceneObject t) {
    if (t.visible) {
      t.render(r);
    }
    for (SceneObject c : t.children) {
      render(r, c);
    }
  }

  ///<summary>Render visible scene graph objects.</summary>
  @Override
  public void render(Renderer r) {
    // Traverse scene graph and render it.
    for (SceneObject c : children) {
      render(r, c);
    }
  }
}

