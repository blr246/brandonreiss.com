/*
 * Box.java
 *
 * A 2d box.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

public class Box extends Surface {
  public Box() {
    vertices = new Vector4[] {new Vector4( 0.5,  0.5, 0.0, 1.0),
                              new Vector4(-0.5,  0.5, 0.0, 1.0),
                              new Vector4(-0.5, -0.5, 0.0, 1.0),
                              new Vector4( 0.5, -0.5, 0.0, 1.0),
                              };
    updateAllocations();
  }
}

