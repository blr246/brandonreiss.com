/*
 * SceneObject.java
 *
 * A scene geometry object that is transformable and renderable.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;
import com.brandonreiss.math.*;
import java.awt.*;

public abstract class SceneObject {
  private SceneObject parent = null;
  private SceneObject child = null;
  private Matrix4x4 transformLocal = Matrix4x4.matrixIdentity();

  /// <summary> Get reference to the local transform. </summary>
  public Matrix4x4 getTransformLocal() {
    return transformLocal;
  }

  /// <summary> Set reference to the local transform. </summary>
  public void setTransformLocal(Matrix4x4 mat) {
    transformLocal = mat;
  }

  /// <summary> Get the global transform. </summary>
  public Matrix4x4 getTransformGlobal() {
    if (null != parent) {
      return Matrix4x4.mul(parent.getTransformGlobal(), getTransformLocal());
    }
    else {
      return transformLocal;
    }
  }

  /// <summary> Set the object parent. </summary>
  public void setParent(SceneObject parent_) {
    parent = parent_;
    parent.child = this;
  }

  /// <summary> Render a scene object to the graphics. </summary>
  public abstract void render(Renderer r);
}

