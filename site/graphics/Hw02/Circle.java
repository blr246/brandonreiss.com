/*
 * Circle.java
 *
 * A circle shape.
 *
 * 2012 Brandon Reiss. All Rights Reserved. Do not duplicate or distribute
 * without the author's consent.
*/
package com.brandonreiss.geometry;

import com.brandonreiss.math.*;

public class Circle extends Surface {
  public final int DEFAULT_STEPS = 25;

  public Circle() {
    makeVertices(DEFAULT_STEPS);
  }

  public Circle(int steps) {
    makeVertices(steps);
  }

  private void makeVertices(int steps) {
    vertices = new Vector4[steps];
    final double twoPi = 2.0 * Math.PI;
    final double stepSize = twoPi / steps;
    for (int step = 0; step < steps; ++step) {
      final double theta = step * stepSize;
      vertices[step] = new Vector4(0.5 * Math.cos(theta),
                                   0.5 * Math.sin(theta), 0.0, 1.0);
    }
    updateAllocations();
  }
}

