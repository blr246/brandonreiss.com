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
  ///<summary>The object parent.</summary>
  public SceneObject parent = null;
  ///<summary>List of object children.</summary>
  public Vector<SceneObject> children = new Vector<SceneObject>(10, 5);
  ///<summary>Local transform used to render this object.</summary>
  ///<remarks>
  ///<para>Perform all object transformation on this matrix.</para>
  ///</remarks>
  public Matrix4x4 transformLocal = Matrix4x4.CreateMatrixIdentity();
  ///<summary>Global transform used to render this object.</summary>
  ///<remarks>
  ///<para>Do not mutate this matrix directly. It is accessible since we
  ///do not want to copy it on access, but it will be updated by the scene
  ///graph every time the tree transformations are udpated.</para>
  ///</remarks>
  public Matrix4x4 transform = Matrix4x4.CreateMatrixIdentity();
  ///<summary>Visibility property.</summary>
  public boolean visible = true;
  ///<summary>The object color.</summary>
  public Color color = Color.black;

  ///<summary>Simple test for parent.</summary>
  public boolean hasParent() {
    return null != parent;
  }

  ///<summary>Get child count.</summary>
  public int numChildren() {
    return children.size();
  }

  ///<summary>Set the object parent.</summary>
  public void setParent(SceneObject parent_) {
    parent_.addChild(this);
  }

  ///<summary>Break link with parent.</summary>
  public void breakParent() {
    if (hasParent()) {
      parent.removeChild(this);
    }
  }

  ///<summary>Add a child object.</summary>
  public void addChild(SceneObject child) { 
    child.breakParent();
    child.parent = this;
    children.add(child);
  }

  ///<summary>Remove a child object.</summary>
  public void removeChild(SceneObject child) { 
    assert(this == child.parent);
    children.remove(child);
    child.parent = null;
  }

  ///<summary>Render a scene object to the graphics.</summary>
  public abstract void render(Renderer r);
}

