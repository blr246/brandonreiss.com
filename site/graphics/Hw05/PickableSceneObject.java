/*
 * PickableSceneObject.java
 *
 * Object that receives mouse interaction from the 3d viewport.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.interactive;

import com.brandonreiss.geometry.*;
import com.brandonreiss.math.*;

///<summary>A pickable object in 3d space.</summary>
public class PickableSceneObject extends SceneObject implements Pickable {

  ///<summary>Action to perform when picked.</summary>
  public interface Callback {
    public void onPicked();
    public void onMouseOver();
  }

  ///<summary>The NOP callback.</summary>
  private static Callback defaultCallback = new Callback() {
    public void onPicked() {}
    public void onMouseOver() {}
  };

  ///<summary>Object that is pickable.</summary>
  public SceneObject target = null;
  ///<summary>Callback to execute on pick().</summary>
  private Callback callback = defaultCallback;

  public PickableSceneObject() {}
  public PickableSceneObject(SceneObject o) {
    setTargetObject(o);
  }
  public PickableSceneObject(SceneObject o, Callback callback_) {
    setTargetObject(o);
    setCallback(callback_);
  }

  ///<summary>Set target pickable object.</summary>
  public void setTargetObject(SceneObject o) {
    target = o;
    this.setParent(target);
  }

  ///<summary>Set callback for the Pick() method.</summary>
  ///<returns>The previous callback</summary>
  public Callback setCallback(Callback callback_) {
    Callback callbackPrev = callback;
    callback = callback_;
    return callbackPrev;
  }

  ///<summary>Temporary to invert pickable transform.</summary>
  private Matrix4x4 tformInv = new Matrix4x4();
  ///<summary>Temporary to transform picking ray.</summary>
  private Ray rayTform = new Ray();
  ///<summary>General Vector3 temporary.</summary>
  private Vector3 v3Tmp = new Vector3();
  ///<summary>Unit cube faces.</summary>
  static Vector4[] cubeFaces = new Vector4[] {new Vector4( 1, 0, 0,-1),
                                              new Vector4(-1, 0, 0,-1),
                                              new Vector4( 0, 1, 0,-1),
                                              new Vector4( 0,-1, 0,-1),
                                              new Vector4( 0, 0, 1,-1),
                                              new Vector4( 0, 0,-1,-1),
  };

  ///<summary>Test intersection and return the distance.</summary>
  ///<returns>Positive distance when intersection else -1</summary>
  @Override
  public double testIntersection(Ray r) {
    // Transform the ray with respect to the oobb world. This simplifies
    // immensely the intersection testing.
    tformInv.set(transform).inverse();
    rayTform.pt.set(r.pt).transformCoord(tformInv);
    rayTform.dir.set(r.dir).transformNormal(tformInv).normalize();
    // Check if there is an intersection by finding the closest point from the
    // transformed ray to the origin.
    {
      final double rayTformPlaneW = -rayTform.pt.dot(rayTform.dir);
      v3Tmp.set(rayTform.dir).mul(rayTformPlaneW).add(rayTform.pt);
    }
    // Confirm within uint oobb.
    final boolean intersects = Math.max(v3Tmp.max(), Math.abs(v3Tmp.min())) <= 1;
    if (!intersects) {
      return -1;
    }
    // Shoot the transformed ray at each cube face checking intersection.
    double distanceSq = Double.POSITIVE_INFINITY;
    for (Vector4 face : cubeFaces) {
      // Check ray parallel to face or a backface.
      final double rayTformDotFace = rayTform.dir.dot(face);
      final boolean backface = rayTformDotFace >= 0;
      if (!backface) {
        Util.PointProjectPlaneAlongDir(v3Tmp.set(rayTform.pt), face, rayTform.dir);
        // See if the ray hit the face.
        final boolean hitFace = Math.max(v3Tmp.max(), Math.abs(v3Tmp.min())) <= (1 + 1.0e-4);
        if (hitFace) {
          // Transform the point back into the world and test length squared.
          v3Tmp.transformCoord(transform);
          distanceSq = Math.min(v3Tmp.sub(r.pt).lengthSq(), distanceSq);
        }
      }
    }
    return (Double.POSITIVE_INFINITY == distanceSq) ? -1 : Math.sqrt(distanceSq);
  }

  ///<summary>Invoke the pickable callback when picked.</summary>
  @Override
  public void pick() {
    callback.onPicked();
  }

  ///<summary>Invoke the pickable callback when moused over.</summary>
  @Override
  public void mouseOver() {
    callback.onMouseOver();
  }

  @Override
  public void render(Renderer r) {}
}

