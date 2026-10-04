/*
 * Plane.java
 *
 * A 3d plane.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

public class Plane extends Mesh {
  public Plane() {
    vertices = new Vector4[] {new Vector4( 1.0, 1.0, 0.0, 1.0),
                              new Vector4(-1.0, 1.0, 0.0, 1.0),
                              new Vector4(-1.0,-1.0, 0.0, 1.0),
                              new Vector4( 1.0,-1.0, 0.0, 1.0),
    };
    faces = new int[][] {{0, 1, 2, 3}};
    updateAllocations();
  }
}

