/*
 * Cube.java
 *
 * A 3d wireframe cube.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

/// <summary> Wireframe cube. </summary>
public class Cube extends Wireframe {
  public Cube() {
    vertices = new Vector4[] {// Top.
                              new Vector4( 0.5,  0.5, 0.5, 1.0),
                              new Vector4(-0.5,  0.5, 0.5, 1.0),
                              new Vector4(-0.5,  0.5, 0.5, 1.0),
                              new Vector4(-0.5, -0.5, 0.5, 1.0),
                              new Vector4(-0.5, -0.5, 0.5, 1.0),
                              new Vector4( 0.5, -0.5, 0.5, 1.0),
                              new Vector4( 0.5, -0.5, 0.5, 1.0),
                              new Vector4( 0.5,  0.5, 0.5, 1.0),

                              // Uprights.
                              new Vector4( 0.5,  0.5,-0.5, 1.0),
                              new Vector4( 0.5,  0.5, 0.5, 1.0),
                              new Vector4(-0.5,  0.5,-0.5, 1.0),
                              new Vector4(-0.5,  0.5, 0.5, 1.0),
                              new Vector4(-0.5, -0.5,-0.5, 1.0),
                              new Vector4(-0.5, -0.5, 0.5, 1.0),
                              new Vector4( 0.5, -0.5,-0.5, 1.0),
                              new Vector4( 0.5, -0.5, 0.5, 1.0),

                              // Bottom.
                              new Vector4( 0.5,  0.5,-0.5, 1.0),
                              new Vector4(-0.5,  0.5,-0.5, 1.0),
                              new Vector4(-0.5,  0.5,-0.5, 1.0),
                              new Vector4(-0.5, -0.5,-0.5, 1.0),
                              new Vector4(-0.5, -0.5,-0.5, 1.0),
                              new Vector4( 0.5, -0.5,-0.5, 1.0),
                              new Vector4( 0.5, -0.5,-0.5, 1.0),
                              new Vector4( 0.5,  0.5,-0.5, 1.0),
                              };
    updateAllocations();
  }
}

