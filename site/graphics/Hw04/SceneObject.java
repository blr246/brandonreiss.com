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
import java.util.Vector;

public abstract class SceneObject {
  public SceneObject parent = null;
  public Vector<SceneObject> children = new Vector<SceneObject>(10, 5);
  /// <summary> The local modeling transform. </summary>
  public Matrix4x4 modelTransformLocal = Matrix4x4.matrixIdentity();
  /// <summary> The local animation transform. </summary>
  public Matrix4x4 animTransformLocal = Matrix4x4.matrixIdentity();
  /// <summary> The global transform used to render this object. </summary>
  public Matrix4x4 transform = Matrix4x4.matrixIdentity();

  /// <summary> Simple test for parent. </summary>
  public boolean hasParent() {
    return null != parent;
  }

  /// <summary> Get child count. </summary>
  public int numChildren() {
    return children.size();
  }

  /// <summary> Set the object parent. </summary>
  public void setParent(SceneObject parent_) {
    parent_.addChild(this);
  }

  /// <summary> Break link with parent. </summary>
  public void breakParent() {
    if (hasParent()) {
      parent.removeChild(this);
    }
  }

  /// <summary> Add a child object. </summary>
  public void addChild(SceneObject child) { 
    child.breakParent();
    child.parent = this;
    children.add(child);
  }

  /// <summary> Remove a child object. </summary>
  public void removeChild(SceneObject child) { 
    assert(this == child.parent);
    children.remove(child);
    child.parent = null;
  }

  /// <summary> Render a scene object to the graphics. </summary>
  public abstract void render(Renderer r);
}

