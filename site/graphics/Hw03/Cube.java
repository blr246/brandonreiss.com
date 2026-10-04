/*
 * Cube.java
 *
 * A 3d cube.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

public class Cube extends Mesh {
  public Cube() {
    vertices = new Vector4[] {// Top.
                              new Vector4( 1.0, 1.0, 1.0, 1.0),
                              new Vector4(-1.0, 1.0, 1.0, 1.0),
                              new Vector4(-1.0,-1.0, 1.0, 1.0),
                              new Vector4( 1.0,-1.0, 1.0, 1.0),
                              // Right.
                              new Vector4( 1.0, 1.0, 1.0, 1.0),
                              new Vector4( 1.0,-1.0, 1.0, 1.0),
                              new Vector4( 1.0,-1.0,-1.0, 1.0),
                              new Vector4( 1.0, 1.0,-1.0, 1.0),
                              // Front.
                              new Vector4( 1.0,-1.0, 1.0, 1.0),
                              new Vector4(-1.0,-1.0, 1.0, 1.0),
                              new Vector4(-1.0,-1.0,-1.0, 1.0),
                              new Vector4( 1.0,-1.0,-1.0, 1.0),
                              // Left.
                              new Vector4(-1.0,-1.0, 1.0, 1.0),
                              new Vector4(-1.0, 1.0, 1.0, 1.0),
                              new Vector4(-1.0, 1.0,-1.0, 1.0),
                              new Vector4(-1.0,-1.0,-1.0, 1.0),
                              // Back.
                              new Vector4(-1.0, 1.0, 1.0, 1.0),
                              new Vector4( 1.0, 1.0, 1.0, 1.0),
                              new Vector4( 1.0, 1.0,-1.0, 1.0),
                              new Vector4(-1.0, 1.0,-1.0, 1.0),
                              // Bottom.
                              new Vector4( 1.0, 1.0,-1.0, 1.0),
                              new Vector4( 1.0,-1.0,-1.0, 1.0),
                              new Vector4(-1.0,-1.0,-1.0, 1.0),
                              new Vector4(-1.0, 1.0,-1.0, 1.0),
    };
    faces = new int[][] {{ 0,  1,  2,  3},
                         { 4,  5,  6,  7},
                         { 8,  9, 10, 11},
                         {12, 13, 14, 15},
                         {16, 17, 18, 19},
                         {20, 21, 22, 23},
    };
    updateAllocations();
  }
}

