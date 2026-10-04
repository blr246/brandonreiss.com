/*
 * SceneGraph.java
 *
 * Tree of SceneObjects used to maintain scene heirarchy.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
import com.brandonreiss.geometry.*;
import com.brandonreiss.math.*;

/// <summary> A tree of scene objects to render. </summary>
public class SceneGraph extends SceneObject {
  public boolean showAxes = true;

  private MatrixStack matrixStack = new MatrixStack();
  private Axes root = new Axes();

  /// <summary> Render the scene graph rooted at the given object. </summary>
  private void renderSubtree(SceneObject t, Renderer r) {
    //System.out.println(matrixStack.size() + " : renderSubtree()");
    // Push the root animation transform.
    matrixStack.push(t.animTransformLocal);
    {
      // Model the root.
      matrixStack.push(t.modelTransformLocal);
      {
        // Set transform and render the root.
        t.transform = matrixStack.top();
        t.render(r);
      }
      matrixStack.pop();
      // Render the children.
      for (SceneObject s : t.children) {
        renderSubtree(s, r);
      }
    }
    matrixStack.pop();
  }

  /// <summary> Render the scene graph. </summary>
  @Override
  public void render(Renderer r) {
    //System.out.println(matrixStack.size() + " : render()");
    // Push the root transform (always identity).
    matrixStack.push(transform);
    {
      if (showAxes) {
        renderSubtree(root, r);
      }
      // Traverse scene graph and render it.
      for (SceneObject s : children) {
        renderSubtree(s, r);
      }
    }
    matrixStack.pop();
  }
}

