/*
 * Pickable.java
 *
 * Object that receives mouse interaction from the 3d viewport.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.interactive;

import com.brandonreiss.math.Ray;

///<summary>A pickable object in 3d space.</summary>
public interface Pickable {
  ///<summary>Test intersection and return the distance.</summary>
  ///<returns>Positive distance when intersection else -1</summary>
  public double testIntersection(Ray r);
  ///<summary>Pick this object.</summary>
  public void pick();
  ///<summary>Mouse over this object.</summary>
  public void mouseOver();
}

